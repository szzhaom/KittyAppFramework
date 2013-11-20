package kitty.kaf.pools;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import kitty.kaf.exceptions.ConnectException;

public class HashedConnectionPoolList<C extends IConnection, P extends ConnectionPool<C>>
		extends ConnectionPoolList<P> {
	CopyOnWriteArrayList<Integer> noConsistentList;
	TreeMap<Long, Integer> consistentMap;
	/**
	 * 是否采用统一哈希算法实现负载均衡
	 */
	volatile boolean useConsistentHash;
	/**
	 * 当出现坏连接时，是否自动调用定位规则，如果是，则当出现坏连接时，下次将不再使用，直到连接可用时才重新使用，提高连接的成功率，但会更改主机的访问规则
	 * ，如果不同主机的访问不会改变业务处理，可以设为true
	 */
	volatile boolean adjustForBadConnection;

	public HashedConnectionPoolList(String name) {
		super(name);
	}

	@Override
	public void markBadConnectionPool(P badPool, Throwable e) {
		if (adjustForBadConnection)
			super.markBadConnectionPool(badPool, e);
	}

	@Override
	protected void checkBadConnectionPools() {
		if (adjustForBadConnection)
			super.checkBadConnectionPools();
	}

	private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e);
			}
		}
	};

	/**
	 * 当连接池发生变化（添加，删除）时，调用此函数，重新组织分布式访问
	 */
	protected void changed() {
		if (noConsistentList != null) {
			noConsistentList.clear();
			noConsistentList = null;
		}
		if (consistentMap != null) {
			consistentMap.clear();
			consistentMap = null;
		}
		if (useConsistentHash) { // 使用一致性哈希
			consistentMap = new TreeMap<Long, Integer>();
			int totalWeight = 0;
			for (MyConnectionPool p : pools) {
				totalWeight += p.getWeights();
			}
			int ps = pools.size();
			MessageDigest md5 = MD5.get();
			for (int i = 0; i < ps; i++) {
				MyConnectionPool p = pools.get(i);
				int thisWeight = p.getWeights();

				double factor = Math.floor(((double) (40 * ps * thisWeight))
						/ (double) totalWeight);

				for (long j = 0; j < factor; j++) {
					String host = p.getPool().getConnectionUrl() + "-" + j;
					byte[] d = md5.digest(host.getBytes());
					for (int h = 0; h < 4; h++) {
						Long k = ((long) (d[3 + h * 4] & 0xFF) << 24)
								| ((long) (d[2 + h * 4] & 0xFF) << 16)
								| ((long) (d[1 + h * 4] & 0xFF) << 8)
								| ((long) (d[0 + h * 4] & 0xFF));
						consistentMap.put(k, i);
					}
				}
			}
		} else {
			noConsistentList = new CopyOnWriteArrayList<Integer>();
			for (int i = 0; i < pools.size(); i++) {
				MyConnectionPool p = pools.get(i);
				for (int j = 0; j < p.getWeights(); j++)
					noConsistentList.add(i);
			}
		}

	}

	private static long md5HashingAlg(String key) {
		MessageDigest md5 = MD5.get();
		md5.reset();
		md5.update(key.getBytes());
		byte[] bKey = md5.digest();
		long res = ((long) (bKey[3] & 0xFF) << 24)
				| ((long) (bKey[2] & 0xFF) << 16)
				| ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
		return res;
	}

	private long getHash(String key) {
		if (useConsistentHash) {
			return md5HashingAlg(key);
		} else
			return key.hashCode();
	}

	private int getConnectionPoolIndex(String key) throws ConnectException {
		long hc = getHash(key);
		if (useConsistentHash) {
			Long k = consistentMap.ceilingKey(hc);
			if (k == null)
				k = consistentMap.firstKey();

			return consistentMap.get(k);
		} else {
			return (int) hc % noConsistentList.size();
		}
	}

	private P getConnectionPool(String key) throws ConnectException {
		int index = getConnectionPoolIndex(key);
		if (index < 0)
			index *= -1;
		return get(index);
	}

	/**
	 * 根据指定的key和hashCode，获得连接，如果hashCode=null，则根据key计算一个哈希值代替
	 * 
	 * @param caller
	 *            连接调用者
	 * @param key
	 *            关键字，根据关键字按内部哈希算法，算出哈希值，决定访问哪个连接池，实现分布式访问
	 * @return 获取的连接
	 * @throws ConnectException
	 *             如果获取连接失败
	 * @throws InterruptedException
	 *             如果线程中断
	 */
	public C getConnection(Object caller, String key) throws ConnectException,
			InterruptedException {
		int size = size();
		if (size == 0)
			throw new ConnectException("没有可用的连接");
		else if (size == 1) {
			return get(0).getConnection(caller);
		} else {
			return getConnectionPool(key).getConnection(caller);
		}
	}

	/**
	 * 根据指定的关键字列表和哈希列表，获取连接池Map。
	 * 
	 * @param keys
	 *            关键字列表
	 * @param hashCodes
	 *            哈希值列表
	 * @return 连接池MAP
	 * @throws IOException
	 */
	public Map<P, Collection<Integer>> getConnectionPools(List<String> keys)
			throws ConnectException {
		int size = size();
		if (size == 0)
			throw new ConnectException("没有可用的连接池");
		else {
			HashMap<P, Collection<Integer>> map = new HashMap<P, Collection<Integer>>();
			if (size == 1) {
				map.put(get(0), null);
			} else {
				if (keys.size() == 1) {
					map.put(getConnectionPool(keys.get(0)), null);
				} else {
					for (int i = 0; i < keys.size(); i++) {
						P pool = getConnectionPool(keys.get(i));
						Collection<Integer> c = map.get(pool);
						if (c != null)
							c.add(i);
						else {
							c = new ArrayList<Integer>();
							c.add(i);
							map.put(pool, c);
						}
					}
				}
			}
			return map;
		}
	}

	public Map<P, Collection<Integer>> getConnectionPools(Object[] keys)
			throws ConnectException {
		int size = size();
		if (size == 0)
			throw new ConnectException("没有可用的连接池", null);
		else {
			HashMap<P, Collection<Integer>> map = new HashMap<P, Collection<Integer>>();
			if (size == 1) {
				map.put(get(0), null);
			} else {
				if (keys.length == 1) {
					map.put(getConnectionPool(keys[0].toString()), null);
				} else {
					for (int i = 0; i < keys.length; i++) {
						P pool = getConnectionPool(keys[i].toString());
						Collection<Integer> c = map.get(pool);
						if (c != null)
							c.add(i);
						else {
							c = new ArrayList<Integer>();
							c.add(i);
							map.put(pool, c);
						}
					}
				}
			}
			return map;
		}
	}

	public Map<P, Collection<String>> getConnectionPoolsMapKey(List<String> keys)
			throws ConnectException {
		int size = size();
		if (size == 0)
			throw new ConnectException("没有可用的连接池", null);
		else {
			HashMap<P, Collection<String>> map = new HashMap<P, Collection<String>>();
			if (size == 1) {
				map.put(get(0), null);
			} else {
				if (keys.size() == 1) {
					map.put(getConnectionPool(keys.get(0).toString()), null);
				} else {
					for (int i = 0; i < keys.size(); i++) {
						P pool = getConnectionPool(keys.get(i));
						Collection<String> c = map.get(pool);
						if (c != null)
							c.add(keys.get(i));
						else {
							c = new ArrayList<String>();
							c.add(keys.get(i));
							map.put(pool, c);
						}
					}
				}
			}
			return map;
		}
	}

	/**
	 * 当出现坏连接时，是否自动调用定位规则，如果是，则当出现坏连接时，下次将不再使用，直到连接可用时才重新使用，提高连接的成功率，但会更改主机的访问规则
	 * ，如果不同主机的访问不会改变业务处理，可以设为true
	 */
	public boolean isAdjustForBadConnection() {
		return adjustForBadConnection;
	}

	/**
	 * 当出现坏连接时，是否自动调用定位规则，如果是，则当出现坏连接时，下次将不再使用，直到连接可用时才重新使用，提高连接的成功率，但会更改主机的访问规则
	 * ，如果不同主机的访问不会改变业务处理，可以设为true
	 * 
	 * @param adjustForBadConnection
	 *            是否自动处理坏连接
	 */
	public void setAdjustForBadConnection(boolean adjustForBadConnection) {
		this.adjustForBadConnection = adjustForBadConnection;
	}

	/**
	 * 是否采用统一哈希算法，自动定位服务器
	 * 
	 */
	public boolean isUseConsistentHash() {
		return useConsistentHash;
	}

	/**
	 * 设置是否采用统一哈希算法，自动定位服务器
	 * 
	 * @param useConsistentHash
	 */
	public void setUseConsistentHash(boolean useConsistentHash) {
		if (useConsistentHash == this.useConsistentHash)
			return;
		this.useConsistentHash = useConsistentHash;
		changed();
	}
}
