package kitty.kaf.pools;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.exceptions.DataException;

/**
 * 连接对象，所有不同的连接对象基数，描述一个连接的基本行为
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public interface IConnection {

	/**
	 * 打开一个连接
	 * 
	 * @throws ConnectException
	 *             打开连接失败时，抛出此错误
	 */
	void open() throws ConnectException;

	/**
	 * 关闭或归还一个连接 <br>
	 * <font color=red>不允许抛出任何错误</font>
	 */
	void close();

	/**
	 * 测试当前连接是否打开
	 * 
	 * @return true - 连接已经关闭<br>
	 *         false - 连接已经打开
	 */
	boolean isClosed();

	/**
	 * 保持连接的活动状态，通常表现为发送一个心跳消息
	 * 
	 * @throws DataException
	 *             通讯失败时，抛出此异常
	 */
	void keepAlive() throws DataException;

	/**
	 * 获取连接的最后活动时间。此时间主要用于发送保持连接的心跳消息。
	 */
	public long getLastAliveTime();

	/**
	 * 获取连接创建时的时间
	 */
	public long getCreationTime();

	/**
	 * 获取最后一次被调用的时间。此时间用于将长时间没有使用的连接资源断开
	 * 
	 */
	public long getLastCallTime();

	/**
	 * 获取连接所属的连接池对象
	 */
	public ConnectionPool<?> getPool();

	/**
	 * 获取连接的调用者。当不为null时，表示连接正在被调用者使用，为null时，表示连接处于空闲状态
	 * 
	 */
	public Object getCaller();

	/**
	 * 设置连接的调用者。当不为null时，表示连接正在被调用者使用，为null时，表示连接处于空闲状态
	 * 
	 */
	public void setCaller(Object caller);
}
