package kitty.kaf.pools;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import kitty.kaf.exceptions.ConnectException;

/**
 * 基于轮循式分布式访问的连接池列表
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * @param <C>
 * @param <P>
 */
public class LoopedConnectionPoolList<C extends IConnection, P extends ConnectionPool<C>>
		extends ConnectionPoolList<P> {
	private AtomicInteger index = new AtomicInteger(0);

	public LoopedConnectionPoolList(String name) {
		super(name);
	}

	/**
	 * 轮循访问可用的连接池，并从连接池获取一个连接
	 * 
	 * @param caller
	 *            连接调用者
	 * @return 获取的连接
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public C getConnection(Object caller) throws InterruptedException,
			ConnectException {
		int i = index.getAndAdd(1);
		if (i >= size()) {
			i = 0;
			index.set(1);
		}
		if (size() == 0) {
			throw new ConnectException("无可用的连接池");
		}
		return get(i).getConnection(caller);
	}

}
