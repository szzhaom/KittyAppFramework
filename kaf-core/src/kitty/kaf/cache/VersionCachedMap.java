package kitty.kaf.cache;

import java.io.Serializable;
import java.util.Date;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.io.Cachable;
import kitty.kaf.util.DateTime;

/**
 * 基于版本控制的缓存Map，用于快速失效批量删除
 * 
 * @author apple
 * 
 * @param <K>
 * @param <V>
 */
public class VersionCachedMap<K extends Serializable, V extends Cachable<K>> extends CachedMap<K, V> {
	private Date lastModifiedTime;
	private DateTime lastUpdateVersionDate;

	/**
	 * 构建Map
	 * 
	 * @param callback
	 *            当缓存Key不存在时，用于取缓存Key的回调对象
	 * @param cacheClient
	 *            缓存Client
	 * @param keyPrefix
	 *            key前缀，用于将对象id转换成缓存键值，即缓存Key=keyPrefix+v.getId()
	 * @param clazz
	 *            用于生成V实例的类
	 */
	public VersionCachedMap(CacheCallback<K, V> callback, CacheClient cacheClient, String keyPrefix, Class<V> clazz) {
		super(callback, cacheClient, keyPrefix, clazz);
	}

	/**
	 * 
	 * @return 获取最后修改时间
	 */
	public Date getSavedLastModifiedTime() {
		return lastModifiedTime;
	}

	@Override
	public String getCacheKey(Object key) {
		if (lastUpdateVersionDate == null || new DateTime().secondsBetween(lastUpdateVersionDate) >= 1) {
			try {
				lastUpdateVersionDate = new DateTime();
				Date time = (Date) cacheClient.get(this.keyPrefix + "version");
				if (time == null) {
					if (lastModifiedTime != null)
						cacheClient.set(this.keyPrefix + "version", lastModifiedTime, null);
					else {
						lastModifiedTime = new Date(0);
						cacheClient.set(this.keyPrefix + "version", lastModifiedTime, null);
					}
				} else if (lastModifiedTime == null || time.after(lastModifiedTime)) {
					lastModifiedTime = time;
				} else if (time.before(lastModifiedTime)) {
					cacheClient.set(this.keyPrefix + "version", lastModifiedTime, null);
				}
			} catch (Throwable e) {
				throw new CoreException(e);
			}
		}
		return keyPrefix + "data." + lastModifiedTime.getTime() + "." + key;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

}
