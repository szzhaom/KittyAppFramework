package kitty.kaf.cache.clients.memcached;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kitty.kaf.cache.clients.CacheBytesValue;
import kitty.kaf.cache.clients.ICacheClientConnection;
import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.exceptions.DataException;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.io.ValueObject;
import kitty.kaf.logging.Logger;
import kitty.kaf.pools.ConnectionPool;
import kitty.kaf.pools.tcp.TcpConnection;

public class MemcachedConnection extends TcpConnection implements ICacheClientConnection {
	Logger logger = Logger.getLogger(MemcachedConnection.class);
	public static final int MAX_LENGTH = 1024 * 1024;
	// return codes
	static final String VALUE = "VALUE"; // start of value line from
	// server
	static final String STATS = "STAT"; // start of stats line from
	// server
	static final String ITEM = "ITEM"; // start of item line from server
	static final String DELETED = "DELETED"; // successful deletion
	static final String NOTFOUND = "NOT_FOUND"; // record not found for
	// delete or incr/decr
	static final String STORED = "STORED"; // successful store of data
	static final String NOTSTORED = "NOT_STORED"; // data not stored
	static final String OK = "OK"; // success
	static final String END = "END"; // end of data from server

	static final String ERROR = "ERROR"; // invalid command name from
	// client
	static final String CLIENT_ERROR = "CLIENT_ERROR"; // client error
	// in input line
	// - invalid
	// protocol
	static final String SERVER_ERROR = "SERVER_ERROR"; // server error

	static final byte[] B_END = "END\r\n".getBytes();
	static final byte[] B_NOTFOUND = "NOT_FOUND\r\n".getBytes();
	static final byte[] B_DELETED = "DELETED\r\r".getBytes();
	static final byte[] B_STORED = "STORED\r\r".getBytes();
	static final byte[] CRLF = "\r\n".getBytes();
	/**
	 * 保存与服务器的时间偏移量
	 */
	long serverTimeOffset = 0;

	public MemcachedConnection() {
	}

	public MemcachedConnection(ConnectionPool<?> pool) {
		super(pool);
	}

	public MemcachedConnection(ConnectionPool<?> pool, InetSocketAddress address, int connectTimeout, int dataTimeout) {
		super(pool, address, connectTimeout, dataTimeout);
	}

	public MemcachedConnection(InetSocketAddress address, int connectTimeout, int dataTimeout) {
		super(address, connectTimeout, dataTimeout);
	}

	protected void connect() throws IOException {
		try {
			if (isClosed()) {
				open();
			}
		} catch (ConnectException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void open() throws ConnectException {
		MemcachedConnectionPool pool = (MemcachedConnectionPool) getPool();
		socket = new Socket();
		try {
			socket.connect(pool.getAddress(), pool.getConnectionTimeout());
			socket.setSoTimeout(pool.getSoTimeout());
			readStream = new DataReadStream(socket.getInputStream(), dataTimeout);
			writeStream = new DataWriteStream(socket.getOutputStream(), dataTimeout);
			setLastAliveTime(System.currentTimeMillis());
			status();
		} catch (IOException e) {
			throw new ConnectException(e);
		}
	}

	@Override
	public void keepAlive() throws DataException {
		try {
			status();
		} catch (Throwable e) {
		}
	}

	@Override
	public void set(Object key, CacheBytesValue value, Date expiry) throws IOException, InterruptedException {
		if (value.getValue().length > MAX_LENGTH)
			throw new IOException("Set the key fails, exceeds the maximum length limit");
		String cmd = String.format("%s %s %d %d %d\r\n", "set", key, value.getFlags(),
				(expiry == null ? 0 : (expiry.getTime() - serverTimeOffset)) / 1000, value.getValue().length);
		String response = "";
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().write(value.getValue());
				socket.getOutputStream().write(CRLF);
				socket.getOutputStream().flush();
				response = new MemcachedInputStream(socket.getInputStream()).readLine();
				break;
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (NOTSTORED.equals(response)) {
			throw new MemcachedReplyException("not stored");
		} else if (!STORED.equals(response))
			throw new MemcachedReplyException(response);
	}

	@Override
	public CacheBytesValue get(Object key) throws IOException, InterruptedException {
		String response = "";
		boolean error = false;
		byte[] data = null;
		int flags = 0;
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write(("get " + key + "\r\n").getBytes());
				socket.getOutputStream().flush();
				@SuppressWarnings("resource")
				MemcachedInputStream br = new MemcachedInputStream(socket.getInputStream());
				while (true) {
					response = br.readLine();
					if (END.equals(response)) {
						break;
					} else if (response.startsWith(VALUE)) {
						String[] info = response.split(" ");
						flags = Integer.parseInt(info[2]);
						int length = Integer.parseInt(info[3]);
						data = new byte[length];
						br.read(data);
						br.skipLine();
					} else if (ERROR.equals(response) || response.startsWith(CLIENT_ERROR)
							|| response.startsWith(SERVER_ERROR)) {
						error = true;
						break;
					}
				}
				break;
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect: ", e);
			}
		}
		if (error)
			throw new MemcachedReplyException("Failed to obtain key：" + response);
		else if (data != null) {
			return new CacheBytesValue(data, flags);
		} else
			return null;
	}

