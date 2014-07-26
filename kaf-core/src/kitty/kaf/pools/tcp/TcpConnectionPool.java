package kitty.kaf.pools.tcp;

import java.net.InetSocketAddress;

import org.w3c.dom.Element;

import kitty.kaf.helper.StringHelper;
import kitty.kaf.pools.Connection;
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
abstract public class TcpConnectionPool<C extends Connection> extends ConnectionPool<C> {
	protected InetSocketAddress[] addressList;
	protected int dataTimeout;
	protected int soTimeout;
	protected boolean noDelay;

	public TcpConnectionPool(String name, int minConnectionSize, int maxConnectionSize, int connectionTimeout) {
		super(name, minConnectionSize, maxConnectionSize, connectionTimeout);
	}

	public TcpConnectionPool(Element config) {
		super(config);
		if (config.hasAttribute("dataTimeout"))
			dataTimeout = Integer.valueOf(config.getAttribute("dataTimeout")) * 1000;
		if (config.hasAttribute("soTimeout"))
			soTimeout = Integer.valueOf(config.getAttribute("soTimeout")) * 1000;
		// 读取连接字串，可以设置一主多从
		String urls = config.getAttribute("connectionUrls");
		String[] s = StringHelper.splitToStringArray(urls, ";");
		addressList = new InetSocketAddress[s.length];
		int i = 0;
		for (String str : s) {
			String[] ss = StringHelper.splitToStringArray(str, ":");
			addressList[i++] = new InetSocketAddress(ss[0], Integer.valueOf(ss[1]));
		}
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
		return addressList[0].getHostName() + ":" + addressList[0].getPort();
	}

	public InetSocketAddress getAddress() {
		return addressList[0];
	}

	public void setAddress(InetSocketAddress address) {
		if (this.addressList == null)
			this.addressList = new InetSocketAddress[1];
		this.addressList[0] = address;
	}

	public InetSocketAddress[] getAddressList() {
		return addressList;
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
