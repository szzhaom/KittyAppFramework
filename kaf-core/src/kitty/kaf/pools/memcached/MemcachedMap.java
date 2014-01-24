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
public class MemcachedMap<K extends Serializable, V extends Cachable<K>> implements Map<K, V> {
	protected MemcachedClient mc;
	protected String keyPrefix;
	protected Class<V> clazz;
	protected MemcachedCallback<K, V> callback;

	public MemcachedClient getMc() {
		return mc;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public Class<V> getClazz() {
		return clazz;
	}

	public MemcachedMap(MemcachedCallback<K, V> callback, MemcachedClient mc, String keyPrefix, Class<V> clazz) {
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
		return keyPrefix + "." + key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object id) {
		try {
			String k = getCacheKey(id);
			V v = mc.get(k, clazz);
			if (v == null) {
				v = callback.onGetCacheValueById(this, (K) id);
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
		if (idList == null || idList.size() == 0)
			return new ArrayList<V>();
		try {
			List<String> keys = new ArrayList<String>();
			List<V> list = new ArrayList<V>();
			for (K o : idList) {
				if (o != null)
					keys.add(getCacheKey(o));
			}
			Map<String, V> map = mc.get(keys, clazz);
			Iterator<String> it = map.keySet().iterator();
			List<K> ids = new ArrayList<K>();
			ids.addAll(idList);
			while (it.hasNext()) {
				String k = it.next();
				V o = map.get(k);
				list.add(o);
				ids.remove(o.getId());
				// keys.remove(k);
			}
			if (ids.size() > 0) {// 未获取到的，再次从数据库中获取
				List<V> s = callback.onGetCacheValueByIdList(this, ids);
				if (s != null) {
					list.addAll(s);
					for (V o : s) {// 设置缓存
						ids.remove(o.getId());
						put(o.getId(), o);
					}
				}
				for (K id : ids) { // 未找到的，设置为null缓存
					V o = clazz.newInstance();
					o.setNull(true);
					o.setId(id);
					put(id, o);
				}
			}
			for (int i = 0; i < list.size(); i++) {
				V o = list.get(i);
				if (o == null || o.isNull()) {
					list.remove(i);
					i--;
				}
			}
			return list;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}
}
