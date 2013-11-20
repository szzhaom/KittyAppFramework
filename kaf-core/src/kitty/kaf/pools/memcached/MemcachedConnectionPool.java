package kitty.kaf.pools.memcached;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.pools.tcp.TcpConnectionPool;

public class MemcachedConnectionPool<C extends MemcachedConnection> extends
		TcpConnectionPool<C> {

	public MemcachedConnectionPool(String name, int minConnectionSize,
			int maxConnectionSize, int connectionTimeout) {
		super(name, minConnectionSize, maxConnectionSize, connectionTimeout);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected C createConnection() throws ConnectException {
		C con = (C) new MemcachedConnection(this, address,
				getConnectionTimeout(), getDataTimeout());
		con.serializer = new MemcachedBytesObjectSerializer();
		con.open();
		return con;
	}

}
