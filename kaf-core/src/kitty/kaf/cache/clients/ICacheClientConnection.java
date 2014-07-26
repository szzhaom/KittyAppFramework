package kitty.kaf.cache.clients;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kitty.kaf.io.ValueObject;
import kitty.kaf.pools.IConnection;

/**
 * 缓存客户端连接，用于与缓存服务器直接交互数据
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public interface ICacheClientConnection extends IConnection {
	/**
	 * 设置缓存值
	 * 
	 * @param key
	 *            缓存key
	 * @param value
	 *            缓存值
	 * @param expiry
	 *            过期时间点
	 * @throws IOException
	 *             如果通信故障
	 * @throws InterruptedException
	 *             如果异常中断
	 */
	void set(Object key, CacheBytesValue value, Date expiry) throws IOException, InterruptedException;

	/**
	 * 获取缓存值
	 * 
	 * @param key
	 *            缓存key
	 * @throws IOException
	 *             如果通信故障
	 * @throws InterruptedException
	 *             如果异常中断
	 */
	CacheBytesValue get(Object key) throws IOException, InterruptedException;

	/**
	 * 批量获取缓存值，返回结果中，不包含未找到的key
	 * 
	 * @param keys
	 *            缓存key列表
	 * @param map
	 *            返回的缓存键值映射
	 * @throws IOException
	 *             如果通信故障
	 * @throws InterruptedException
	 *             如果异常中断
	 */
	void get(List<String> keys, Map<String, CacheBytesValue> map) throws InterruptedException, IOException;

	/**
	 * 原子加或减
	 * 
	 * @param key
	 *            缓存key
	 * @param stepValue
	 *            加或减的步长
	 * @param value
	 *            操作后的返回值
	 * @return true/false 操作成功/失败
	 * @throws IOException
	 *             如果通信故障
	 * @throws InterruptedException
	 *             如果异常中断
	 */
	boolean incrdecr(Object key, long stepValue, ValueObject<Long> value) throws IOException, InterruptedException;

	/**
	 * 删除某个键
	 * 
	 * @param key
	 *            要删除的键
	 * @return true/false 删除成功/失败
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IOException
	 *             如果通信故障
	 * @throws InterruptedException
	 *             如果异常中断
	 */
	boolean delete(Object key) throws IOException, InterruptedException;

	/**
	 * 批量删除
	 * 
	 * @param keys
	 *            要删除的键列表
	 * @param expiry
	 *            指定删除的时间点
	 * @return true/false 删除成功/失败
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IOException
	 *             如果通信故障
	 * @throws InterruptedException
	 *             如果异常中断
	 */
	boolean delete(Object[] keys) throws IOException, InterruptedException;
}
