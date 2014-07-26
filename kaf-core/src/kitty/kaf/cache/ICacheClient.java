package kitty.kaf.cache;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 缓存客户端远程接口
 * <p>
 * 本接口定义Key-Value缓存客户端接口，用于规范与不同的缓存服务器通讯标准，具体的通讯协议在接口实现类中实现。
 * </p>
 * 
 * @author 赵明
 * @since 1.0
 */
public interface ICacheClient {

	/**
	 * 设置键值，如果key已经存在，则替换，如果key不存在，则新建
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            服务器的过期时间点
	 * @throws IOException
	 *             如果与缓存服务器通讯出现故障
	 * @throws InterruptedException
	 *             如果服务线程中断
	 */
	public void set(String key, Object value, Date expiry) throws IOException, InterruptedException;

	/**
	 * 获取一个键值
	 * 
	 * @param key
	 *            键值数组
	 * @return 返回与key对应的键值，如果key不存在，返回null
	 * @throws IOException
	 *             如果与缓存服务器通讯出现故障
	 * @throws InterruptedException
	 *             如果服务线程中断
	 */
	public Object get(String key) throws IOException, InterruptedException;

	/**
	 * 获取一个或多个值，键值可能分布在不同的服务器上
	 * 
	 * @param keys
	 *            要获取的键名数组
	 * @param map
	 *            返回的键值map
	 * @throws IOException
	 *             如果与缓存服务器通讯出现故障
	 * @throws InterruptedException
	 *             如果服务线程中断
	 */
	public void get(List<String> keys, Map<String, Object> map) throws InterruptedException, IOException;

	/**
	 * 增加计数器
	 * 
	 * @param key
	 *            键
	 * @param stepValue
	 *            增量步长，负数表示递减
	 * @return 操作后的计数值，如果key不存在，则返回null
	 * @throws IOException
	 *             如果与缓存服务器通讯出现故障
	 * @throws InterruptedException
	 *             如果服务线程中断
	 */
	public Long incrdecr(String key, long stepValue) throws IOException, InterruptedException;

	/**
	 * 删除一个缓存数据
	 * 
	 * @param key
	 *            缓存键
	 * @return 是否删除成功
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public boolean delete(String key) throws IOException, InterruptedException;

	/**
	 * 批量删除
	 * 
	 * @param keys
	 *            要删除的键数组
	 * @return 是否删除成功
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public void delete(Object keys[]) throws IOException, InterruptedException;

}