	@Override
	public void get(List<String> keys, Map<String, CacheBytesValue> map) throws InterruptedException, IOException {
		StringBuffer cmd = new StringBuffer("get");
		for (String key : keys)
			cmd.append(" " + key);
		cmd.append("\r\n");
		boolean error = false;
		String response = "";
		for (int i = 0; i < 2; i++) {
			byte[] data = null;
			map.clear();
			connect();
			try {
				socket.getOutputStream().write(cmd.toString().getBytes());
				socket.getOutputStream().flush();
				@SuppressWarnings("resource")
				MemcachedInputStream br = new MemcachedInputStream(socket.getInputStream());
				while (true) {
					response = br.readLine();
					if (END.equals(response)) {
						break;
					} else if (response.startsWith(VALUE)) {
						String[] info = response.split(" ");
						int flags = Integer.parseInt(info[2]);
						int length = Integer.parseInt(info[3]);
						data = new byte[length];
						br.read(data);
						br.skipLine();
						map.put(info[1], new CacheBytesValue(data, flags));
					} else if (ERROR.equals(response) || response.startsWith(CLIENT_ERROR)
							|| response.startsWith(SERVER_ERROR)) {
						error = true;
						break;
					}
				}
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (error)
			throw new MemcachedReplyException("Failed to obtain keys：" + response);
	}

	@Override
	public boolean incrdecr(Object key, long stepValue, ValueObject<Long> value) throws IOException,
			InterruptedException {
		String cmd = String.format("%s %s %d\r\n", stepValue > 0 ? "incr" : "decr", key, Math.abs(stepValue));
		String response = "";
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().flush();
				response = new MemcachedInputStream(socket.getInputStream()).readLine();
				break;
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (NOTFOUND.equals(response)) {
			return false;
		} else if (response.matches("\\d+")) {
			value.setValue(Long.valueOf(response));
			return true;
		} else
			throw new MemcachedReplyException(response);
	}

	@Override
	public boolean delete(Object key) throws IOException, InterruptedException {
		String cmd = String.format("delete %s %d\r\n", key, 0);
		String response = "";
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().flush();
				response = new MemcachedInputStream(socket.getInputStream()).readLine();
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (DELETED.equals(response))
			return true;
		else if (NOTFOUND.equals(response))
			return false;
		else
			throw new MemcachedReplyException("Failed to delete data: " + response);
	}

	@Override
	public boolean delete(Object[] keys) throws IOException, InterruptedException {
		for (Object k : keys)
			delete(k);
		return true;
	}

	/**
	 * 删除键值
	 * 
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	@SuppressWarnings("resource")
	synchronized public boolean flushAll() throws IOException {
		connect();
		String cmd = String.format("flush_all\r\n");
		String response = "";
		try {
			socket.getOutputStream().write(cmd.getBytes());
			socket.getOutputStream().flush();
			MemcachedInputStream br = new MemcachedInputStream(socket.getInputStream());
			response = br.readLine();
		} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
			forceClose();
			throw e;
		}
		return OK.equals(response);
	}

	/**
	 * 获取版本
	 * 
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	@SuppressWarnings("resource")
	synchronized public String version() throws IOException {
		String response = "";
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write("version\r\n".getBytes());
				socket.getOutputStream().flush();
				MemcachedInputStream br = new MemcachedInputStream(socket.getInputStream());
				response = br.readLine();
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (ERROR.equals(response) || response.startsWith(CLIENT_ERROR) || response.startsWith(SERVER_ERROR))
			throw new MemcachedReplyException("Get Version Failed：" + response);
		return response;
	}

	/**
	 * 取服务器状态
	 * 
	 * @param item
	 *            状态项。为null，则取全部
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	@SuppressWarnings("resource")
	synchronized public List<String> status() throws IOException {
		String response = "";
		List<String> s = new ArrayList<String>();
		boolean error = false;
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write("stats\r\n".getBytes());
				socket.getOutputStream().flush();
				MemcachedInputStream br = new MemcachedInputStream(socket.getInputStream());
				while (true) {
					response = br.readLine();
					if (END.equals(response))
						break;
					else if (ERROR.equals(response) || response.startsWith(CLIENT_ERROR)
							|| response.startsWith(SERVER_ERROR)) {
						error = true;
						break;
					} else {
						s.add(response);
						if (response.startsWith("STAT time ")) {
							long l = Long.valueOf(response.substring(10)) * 1000;
							serverTimeOffset = System.currentTimeMillis() - l;
						}
					}
				}
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (error)
			throw new MemcachedReplyException("Get Status Failed：" + response);
		return s;
	}

	class MemcachedInputStream extends BufferedInputStream {

		public MemcachedInputStream(InputStream in) {
			super(in);
		}

		@Override
		public synchronized int read(byte[] arg0, int arg1, int arg2) throws IOException {
			int ret = super.read(arg0, arg1, arg2);
			if (ret == -1)
				throw new IOException("Connection has been disconnected");
			return ret;
		}

		/**
		 * 确保读入指定长度的数据，填充至缓冲区
		 */
		public int read(byte[] b) throws IOException {
			int leftLen = b.length;
			while (leftLen > 0) {
				int len = super.read(b, b.length - leftLen, leftLen);
				if (len > 0) {
					leftLen -= len;
					if (leftLen <= 0)
						break;
				} else
					throw new IOException("Reached the end of the stream");
			}
			return b.length;
		}

		/**
		 * 读取一行数据
		 * 
		 * @return 读取到的数据
		 * @throws IOException
		 */
		public String readLine() throws IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1];
			byte r = 0, n = 0;
			while (read(b, 0, 1) != -1) {
				r = n;
				n = b[0];
				if (r == '\r' && n == '\n')
					break;
				bos.write(b);
			}
			return bos.toString().trim();
		}

		/**
		 * 跳过一行数据
		 * 
		 * @return 读取到的数据
		 * @throws IOException
		 */
		public void skipLine() throws IOException {
			byte[] b = new byte[1];
			byte r = 0, n = 0;
			while (read(b, 0, 1) != -1) {
				r = n;
				n = b[0];
				if (r == '\r' && n == '\n')
					return;
			}
		}
	}

}
