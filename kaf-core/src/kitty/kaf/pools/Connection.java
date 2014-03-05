package kitty.kaf.pools;

import kitty.kaf.logging.Logger;

/**
 * 连接基类
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
abstract public class Connection implements IConnection {
	private static Logger logger = Logger.getLogger(Connection.class);
	/**
	 * 连接所属的连接池
	 */
	ConnectionPool<?> pool;
	/**
	 * 连接最后活动时间
	 */
	long lastAliveTime;
	long lastCallTime;
	long creationTime = System.currentTimeMillis();
	/**
	 * 连接池当前的调用者
	 */
	Object caller;

	public Connection() {
		super();
	}

	/**
	 * 创建一个连接，同时，指定该连接的连接池对象
	 * 
	 * @param pool
	 *            连接池对象
	 */
	public Connection(ConnectionPool<?> pool) {
		super();
		this.pool = pool;
		this.lastCallTime = System.currentTimeMillis();
		this.lastAliveTime = System.currentTimeMillis();
	}

	@Override
	public ConnectionPool<?> getPool() {
		return pool;
	}

	@Override
	public void close() {
		if (pool != null) {
			if (isClosed()) {
				logger.debug("Connection has been disconnected, removed");
				pool.removeConnection(this);
			} else
				pool.returnConnection(this);
		} else
			forceClose();
	}

	/**
	 * 强行关闭连接
	 */
	abstract protected void forceClose();

	@Override
	public long getLastAliveTime() {
		return lastAliveTime;
	}

	protected void setLastAliveTime(long lastAliveTime) {
		this.lastAliveTime = lastAliveTime;
	}

	@Override
	public long getLastCallTime() {
		return lastCallTime;
	}

	void setLastCallTime(long lastCallTime) {
		this.lastCallTime = lastCallTime;
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	public Object getCaller() {
		return caller;
	}

	public void setCaller(Object caller) {
		if (caller != null)
			setLastCallTime(System.currentTimeMillis());
		this.caller = caller;
	}
}
