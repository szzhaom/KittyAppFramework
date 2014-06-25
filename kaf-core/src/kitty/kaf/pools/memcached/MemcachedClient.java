package kitty.kaf.pools.memcached;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kitty.kaf.cache.ICacheClient;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.io.ValueObject;
import kitty.kaf.io.Writable;

/**
 * 缓存客户端
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public class MemcachedClient implements ICacheClient {
	MemcachedConnectionPoolList poolList;
	Object caller;

	public static void main(String[] args) {
		try {
			MemcachedClient c = newInstance(null, "default");
			c.set("aabbcc", "abcdefg", null);
			System.out.println(c.get("aabbcc"));
			System.out.println(c.get("my-first-document"));
		} catch (MemcachedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public MemcachedClient() {
		this.caller = this;
	}

	public MemcachedClient(Object caller) {
		if (caller == null)
			this.caller = this;
		else
			this.caller = caller;
	}

	public MemcachedClient(Object caller, MemcachedConnectionPoolList poolList) {
		super();
		this.caller = caller;
		this.poolList = poolList;
	}

	/**
	 * 新建一个实例
	 * 
	 * @param caller
	 *            调用者
	 * @param name
	 *            分组配置名称
	 * @return 新的缓存客户端实例
	 * @throws MemcachedException
	 */
	public static MemcachedClient newInstance(Object caller, String name) {
		try {
			return MemcachedConnectionFactory.getClientInstance(caller, name);
		} catch (Throwable e) {
			return null;
		}
	}

	public MemcachedConnectionPoolList getPoolList() {
		return poolList;
	}

	/**
	 * 设置键值对
	 * 
	 * @param cmd
	 *            命令名(add,replace,set)
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            服务器的过期时间点
	 * @throws IOException
	 *             如果与缓存服务器通讯出现故障
	 * @throws InterruptedException
	 *             如果连接池被中断
	 */
	protected void set(String cmd, String key, Object value, Date expiry) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			if (value instanceof Writable) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				((Writable) value).writeToStream(new DataWriteStream(out, 3000));
				value = out.toByteArray();
			}
			con.set(cmd, key, value, expiry);
		} finally {
			con.close();
		}
	}

	public void set(String key, Object value, Date expiry) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			con.set("set", key, value, expiry);
		} finally {
			con.close();
		}
	}

	public Object get(String key) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			return con.get(key);
		} finally {
			con.close();
		}
	}

	public void get(List<String> keys, Map<String, Object> map) throws InterruptedException, IOException {
		if (keys == null || keys.size() == 0)
			return;
		Map<MemcachedConnectionPool<MemcachedConnection>, Collection<Integer>> poolMap = poolList
				.getConnectionPools(keys);
		Iterator<MemcachedConnectionPool<MemcachedConnection>> it = poolMap.keySet().iterator();
		while (it.hasNext()) {
			MemcachedConnectionPool<MemcachedConnection> pool = it.next();
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
			MemcachedConnection con = pool.getConnection(caller);
			try {
				con.get(keys, map);
			} finally {
				con.close();
			}
		}
	}

	public Long incrdecr(String key, long stepValue) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			ValueObject<Long> value = new ValueObject<Long>();
			if (con.incrdecr(stepValue > 0 ? "incr" : "decr", key, stepValue, value)) {
				return value.getValue();
			} else
				return null;
		} finally {
			con.close();
		}
	}

	public void flushAll() throws InterruptedException, IOException {
		poolList.flushAll(caller);
	}

	public boolean delete(String key, Date expiry) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			return con.delete(key, expiry);
		} finally {
			con.close();
		}
	}

	public void delete(Object keys[], Date expiry) throws IOException, InterruptedException {
		Map<MemcachedConnectionPool<MemcachedConnection>, Collection<Integer>> map = poolList.getConnectionPools(keys);
		Iterator<MemcachedConnectionPool<MemcachedConnection>> it = map.keySet().iterator();
		while (it.hasNext()) {
			MemcachedConnectionPool<MemcachedConnection> pool = it.next();
			MemcachedConnection con = pool.getConnection(caller);
			try {
				Collection<Integer> c = map.get(pool);
				if (c == null) {
					for (Object k : keys)
						con.delete(k + "", expiry);
				} else {
					for (Integer i : c)
						con.delete(keys[i].toString(), expiry);
				}
			} finally {
				con.close();
			}
		}
	}

}
