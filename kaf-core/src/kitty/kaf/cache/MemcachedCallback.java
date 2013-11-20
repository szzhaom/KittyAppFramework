package kitty.kaf.cache;

public interface MemcachedCallback {
	/**
	 * 获取与缓存Key相关的键值
	 * 
	 * @param source
	 * @param id
	 * @return
	 */
	public Object onGetCacheValue(Object source, Object id) throws Throwable;

	public boolean isNullId(Object v);
}
