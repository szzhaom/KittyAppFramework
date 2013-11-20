package kitty.kaf.pools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kitty.kaf.logging.KafLogger;

/**
 * 连接池列表，表示一组连接池
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * @param <C>
 */
public class ConnectionPoolList<C extends ConnectionPool<?>> {
	protected class MyConnectionPool {
		C pool;
		int weights;

		public MyConnectionPool(C pool, int weights) {
			super();
			this.pool = pool;
			this.weights = weights;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pool == null) ? 0 : pool.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			MyConnectionPool other = (MyConnectionPool) obj;
			if (pool == null) {
				if (other.pool != null)
					return false;
			} else if (!pool.equals(other.pool))
				return false;
			return true;
		}

		public C getPool() {
			return pool;
		}

		public int getWeights() {
			return weights;
		}
	}

	private static final KafLogger logger = KafLogger
			.getLogger(ConnectionPoolList.class);
	protected CopyOnWriteArrayList<MyConnectionPool> pools = new CopyOnWriteArrayList<MyConnectionPool>();
	/**
	 * 坏连接池列表
	 */
	protected CopyOnWriteArrayList<MyConnectionPool> badPools = new CopyOnWriteArrayList<MyConnectionPool>();
	public String name;

	/**
	 * 构造函数
	 * 
	 * @param name
	 *            连接池名称
	 */
	public ConnectionPoolList(String name) {
		super();
		this.name = name;
	}

	/**
	 * 标记一个连接池为坏的连接池
	 * 
	 * @param badPool
	 *            坏的连接池
	 * @param e
	 *            连接池损坏的原因（异常）
	 */
	public void markBadConnectionPool(C badPool, Throwable e) {
		if (pools.size() > 1) {
			MyConnectionPool p = new MyConnectionPool(badPool, 1);
			int index = pools.indexOf(p);
			if (index > -1) {
				p = pools.remove(index);
				if (p != null) {
					badPools.add(p);
					try {
						p.getPool().clean();
					} catch (Throwable ex) {
					}
				}
			}
		}
		logger.error(badPool.toString() + ": Connection has been damaged: ", e);
	}

	/**
	 * 检查不正常的连接池
	 */
	protected void checkBadConnectionPools() {
		List<MyConnectionPool> ls = new ArrayList<MyConnectionPool>();
		ls.addAll(badPools);
		Iterator<MyConnectionPool> it = ls.iterator();
		while (it.hasNext()) {
			try {
				MyConnectionPool p = it.next();
				IConnection con = p.pool.getConnection(this);
				try {
					con.open();
					// 打开连接成功，则将连接池置为有效连接
					badPools.remove(p);
					add(p);
					logger.debug(p.toString() + ": connection restored.");
				} finally {
					con.close();
				}
			} catch (Throwable e) {
			}
		}
		ls.clear();
		ls = null;
	}

	/**
	 * 检查连接池列表中的全部连接，发送保持连接的命令
	 */
	protected void keepAlive() {
		List<MyConnectionPool> ls = new ArrayList<MyConnectionPool>();
		ls.addAll(pools);
		Iterator<MyConnectionPool> it = ls.iterator();
		while (it.hasNext()) {
			try {
				it.next().pool.keepAlive();
			} catch (Throwable e) {
			}
		}
		ls.clear();
		ls = null;
		checkBadConnectionPools();
	}

	/**
	 * 连接池列表发生变化
	 */
	protected void changed() {
	}

	boolean add(MyConnectionPool e) {
		if (pools.add(e)) {
			changed();
			return true;
		} else
			return false;
	}

	public boolean add(C pool, int weights) {
		MyConnectionPool p = new MyConnectionPool(pool, weights);
		if (pools.add(p)) {
			changed();
			return true;
		} else
			return false;
	}

	public void clear() {
		pools.clear();
		badPools.clear();
		changed();
	}

	public C get(int index) {
		return pools.get(index).pool;
	}

	public int size() {
		return pools.size();
	}
}
