package kitty.kaf.cache.clients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kitty.kaf.cache.ICacheClient;
import kitty.kaf.io.ValueObject;
import kitty.kaf.pools.ConnectionPool;
import kitty.kaf.pools.HashedConnectionPoolList;

/**
 * 基于统一哈希的分布式缓存客户端
 * 
 * @author 赵明
 * @version 1.0
 */
public class DistributedCacheClient implements ICacheClient {
	HashedConnectionPoolList<ICacheClientConnection, ConnectionPool<ICacheClientConnection>> poolList;
	Object caller;
	CacheBytesObjectSerializer serializer = new CacheBytesObjectSerializer();

	public DistributedCacheClient() {
	}

	public DistributedCacheClient(
			HashedConnectionPoolList<ICacheClientConnection, ConnectionPool<ICacheClientConnection>> poolList,
			Object caller) {
		super();
		this.poolList = poolList;
		this.caller = caller;
	}

	@Override
	public void set(String key, Object value, Date expiry) throws IOException, InterruptedException {
		ICacheClientConnection con = poolList.getConnection(caller, key);
		try {
			con.set(key, serializer.objectToBytes(value), expiry);
		} finally {
			con.close();
		}
	}

	@Override
	public Object get(String key) throws IOException, InterruptedException {
		ICacheClientConnection con = poolList.getConnection(caller, key);
		try {
			return serializer.bytesToObject((CacheBytesValue) con.get(key));
		} finally {
			con.close();
		}
	}

	@Override
	public void get(List<String> keys, Map<String, Object> map) throws InterruptedException, IOException {
		if (keys == null || keys.size() == 0)
			return;
		Map<ConnectionPool<ICacheClientConnection>, Collection<Integer>> poolMap = poolList.getConnectionPools(keys);
		Iterator<ConnectionPool<ICacheClientConnection>> it = poolMap.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<ICacheClientConnection> pool = it.next();
			Collection<Integer> c = poolMap.get(pool);
			List<String> ks;
			if (c == null) {
				ks = keys;
			} else {
				ks = new ArrayList<String>();
				Iterator<Integer> iit = c.iterator();
				while (iit.hasNext())
					ks.add(keys.get(iit.next()));
			}
			ICacheClientConnection con = pool.getConnection(caller);
			Map<String, CacheBytesValue> map1 = new HashMap<String, CacheBytesValue>();
			try {
				con.get(keys, map1);
			} finally {
				con.close();
			}
			for (String k : map1.keySet()) {
				map.put(k, serializer.bytesToObject((CacheBytesValue) map1.get(k)));
			}
		}
	}

	@Override
	public Long incrdecr(String key, long stepValue) throws IOException, InterruptedException {
		ICacheClientConnection con = poolList.getConnection(caller, key);
		try {
			ValueObject<Long> value = new ValueObject<Long>();
			if (con.incrdecr(key, stepValue, value)) {
				return value.getValue();
			} else {
				set(key, String.valueOf(stepValue), null);
				return stepValue;
			}
		} finally {
			con.close();
		}
	}

	@Override
	public boolean delete(String key) throws IOException, InterruptedException {
		ICacheClientConnection con = poolList.getConnection(caller, key);
		try {
			return con.delete(key);
		} finally {
			con.close();
		}
	}

	@Override
	public void delete(Object[] keys) throws IOException, InterruptedException {
		Map<ConnectionPool<ICacheClientConnection>, Collection<Integer>> map = poolList.getConnectionPools(keys);
		Iterator<ConnectionPool<ICacheClientConnection>> it = map.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<ICacheClientConnection> pool = it.next();
			ICacheClientConnection con = pool.getConnection(caller);
			try {
				Collection<Integer> c = map.get(pool);
				if (c == null) {
					con.delete(keys);
				} else {
					Object[] ks = new Object[c.size()];
					int j = 0;
					for (Integer i : c)
						ks[j++] = keys[i];
					con.delete(ks);
				}
			} finally {
				con.close();
			}
		}
	}

}
