package kitty.kaf.pools.tcp;

import java.net.InetSocketAddress;

import kitty.kaf.pools.ConnectionPool;

/**
 * tcp连接池
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 * @param <C>
 */
abstract public class TcpConnectionPool<C extends TcpConnection> extends
		ConnectionPool<C> {
	protected InetSocketAddress address;
	protected int dataTimeout;
	protected int soTimeout;
	protected boolean noDelay;

	public TcpConnectionPool(String name, int minConnectionSize,
			int maxConnectionSize, int connectionTimeout) {
		super(name, minConnectionSize, maxConnectionSize, connectionTimeout);
	}

	/**
	 * 获取数据超时，以毫秒为单位
	 */
	public int getDataTimeout() {
		return dataTimeout;
	}

	/**
	 * 设置数据超时，以毫秒为单位
	 */
	public void setDataTimeout(int dataTimeout) {
		this.dataTimeout = dataTimeout;
	}

	@Override
	protected void disposeConnection(C c) {
		c.forceClose();
	}

	@Override
	public Object getConnectionUrl() {
		return address.getHostName() + ":" + address.getPort();
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
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

}
