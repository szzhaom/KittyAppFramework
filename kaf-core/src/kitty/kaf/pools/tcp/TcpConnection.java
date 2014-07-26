package kitty.kaf.pools.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.pools.Connection;
import kitty.kaf.pools.ConnectionPool;

/**
 * 基于Tcp协议的连接
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
abstract public class TcpConnection extends Connection {
	protected InetSocketAddress address;
	protected Socket socket;
	protected int connectTimeout;
	protected int dataTimeout;
	protected int soTimeout;
	protected boolean noDelay;
	protected DataReadStream readStream;
	protected DataWriteStream writeStream;

	public TcpConnection() {
		super();
	}

	public TcpConnection(InetSocketAddress address, int connectTimeout, int dataTimeout) {
		super();
		this.address = address;
		this.connectTimeout = connectTimeout;
		this.dataTimeout = dataTimeout;
	}

	public TcpConnection(ConnectionPool<?> pool) {
		super(pool);
	}

	public TcpConnection(ConnectionPool<?> pool, InetSocketAddress address, int connectTimeout, int dataTimeout) {
		super(pool);
		this.address = address;
		this.connectTimeout = connectTimeout;
		this.dataTimeout = dataTimeout;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	/**
	 * 获取连接超时时间，以毫秒为单位。
	 * 
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * 设置连接超时时间，以毫秒为单位。
	 * 
	 * @param timeout
	 *            连接超时时间
	 */
	public void setConnectTimeout(int timeout) {
		this.connectTimeout = timeout;
	}

	/**
	 * 获取数据超时时间，以毫秒为单位。
	 * 
	 */
	public int geDataTimeout() {
		return dataTimeout;
	}

	/**
	 * 设置数据超时时间，以毫秒为单位。
	 * 
	 * @param timeout
	 *            数据超时时间
	 * @throws SocketException
	 *             如果套接字操作失败
	 */
	public void setDataTimeout(int timeout) throws SocketException {
		this.dataTimeout = timeout;
		if (readStream != null)
			readStream.setTimeout(timeout);
		if (writeStream != null)
			writeStream.setTimeout(timeout);
		if (socket != null)
			socket.setSoTimeout(timeout);
	}

	@Override
	public void open() throws ConnectException {
		socket = new Socket();
		try {
			socket.connect(address, connectTimeout);
			socket.setSoTimeout(soTimeout);
			socket.setTcpNoDelay(isNoDelay());
			readStream = new DataReadStream(socket.getInputStream(), dataTimeout);
			writeStream = new DataWriteStream(socket.getOutputStream(), dataTimeout);
			setLastAliveTime(System.currentTimeMillis());
		} catch (IOException e) {
			throw new ConnectException(e);
		}
	}

	@Override
	public boolean isClosed() {
		return socket == null || socket.isClosed();
	}

	@Override
	public void forceClose() {
		if (!isClosed()) {
			readStream = null;
			writeStream = null;
			try {
				socket.close();
			} catch (Throwable e) {
			}
		}
	}

	/**
	 * 获取连接的套接字对象
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * 获取数据读取流
	 */
	public DataReadStream getReadStream() {
		return readStream;
	}

	/**
	 * 获取数据写入流
	 */
	public DataWriteStream getWriteStream() {
		return writeStream;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public boolean isNoDelay() {
		return noDelay;
	}

	public void setNoDelay(boolean noDelay) {
		this.noDelay = noDelay;
	}

	public int getDataTimeout() {
		return dataTimeout;
	}

}
