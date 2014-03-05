package kitty.kaf.pools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.logging.Logger;
import kitty.kaf.util.DateTime;
import kitty.kaf.watch.WatchTask;
import kitty.kaf.watch.Watcher;

/**
 * 连接池。一个连接池内可以拥有多个连接，并可维护这些连接的状态，不用每次都重新创建连接。<br>
 * 派生类实现createConntection()接口，创建实际连接。
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * @param <C>
 *            连接类别
 */
abstract public class ConnectionPool<C extends IConnection> implements
		WatchTask {
	private final static Logger logger = Logger
			.getLogger(ConnectionPool.class);
	/**
	 * 连接池名称
	 */
	protected volatile String name;
	/**
	 * 最大连接数目，默认为30个
	 */
	protected volatile int maxConnectionSize = 30;
	/**
	 * 最小连接数目，默认为0个。超过此数量的超时未使用的连接，将被移除
	 */
	protected volatile int minConnectionSize = 0;
	/**
	 * 连接池使用次数
	 */
	protected volatile long usageCount = 0;
	/**
	 * 空闲连接列表
	 */
	ArrayList<C> idleConnections = new ArrayList<C>();
	/**
	 * 工作连接列表
	 */
	ArrayList<C> workConnections = new ArrayList<C>();
	/**
	 * 最后的活动时间
	 */
	protected volatile long lastAliveTime = System.currentTimeMillis();
	/**
	 * 连接池当前是否可用
	 */
	protected volatile boolean available;
	/**
	 * 发送保持连接活动的消息时间间隔，以毫秒为单位
	 */
	protected volatile int keepAliveInterval = 60 * 1000;
	/**
	 * 连接超时。以毫秒为单位
	 */
	protected volatile int connectionTimeout;
	/**
	 * 最长空闲时限，超过此空闲时间没有使用的连接，将被移除。以毫秒为单位
	 */
	protected volatile int idleMaximumPeriod = 180 * 10000;
	/**
	 * 最长工作时限，超时未归还的连接，将被告警。以毫秒为单位
	 */
	protected volatile int workMaximumPeriod = 180 * 1000;
	/**
	 * 连接池守护者
	 */
	private Watcher watcher;
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * 创建一个连接
	 * 
	 * @return 新创建的连接
	 */
	abstract protected C createConnection() throws ConnectException;

	/**
	 * 销毁一个连接
	 * 
	 * @param c
	 */
	abstract protected void disposeConnection(C c);

	/**
	 * 获取连接URL
	 * 
	 * @return 连接URL
	 */
	abstract public Object getConnectionUrl();

	/**
	 * 构建一个连接池
	 * 
	 * @param name
	 *            连接池名称
	 * @param minConnectionSize
	 *            最小连接数
	 * @param maxConnectionSize
	 *            最大的连接数
	 * @param connectionTimeout
	 *            连接超时时间，以毫秒为单位
	 */
	public ConnectionPool(String name, int minConnectionSize,
			int maxConnectionSize, int connectionTimeout) {
		super();
		this.name = name;
		this.maxConnectionSize = maxConnectionSize;
		this.minConnectionSize = minConnectionSize;
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * 向list中的每个连接发送保持连接活动的消息
	 * 
	 * @param list
	 *            需要保持的连接列表
	 */
	protected void doKeepAlive(List<C> list) {
		Iterator<C> it = list.iterator();
		while (it.hasNext()) {
			try {
				C con = it.next();
				double times = DateTime.milliSecondsBetween(
						System.currentTimeMillis(), con.getLastAliveTime());
				if (getKeepAliveInterval() > 0
						&& times > getKeepAliveInterval())
					con.keepAlive();
			} catch (Throwable e) {
			}
		}
	}

	/**
	 * 移除长时间未使用的连接
	 */
	protected void removeIdleOverTimeConnections() {
		List<C> ls = new ArrayList<C>();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			ls.addAll(idleConnections);
		} finally {
			lock.unlock();
		}
		int c = ls.size() - getMinConnectionSize();
		long now = System.currentTimeMillis();
		for (int i = 0; i < c; i++) {
			C con = ls.get(i);
			try {
				long times = now - con.getLastCallTime();
				if (times > getIdleMaximumPeriod()) {
					logger.debug(con + ": Not used for a long time, removed.");
					disposeConnection(con);
					removeConnection(con);
				}
			} catch (Throwable e) {
			}
		}
		ls.clear();
		ls = null;
	}

	/**
	 * 强制移除长时间未归还的连接
	 */
	protected void removeWorkOverTimeConnections() {
		List<C> ls = new ArrayList<C>();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			ls.addAll(workConnections);
		} finally {
			lock.unlock();
		}
		int c = ls.size();
		long now = System.currentTimeMillis();
		for (int i = 0; i < c; i++) {
			C con = ls.get(i);
			try {
				long times = now - con.getLastCallTime();
				if (times > getWorkMaximumPeriod()) {
					logger.debug(con
							+ ": Not returned for a long time, removed.");
					disposeConnection(con);
					removeConnection(con);
				}
			} catch (Throwable e) {
			}
		}
		ls.clear();
		ls = null;
	}

	protected void keepAlive() {
		List<C> ls = new ArrayList<C>();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			ls.addAll(idleConnections);
		} finally {
			lock.unlock();
		}
		doKeepAlive(ls);
		ls.clear();
		ls = null;
	}

	/**
	 * 监视连接池中的连接：<br>
	 * 1. 移除长时间未使用的空闲连接;<br>
	 * 2. 向空闲连接发送保持的心跳消息;<br>
	 * 3. 对于长时间未归还的连接告警
	 */
	public void watch() {
		removeIdleOverTimeConnections();
		removeWorkOverTimeConnections();
		keepAlive();
	}

	/**
	 * 获取一个连接
	 * 
	 * @param caller
	 *            连接调用者
	 * @return 如果获取成功，则返回获取的连接对象，否则，返回null
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public C getConnection(Object caller) throws InterruptedException,
			ConnectException {
		usageCount++;
		C o = getIdleConnection();
		if (o != null) {
			o.setCaller(caller);
			return o;
		}
		o = getNewConnection();
		if (o != null) {
			o.setCaller(caller);
			return o;
		}

		long time = System.currentTimeMillis();
		while (System.currentTimeMillis() - time < connectionTimeout) {
			Thread.sleep(100);
			o = getIdleConnection();
			if (o != null) {
				o.setCaller(caller);
				return o;
			}
		}
		debug("Not enough connections");
		throw new ConnectException("Not enough connections");
	}

	/**
	 * 获取一个新连接
	 * 
	 * @return 新获取的连接
	 * @throws ConnectException
	 */
	protected C getNewConnection() throws ConnectException {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int c = idleConnections.size() + workConnections.size();
			if (c < maxConnectionSize) {
				C o = createConnection();
				workConnections.add(o);
				debug("Get a new connection");
				return o;
			} else
				return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获得连接总数
	 */
	protected int getConnectionCount() {
		int c;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			c = workConnections.size() + idleConnections.size();
		} finally {
			lock.unlock();
		}
		return c;
	}

	/**
	 * 获取空闲连接总数
	 * 
	 */
	protected int getIdleConnectionCount() {
		int c;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			c = idleConnections.size();
		} finally {
			lock.unlock();
		}
		return c;
	}

	/**
	 * 获取工作连接总数
	 * 
	 */
	protected int getWorkCount() {
		int c;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			c = workConnections.size();
		} finally {
			lock.unlock();
		}
		return c;
	}

	/**
	 * 获取空闲连接
	 * 
	 * @return 如果有空闲连接，则返回，否则，返回null
	 */
	protected C getIdleConnection() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (idleConnections.size() > 0) {
				C o = idleConnections.remove(0);
				workConnections.add(o);
				debug("Get a idle connection");
				return o;
			} else
				return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 归还连接
	 * 
	 * @param o
	 *            要归还的连接
	 */
	@SuppressWarnings("unchecked")
	public boolean returnConnection(Object o) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (workConnections.remove(o)) {
				idleConnections.add((C) o);
				((C) o).setCaller(null);
				debug("Return a connection");
				return true;
			} else
				return false;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 移除连接
	 * 
	 * @param o
	 *            要移除的连接
	 */
	@SuppressWarnings("unchecked")
	public void removeConnection(Object o) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			((C) o).setCaller(null);
			workConnections.remove(o);
			idleConnections.remove(o);
			debug("Remove a connection");
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取连接池使用次数
	 * 
	 */
	public long getUsageCount() {
		return usageCount;
	}

	public String toString() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int works = workConnections.size();
			int idles = idleConnections.size();
			return "({busy=" + works + ",idle=" + idles + ",max="
					+ maxConnectionSize + ",total=" + (works + idles) + "})";

		} finally {
			lock.unlock();
		}
	}

	public void debug(String msg) {
		if (logger.isDebugEnabled()) {
			logger.debug(toString() + ": " + msg);
		}
	}

	public void debug(String msg, Throwable e) {
		if (logger.isDebugEnabled()) {
			logger.error(toString() + ": " + msg, e);
		}
	}

	/**
	 * 获取连接最后的活动时间
	 */
	public long getLastAliveTime() {
		return lastAliveTime;
	}

	/**
	 * 设置连接最后的活动时间
	 */
	public void setLastAliveTime(long lastAliveTime) {
		this.lastAliveTime = lastAliveTime;
	}

	/**
	 * 获取连接池当前是否可用
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * 设置连接池当前是否可用
	 */
	public void setAvailable(boolean available) {
		this.available = available;
	}

	/**
	 * 获取连接超时
	 * 
	 * @return 连接超时，以毫秒为单位
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * 清空连接池
	 */
	public void clean() {
		List<C> list = new ArrayList<C>();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			list.addAll(workConnections);
			list.addAll(idleConnections);
		} finally {
			lock.unlock();
		}
		for (C o : list) {
			try {
				disposeConnection(o);
			} catch (Throwable e) {
			}
		}
		list.clear();
		list = null;
	}

	/**
	 * 获取最大连接数
	 * 
	 * @return 最大连接数
	 */
	public int getMaxConnectionSize() {
		return maxConnectionSize;
	}

	/**
	 * 获取发送保持连接活动的消息时间间隔，以毫秒为单位
	 */
	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	/**
	 * 设置发送保持连接活动的消息时间间隔，以毫秒为单位
	 */
	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	/**
	 * 获取空闲超时时间，以毫秒为单位。超时未使用的连接将被移除
	 */
	public int getIdleMaximumPeriod() {
		return idleMaximumPeriod;
	}

	/**
	 * 设置空闲超时时间，以毫秒为单位。超时未使用的连接将被移除
	 */
	public void setIdleMaximumPeriod(int idleTimeout) {
		this.idleMaximumPeriod = idleTimeout;
	}

	/**
	 * 获取工作最大时限，以毫秒为单位。超过时限未归还的连接，将被移除
	 * 
	 */
	public int getWorkMaximumPeriod() {
		return workMaximumPeriod;
	}

	/**
	 * 设置工作最大时限，以毫秒为单位。超过时限未归还的连接，将被移除
	 * 
	 */
	public void setWorkMaximumPeriod(int workMaximumPeriod) {
		this.workMaximumPeriod = workMaximumPeriod;
	}

	/**
	 * 最小连接数目，默认为0个。超过此数量的超时未使用的连接，将被移除
	 */
	public int getMinConnectionSize() {
		return minConnectionSize;
	}

	/**
	 * 获取连接池守护者对象
	 */
	public Watcher getWatcher() {
		return watcher;
	}

	/**
	 * 设置连接池守护者对象
	 * 
	 * @param watcher
	 *            守护者
	 */
	public void setWatcher(Watcher watcher) {
		if (this.watcher != null)
			this.watcher.removeTask(this);
		this.watcher = watcher;
		if (this.watcher != null)
			this.watcher.addTask(this);
	}

	/**
	 * 获取连接池的名称
	 * 
	 */
	public String getName() {
		return name;
	}

}
