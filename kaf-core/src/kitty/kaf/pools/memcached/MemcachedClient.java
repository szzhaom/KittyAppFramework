package kitty.kaf.pools.memcached;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.io.Readable;
import kitty.kaf.io.ValueObject;
import kitty.kaf.io.Writable;

/**
 * 缓存客户端
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public class MemcachedClient {
	MemcachedConnectionPoolList poolList;
	Object caller;

	public static void main(String[] args) {
		try {
			MemcachedClient c = newInstance(null, "default");
			c.set("aabbcc", 123, null);
			System.out.println(c.get("aabbcc"));
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
	public void set(String key, Object value, Date expiry) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			con.set("set", key, value, expiry);
		} finally {
			con.close();
		}
	}

	/**
	 * 获取一个键值
	 * 
	 * @param key
	 *            键值数组
	 * @return 返回与key对应的键值，如果key不存在，返回null
	 * @throws IOException
	 *             如果与缓存服务器通讯出现故障
	 * @throws InterruptedException
	 *             如果连接池被中断
	 */
	public Object get(String key) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			return con.get(key);
		} finally {
			con.close();
		}
	}

	/**
	 * 获取一个或多个值，键值可能分布在不同的服务器上
	 * 
	 * @param keys
	 *            要获取的键名数组
	 * @param hashCodes
	 *            指定哈希值列表，用于查找合适的服务器。如果为null，则取键值列表中的各键的哈希值，如果不为null，
	 *            则哈希列表与键列表必须具备一一对应的关系
	 * @param map
	 *            返回的键值map
	 * @param classLoader
	 *            类装载器，通常为null
	 * @throws InterruptedException
	 *             如果线程中断
	 * @throws IOException
	 *             如果与Memcached服务器出现通讯故障
	 * @throws ConnectException
	 *             如果与Memcached服务器连接失败
	 */
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

	/**
	 * 增加计数器
	 * 
	 * @param key
	 *            键
	 * @param initValue
	 *            初始值
	 * @param stepValue
	 *            增量步长
	 * @param hashCode
	 *            指定哈希值，用于查找合适的服务器。如果为0，则默认为键名的哈希值
	 * @return -1，设置失败; >=0,操作成功，返回操作后的值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Long incrdecr(String cmd, String key, long stepValue) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			ValueObject<Long> value = new ValueObject<Long>();
			if (con.incrdecr(cmd, key, stepValue, value)) {
				return value.getValue();
			} else
				return null;
		} finally {
			con.close();
		}
	}

	/**
	 * 删除全部缓存内容
	 * 
	 * @throws InterruptedException
	 *             如果线程中断
	 * @throws IOException
	 *             如果与Memcached服务器出现通讯故障
	 * @throws ConnectException
	 *             如果与Memcached服务器连接失败
	 */
	public void flushAll() throws InterruptedException, IOException {
		poolList.flushAll(caller);
	}

	/**
	 * 删除
	 * 
	 * @throws InterruptedException
	 *             如果线程中断
	 * @throws IOException
	 *             如果与Memcached服务器出现通讯故障
	 * @throws ConnectException
	 *             如果与Memcached服务器连接失败
	 */
	public boolean delete(String key, Date expiry) throws IOException, InterruptedException {
		MemcachedConnection con = poolList.getConnection(caller, key);
		try {
			return con.delete(key, expiry);
		} finally {
			con.close();
		}
	}

	/**
	 * 批量删除，注意，Memcached不支持一次性的批量删除
	 * 
	 * @param keys
	 *            要删除的键
	 * @param expiry
	 *            什么时间点删除，为null则立即删除
	 * @return 是否删除成功
	 * @throws InterruptedException
	 *             如果线程中断
	 * @throws IOException
	 *             如果与Memcached服务器出现通讯故障
	 * @throws ConnectException
	 *             如果与Memcached服务器连接失败
	 */
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

	/**
	 * 获取数据，并转换成对象返回
	 * 
	 * @param key
	 *            数据的key
	 * @param valueClazz
	 *            要返回的对象类
	 * @return 读取的对象
	 * @throws InterruptedException
	 *             如果线程中断
	 * @throws IOException
	 *             如果与Memcached服务器出现通讯故障
	 * @throws ConnectException
	 *             如果与Memcached服务器连接失败
	 */
	public <E extends Readable> E get(String key, Class<E> valueClazz) throws InterruptedException, IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		E cl;
		try {
			cl = valueClazz.newInstance();
			cl.readFromStream(new DataReadStream(new ByteArrayInputStream(b), 3000));
		} catch (IOException e) {
			delete(key, null);
			throw e;
		} catch (Throwable e) {
			delete(key, null);
			throw new IOException(e);
		}
		return cl;
	}

	/**
	 * 装入数据至o中
	 * 
	 * @param key
	 *            数据的key
	 * @param o
	 *            要装入数据的对象类
	 * @return 读取的对象
	 * @throws InterruptedException
	 *             如果线程中断
	 * @throws IOException
	 *             如果与Memcached服务器出现通讯故障
	 * @throws ConnectException
	 *             如果与Memcached服务器连接失败
	 */
	public <E extends Readable> void load(String key, E o) throws InterruptedException, IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return;
		try {
			o.readFromStream(new DataReadStream(new ByteArrayInputStream(b), 3000));
		} catch (IOException e) {
			delete(key, null);
			throw e;
		} catch (Throwable e) {
			delete(key, null);
			throw new IOException(e);
		}
	}

	/**
	 * 获取数据，并转换成对象返回
	 * 
	 * @param key
	 *            数据的key
	 * @param valueClazz
	 *            要返回的对象类
	 * @return 读取的对象
	 * @throws InterruptedException
	 *             如果线程中断
	 * @throws IOException
	 *             如果与Memcached服务器出现通讯故障
	 * @throws ConnectException
	 *             如果与Memcached服务器连接失败
	 */
	public <E extends Readable> Map<String, E> get(List<String> keys, Class<E> valueClazz) throws InterruptedException,
			IOException {
		Map<String, E> rmap = new HashMap<String, E>();
		if (keys == null || keys.size() == 0)
			return rmap;
		Map<String, Object> map = new HashMap<String, Object>();
		get(keys, map);
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			byte[] b = (byte[]) map.get(key);
			E cl;
			try {
				cl = valueClazz.newInstance();
				cl.readFromStream(new DataReadStream(new ByteArrayInputStream(b), 3000));
				rmap.put(key, cl);
			} catch (IOException e) {
				delete(key, null);
				throw e;
			} catch (Throwable e) {
				delete(key, null);
				throw new IOException(e);
			}
		}
		return rmap;
	}

	public List<String> getPacketByteLenStringList(String key) throws IOException, InterruptedException {
		byte[] b = (byte[]) get(key);
		if (b != null) {
			DataReadStream stream = new DataReadStream(new ByteArrayInputStream(b), 3000);
			try {
				return stream.readPacketByteLenStringList();
			} finally {
				stream.getInputStream().close();
			}
		} else
			return null;
	}

	public List<String> getPacketShortLenStringList(String key) throws IOException, InterruptedException {
		byte[] b = (byte[]) get(key);
		if (b != null) {
			DataReadStream stream = new DataReadStream(new ByteArrayInputStream(b), 3000);
			try {
				return stream.readPacketShortLenStringList();
			} finally {
				stream.getInputStream().close();
			}
		} else
			return null;
	}

	public void setPacketByteLenStringList(String key, List<String> ls, Date expiry) throws IOException,
			InterruptedException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataWriteStream stream = new DataWriteStream(out, 3000);
		stream.writePacketByteLenStringList(ls);
		set(key, out.toByteArray(), expiry);
	}

	public void setPacketShortLenStringList(String key, List<String> ls, Date expiry) throws IOException,
			InterruptedException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataWriteStream stream = new DataWriteStream(out, 3000);
		stream.writePacketShortLenStringList(ls);
		set(key, out.toByteArray(), expiry);
	}

	public <E extends Readable> List<E> getReadableList(String key, Class<E> clazz) throws IOException,
			InterruptedException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		List<E> ret = new ArrayList<E>();
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		DataReadStream stream = new DataReadStream(in, 3000);
		int c = stream.readInt();
		for (int i = 0; i < c; i++) {
			try {
				E o = clazz.newInstance();
				o.readFromStream(stream);
				ret.add(o);
			} catch (Throwable e) {
				throw new IOException(e);
			}
		}
		return ret;
	}

	public <E extends Writable> void setWritableList(String key, List<E> ls, Date expiry) throws IOException,
			InterruptedException {
		if (ls == null)
			return;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataWriteStream stream = new DataWriteStream(out, 3000);
		stream.writeInt(ls.size());
		for (E o : ls)
			o.writeToStream(stream);
		set(key, out.toByteArray(), expiry);
	}
}
