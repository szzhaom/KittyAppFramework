package kitty.kaf.pools.memcached;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import kitty.kaf.pools.HashedConnectionPoolList;

public class MemcachedConnectionPoolList
		extends
		HashedConnectionPoolList<MemcachedConnection, MemcachedConnectionPool<MemcachedConnection>> {
	public ConcurrentHashMap<String, String> configMap = new ConcurrentHashMap<String, String>();
	public volatile String executorClass;

	public MemcachedConnectionPoolList(String name) {
		super(name);
	}

	/**
	 * 删除连接池列表中的所有服务器的缓存对象
	 * 
	 * @param caller
	 *            调用者
	 * @throws InterruptedException
	 *             如果线程中断
	 * @throws IOException
	 *             如果与Memcached服务器出现通讯故障
	 */
	public void flushAll(Object caller) throws InterruptedException,
			IOException {
		for (int i = 0; i < size(); i++) {
			MemcachedConnection con = get(i).getConnection(caller);
			try {
				con.flushAll();
			} finally {
				con.close();
			}
		}
	}

}
