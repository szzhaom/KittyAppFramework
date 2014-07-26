package kitty.kaf.cache.clients.memcached;

import org.w3c.dom.Element;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.logging.Logger;
import kitty.kaf.pools.tcp.TcpConnectionPool;

/**
 * Memcached缓存连接池
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class MemcachedConnectionPool extends TcpConnectionPool<MemcachedConnection> {
	final static Logger logger = Logger.getLogger(MemcachedConnectionPool.class);

	public MemcachedConnectionPool(Element config) {
		super(config);
	}

	@Override
	protected MemcachedConnection createConnection() throws ConnectException {
		return new MemcachedConnection(this);
	}

}
