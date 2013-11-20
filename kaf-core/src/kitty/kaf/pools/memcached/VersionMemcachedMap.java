package kitty.kaf.pools.memcached;

import java.io.Serializable;
import java.util.Date;

import kitty.kaf.cache.MemcachedCallback;
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
public class VersionMemcachedMap<K extends Serializable, V extends Cachable<K>>
		extends MemcachedMap<K, V> {
	private Date lastModifiedTime;
	private DateTime lastUpdateVersionDate;

	public VersionMemcachedMap(MemcachedCallback callback, MemcachedClient mc,
			String keyPrefix, Class<V> clazz) {
		super(callback, mc, keyPrefix, clazz);
	}

	public Date getSavedLastModifiedTime() {
		return lastModifiedTime;
	}

	@Override
	public String getCacheKey(Object key) {
		if (lastUpdateVersionDate == null
				|| new DateTime().secondsBetween(lastUpdateVersionDate) >= 1) {
			try {
				lastUpdateVersionDate = new DateTime();
				Date time = (Date) mc.get(this.keyPrefix + "version");
				if (time == null) {
					if (lastModifiedTime != null)
						mc.set(this.keyPrefix + "version", lastModifiedTime,
								null);
					else {
						lastModifiedTime = new Date(0);
						mc.set(this.keyPrefix + "version", lastModifiedTime,
								null);
					}
				} else if (lastModifiedTime == null
						|| time.after(lastModifiedTime)) {
					lastModifiedTime = time;
				} else if (time.before(lastModifiedTime)) {
					mc.set(this.keyPrefix + "version", lastModifiedTime, null);
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
