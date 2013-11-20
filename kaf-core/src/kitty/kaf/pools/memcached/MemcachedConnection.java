package kitty.kaf.pools.memcached;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.io.ValueObject;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.pools.ConnectionPool;
import kitty.kaf.pools.tcp.TcpConnection;

/**
 * memcached连接
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public class MemcachedConnection extends TcpConnection {
	static KafLogger logger = KafLogger
			.getLogger(MemcachedConnection.class);
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
	static final byte[] CR = "\r\n".getBytes();
	MemcachedBytesObjectSerializer serializer = null;

	/**
	 * 构建一个memcached连接
	 * 
	 */
	public MemcachedConnection() {
	}

	public MemcachedConnection(MemcachedBytesObjectSerializer serializer) {
		super();
	}

	public MemcachedConnection(ConnectionPool<?> pool,
			InetSocketAddress address, int connectTimeout, int dataTimeout) {
		super(pool, address, connectTimeout, dataTimeout);
	}

	public MemcachedConnection(ConnectionPool<?> pool) {
		super(pool);
	}

	public MemcachedConnection(InetSocketAddress address, int connectTimeout,
			int dataTimeout) {
		super(address, connectTimeout, dataTimeout);
	}

	/**
	 * 获取序列化器
	 */
	public MemcachedBytesObjectSerializer getSerializer() {
		return serializer;
	}

	/**
	 * 设置序列化器
	 */
	public void setSerializer(MemcachedBytesObjectSerializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * 保持连接
	 */
	synchronized public void keepAlive() {
		if (isClosed())
			return;
		// 空闲5分钟时，断开连接
		if (System.currentTimeMillis() - getLastAliveTime() < 600000)
			return;
		updateLastAliveTime();
		try {
			forceClose();
		} catch (Throwable e) {
		}
	}

	protected void connect() throws IOException {
		try {
			super.open();
		} catch (ConnectException e) {
			throw new IOException(e);
		}
	}

	private void updateLastAliveTime() {
		setLastAliveTime(System.currentTimeMillis());
	}

	/**
	 * 
	 * 存储键值
	 * 
	 * @param cmdname
	 *            存储命令(add/replace/set)
	 * @param key
	 *            键名
	 * @param flags
	 *            标志位
	 * @param value
	 *            设置的字节数组
	 * @return 是否存储成功
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	@SuppressWarnings("resource")
	synchronized public boolean set(String cmdname, String key, int flags,
			byte[] value, int offset, int length, long expiry)
			throws IOException {
		if (value.length > MAX_LENGTH)
			throw new IOException(
					"Set the key fails, exceeds the maximum length limit");
		if (length == 0)
			length = value.length;
		String cmd = String.format("%s %s %d %d %d\r\n", cmdname, key, flags,
				(expiry / 1000), value.length);
		String response = "";
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().write(value, offset, length);
				socket.getOutputStream().write(CR);
				socket.getOutputStream().flush();
				MemcachedInputStream br = new MemcachedInputStream(
						socket.getInputStream());
				response = br.readLine();
				break;
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (STORED.equals(response)) {
			// logger.info("set[key=" + key + " len=" + value.length + "]");
			return true;
		} else if (NOTSTORED.equals(response))
			return false;
		else
			throw new MemcachedException(response);
	}

	/**
	 * 存储键值
	 * 
	 * @param cmdname
	 *            存储命令(add/replace/set)
	 * @param key
	 *            键名
	 * @param flags
	 *            标志位
	 * @param value
	 *            设置的字节数组
	 * @return 是否存储成功
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	public boolean set(String cmdname, String key, int flags, byte[] value,
			int offset, int length, Date expiry) throws IOException {
		return set(cmdname, key, flags, value, offset, length,
				expiry == null ? 0 : expiry.getTime());
	}

	/**
	 * 设置缓存
	 * 
	 * @param cmd
	 *            设置缓存命令(add/replace/set)
	 * @param key
	 *            缓存键值
	 * @param v
	 *            要设置的对象
	 * @param expiry
	 *            过期时间
	 * @return 是否设置成功
	 * @throws IOException
	 *             如果操作失败
	 */
	public boolean set(String cmd, String key, Object v, long expiry)
			throws IOException {
		MemcachedValue mv = serializer.objectToBytes(v);
		return set(cmd, key, mv.getFlags(), mv.getValue(), 0,
				mv.getValue().length, expiry);
	}

	/**
	 * 设置缓存
	 * 
	 * @param cmd
	 *            设置缓存命令(add/replace/set)
	 * @param key
	 *            缓存键值
	 * @param v
	 *            要设置的对象
	 * @param expiry
	 *            过期时间
	 * @return 是否设置成功
	 * @throws IOException
	 *             如果操作失败
	 */
	public boolean set(String cmd, String key, Object v, Date expiry)
			throws IOException {
		return set(cmd, key, v, expiry == null ? 0L : expiry.getTime());
	}

	/**
	 * 增加/减少键值
	 * 
	 * @param cmdname
	 *            命令名(incr,decr)
	 * @param key
	 *            键名
	 * @param inc
	 *            增/减步长
	 * @param value
	 *            返回值
	 * @return true 设置成功
	 * @return false 设置失败，原值不存在
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	@SuppressWarnings("resource")
	synchronized public boolean incrdecr(String cmdname, String key, long inc,
			ValueObject<Long> value) throws IOException {
		String cmd = String.format("%s %s %d\r\n", cmdname, key, inc);
		String response = "";
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().flush();
				MemcachedInputStream br = new MemcachedInputStream(
						socket.getInputStream());
				response = br.readLine();
				break;
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (NOTFOUND.equals(response))
			return false;
		else if (response.matches("\\d+")) {
			value.setValue(Long.parseLong(response));
			return true;
		} else
			throw new MemcachedException(response);
	}

	/**
	 * 获取键值
	 * 
	 * @param key
	 *            键名
	 * @param value
	 *            返回的键值
	 * @return 是否获取成功
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	@SuppressWarnings("resource")
	synchronized public boolean get(String key, ValueObject<Object> value)
			throws IOException {
		logger.debug("get: " + key);
		String response = "";
		boolean error = false;
		byte[] data = null;
		int flags = 0;
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write(
						("get " + key + "\r\n").getBytes());
				socket.getOutputStream().flush();
				MemcachedInputStream br = new MemcachedInputStream(
						socket.getInputStream());
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
					} else if (ERROR.equals(response)
							|| response.startsWith(CLIENT_ERROR)
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
			throw new MemcachedException("Failed to obtain key：" + response);
		else if (data != null) {
			value.setValue(serializer.bytesToObject(new MemcachedValue(data,
					flags)));
			return true;
		} else
			return false;
	}

	/**
	 * 获取键值
	 * 
	 * @param key
	 *            键名
	 * @return 返回的键值对象，找不到key则返回null
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */

	public Object get(String key) throws IOException {
		ValueObject<Object> v = new ValueObject<Object>();
		if (get(key, v))
			return v.getValue();
		else
			return null;
	}

	/**
	 * 获取一个或多个键值
	 * 
	 * @param keys
	 *            键值数组
	 * @param map
	 *            返回的键值map
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	@SuppressWarnings("resource")
	synchronized public void get(Collection<String> keys,
			Map<String, Object> map) throws IOException {
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
				MemcachedInputStream br = new MemcachedInputStream(
						socket.getInputStream());
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
						map.put(info[1], serializer
								.bytesToObject(new MemcachedValue(data, flags)));
					} else if (ERROR.equals(response)
							|| response.startsWith(CLIENT_ERROR)
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
			throw new MemcachedException("Failed to obtain keys：" + response);
	}

	/**
	 * 获取一个或多个键值
	 * 
	 * @param keys
	 *            键值数组
	 * @return 返回的键值map
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	public Map<String, Object> get(Collection<String> keys) throws IOException {
		HashMap<String, Object> ret = new HashMap<String, Object>();
		get(keys, ret);
		return ret;
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
			MemcachedInputStream br = new MemcachedInputStream(
					socket.getInputStream());
			response = br.readLine();
		} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
			forceClose();
			throw e;
		}
		return OK.equals(response);
	}

	/**
	 * 删除
	 * 
	 * @param key
	 *            要删除的键名
	 * @param expiry
	 *            延时的时间，为0则立即删除
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	@SuppressWarnings("resource")
	synchronized public boolean delete(String key, long expiry)
			throws IOException {
		String cmd = String.format("delete %s %d\r\n", key, expiry / 1000);
		String response = "";
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				socket.getOutputStream().write(cmd.getBytes());
				socket.getOutputStream().flush();
				MemcachedInputStream br = new MemcachedInputStream(
						socket.getInputStream());
				response = br.readLine();
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
			throw new MemcachedException("Failed to delete data: " + response);
	}

	/**
	 * 删除
	 * 
	 * @param key
	 *            要删除的键名
	 * @param expiry
	 *            延时的时间，为null则立即删除
	 * @throws IOException
	 *             网络或协议故障时抛出
	 */
	public boolean delete(String key, Date expiry) throws IOException {
		return delete(key, expiry == null ? 0L : expiry.getTime());
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
				MemcachedInputStream br = new MemcachedInputStream(
						socket.getInputStream());
				response = br.readLine();
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (ERROR.equals(response) || response.startsWith(CLIENT_ERROR)
				|| response.startsWith(SERVER_ERROR))
			throw new MemcachedException("Get Version Failed：" + response);
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
	synchronized public String status(String item) throws IOException {
		String response = "";
		StringBuffer buf = new StringBuffer();
		boolean error = false;
		for (int i = 0; i < 2; i++) {
			connect();
			try {
				if (item == null || item.isEmpty())
					socket.getOutputStream().write("stats\r\n".getBytes());
				else
					socket.getOutputStream().write(
							("stats " + item + "\r\n").getBytes());
				socket.getOutputStream().flush();
				MemcachedInputStream br = new MemcachedInputStream(
						socket.getInputStream());
				while (true) {
					response = br.readLine();
					if (END.equals(response))
						break;
					else if (ERROR.equals(response)
							|| response.startsWith(CLIENT_ERROR)
							|| response.startsWith(SERVER_ERROR)) {
						error = true;
						break;
					} else
						buf.append(response + "\r\n");
				}
			} catch (IOException e) { // 视为连接出现问题，关闭连接，下次会自动重新连接
				forceClose();
				if (i == 1)
					throw e;
				logger.debug("connection disconnected,reconnect");
			}
		}
		if (error)
			throw new MemcachedException("Get Status Failed：" + response);
		return buf.toString();
	}

	class MemcachedInputStream extends BufferedInputStream {

		public MemcachedInputStream(InputStream in) {
			super(in);
		}

		@Override
		public synchronized int read(byte[] arg0, int arg1, int arg2)
				throws IOException {
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
