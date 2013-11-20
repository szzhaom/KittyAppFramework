package kitty.kaf.pools.memcached;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kitty.kaf.cache.MemcachedCallback;
import kitty.kaf.exceptions.CoreException;
import kitty.kaf.io.Cachable;

/**
 * 基于缓存的Map
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 * @param <K>
 * @param <V>
 */
public class MemcachedMap<K extends Serializable, V extends Cachable<K>>
		implements Map<K, V> {
	protected MemcachedClient mc;
	protected String keyPrefix;
	protected Class<V> clazz;
	protected MemcachedCallback callback;

	public MemcachedClient getMc() {
		return mc;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public Class<V> getClazz() {
		return clazz;
	}

	public MemcachedMap(MemcachedCallback callback, MemcachedClient mc,
			String keyPrefix, Class<V> clazz) {
		super();
		this.mc = mc;
		this.keyPrefix = keyPrefix;
		this.clazz = clazz;
		this.callback = callback;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public String getCacheKey(Object key) {
		return keyPrefix + key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object id) {
		try {
			String k = getCacheKey(id);
			V v = mc.get(k, clazz);
			if (v == null) {
				v = (V) callback.onGetCacheValue(this, id);
				if (v == null) {
					v = clazz.newInstance();
					v.setNull(true);
				}
				mc.set(k, v, null);
			}
			return v.isNull() ? null : v;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public V put(K k, V v) {
		try {
			mc.set(getCacheKey(k), v, null);
			return null;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		try {
			Iterator<? extends K> it = arg0.keySet().iterator();
			while (it.hasNext()) {
				K k = it.next();
				mc.set(getCacheKey(k), arg0.get(k), null);
			}
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public V remove(Object arg0) {
		try {
			mc.delete(getCacheKey(arg0), null);
			return null;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	public void removeAll(List<?> c) {
		try {
			for (Object k : c)
				mc.delete(getCacheKey(k), null);
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	public void removeAll(Object[] c) {
		try {
			for (Object k : c)
				mc.delete(getCacheKey(k), null);
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	public List<V> gets(List<K> idList) {
		try {
			List<String> keys = new ArrayList<String>();
			List<V> list = new ArrayList<V>();
			for (K o : idList) {
				if (o != null)
					keys.add(getCacheKey(o));
			}
			Map<String, V> map = mc.get(keys, clazz);
			list.addAll(map.values());
			// 未获取到的，再次从数据库中获取
			for (K id : idList) {
				if (id == null)
					continue;
				boolean has = false;
				for (V o : list) {
					if (o.getId().equals(id)) {
						has = true;
						break;
					}
				}
				if (!has) {
					list.add(get(id));
				}
			}
			return list;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}
}
