package kitty.kaf.cache;

import java.io.Serializable;
import java.util.List;

import kitty.kaf.io.Cachable;

/**
 * 缓存回调接口
 * <p>
 * 当一个缓存Map获取值时，如果缓存内部找不到该值，则会从缓存服务器取值，此时需要调用此接口获取服务器的数据
 * </p>
 * 
 * @author 赵明
 * @version 1.0
 * @param <K>
 * @param <E>
 */
public interface CacheCallback<K extends Serializable, E extends Cachable<K>> {
	/**
	 * 根据唯一ID获取缓存数据
	 * 
	 * @param source
	 *            调用源
	 * @param id
	 *            对应于数据库中的主键
	 * @return 获取的缓存数据对象
	 */
	public E onGetCacheValueById(Object source, K id) throws Throwable;

	/**
	 * 根据唯一名称获取缓存数据
	 * <p>
	 * 说明：并不是所有缓存都需要根据名称查找数据
	 * </p>
	 * 
	 * @param source
	 *            调用源
	 * @param name
	 *            对应于数据库中的唯一名称
	 * @return 获取的数据对象
	 */
	public E onGetCacheValueByName(Object source, String name) throws Throwable;

	/**
	 * 根据唯一ID列表，获取缓存数据列表。即一次性获取多个数据
	 * 
	 * @param source
	 *            调用源
	 * @param idList
	 *            对应于数据库中的唯一ID列表
	 * @return 获取的数据对象列表
	 */
	public List<E> onGetCacheValueByIdList(Object source, List<K> idList) throws Throwable;

	/**
	 * 根据唯一名称列表，获取缓存数据列表。即一次性获取多个数据
	 * <p>
	 * 说明：并不是所有缓存都需要根据名称查找数据
	 * </p>
	 * 
	 * @param source
	 *            调用源
	 * @param nameList
	 *            对应于数据库中的唯一名称列表
	 * @return 获取的数据对象列表
	 */
	public List<E> onGetCacheValueByNameList(Object source, List<String> nameList) throws Throwable;
}
