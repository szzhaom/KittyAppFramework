package kitty.kaf.pools.memcached;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kitty.kaf.cache.MemcachedCallback;
import kitty.kaf.exceptions.CoreException;
import kitty.kaf.helper.SecurityHelper;
import kitty.kaf.io.UnuqieKeyCachable;

/**
 * 包含名字和关键字的缓存Map
 * 
 * @author apple
 * 
 * @param <K>
 * @param <V>
 */
public class UniqueKeyMemcachedMap<K extends Serializable, N extends Serializable, V extends UnuqieKeyCachable<K>>
		extends MemcachedMap<K, V> {

	public UniqueKeyMemcachedMap(MemcachedCallback getValueCallback,
			MemcachedClient mc, String keyPrefix, Class<V> clazz) {
		super(getValueCallback, mc, keyPrefix, clazz);
	}

	protected String getUKCacheKey(Object key) throws NoSuchAlgorithmException {
		return keyPrefix + ".uk." + SecurityHelper.md5(key.toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		try {
			String k = getCacheKey(key);
			V v = mc.get(k, clazz);
			if (v == null) {
				v = (V) callback.onGetCacheValue(this, key);
				if (v == null) {
					v = clazz.newInstance();
					v.setNull(true);
				}
				if (!v.isNull())
					mc.set(getUKCacheKey(v.getUniqueKey()), v.getId(), null);
				mc.set(k, v, null);
			}
			return v.isNull() ? null : v;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public V remove(Object key) {
		try {
			String ck = getCacheKey(key);
			V v = mc.get(ck, clazz);
			mc.delete(ck, null);
			if (v != null)
				mc.delete(getUKCacheKey(v.getUniqueKey()), null);
			return null;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public void removeAll(List<?> c) {
		try {
			for (Object k : c) {
				String ck = getCacheKey(k);
				V v = mc.get(ck, clazz);
				mc.delete(ck, null);
				if (v != null)
					mc.delete(getUKCacheKey(v.getUniqueKey()), null);
			}
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public void removeAll(Object[] c) {
		try {
			for (Object k : c) {
				String ck = getCacheKey(k);
				V v = mc.get(ck, clazz);
				mc.delete(ck, null);
				if (v != null)
					mc.delete(getUKCacheKey(v.getUniqueKey()), null);
			}
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public V getByName(Object name) {
		try {
			String nk = getUKCacheKey(name);
			K key = (K) mc.get(nk);
			V v = null;
			if (key != null) {
				if (callback.isNullId(key))
					return null;
				v = mc.get(getCacheKey(key), clazz);
			}
			if (v == null) {
				v = (V) callback.onGetCacheValue(this, name);
				if (v == null) {
					v = clazz.newInstance();
					v.setNull(true);
				}
				if (!v.isNull()) {
					mc.set(getCacheKey(v.getId()), v, null);
				}
				mc.set(nk, v.getId(), null);
			}
			return v.isNull() ? null : v;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public V put(K key, V value) {
		try {
			mc.set(getCacheKey(key), value, null);
			mc.set(getUKCacheKey(value.getUniqueKey()), value.getId(), null);
			return null;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> c) {
		try {
			Iterator<? extends K> it = c.keySet().iterator();
			while (it.hasNext()) {
				K k = it.next();
				V o = c.get(k);
				mc.set(getCacheKey(k), o, null);
				mc.set(getUKCacheKey(o.getUniqueKey()), o.getId(), null);
			}
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	public void removeUniqueKey(Object name) {
		try {
			String nk = getUKCacheKey(name);
			mc.delete(nk, null);
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}
}
