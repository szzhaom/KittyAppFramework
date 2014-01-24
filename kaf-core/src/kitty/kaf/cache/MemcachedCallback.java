package kitty.kaf.cache;

import java.io.Serializable;
import java.util.List;

import kitty.kaf.io.Cachable;

public interface MemcachedCallback<K extends Serializable, E extends Cachable<K>> {
	/**
	 * 获取与缓存Key相关的键值
	 * 
	 * @param source
	 * @param id
	 * @return
	 */
	public E onGetCacheValueById(Object source, K id) throws Throwable;

	public E onGetCacheValueByName(Object source, String id) throws Throwable;

	public List<E> onGetCacheValueByIdList(Object source, List<K> id) throws Throwable;

	public List<E> onGetCacheValueByNameList(Object source, List<String> id) throws Throwable;
}
