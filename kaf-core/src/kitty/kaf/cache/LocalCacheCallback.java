package kitty.kaf.cache;

import java.util.Date;

public interface LocalCacheCallback {

	/**
	 * 获取最新变化的缓存值列表
	 * 
	 * @param source
	 *            提交的对象
	 * @param lastModifiedTime
	 *            该时间点之后的对象键值返回
	 * @return
	 * @throws Throwable
	 */
	public CacheValueList<?, ?> onGetCacheValueList(Object source,
			long firstIndex, int count, Date lastModifiedTime) throws Throwable;

	public boolean isNullId(Object id);
}
