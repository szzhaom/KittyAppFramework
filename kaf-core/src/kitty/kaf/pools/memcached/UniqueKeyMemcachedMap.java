package kitty.kaf.pools.memcached;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
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
	V nullValue;

	public UniqueKeyMemcachedMap(MemcachedCallback<K, V> getValueCallback, MemcachedClient mc, String keyPrefix,
			Class<V> clazz) {
		super(getValueCallback, mc, keyPrefix, clazz);
		try {
			nullValue = clazz.newInstance();
			nullValue.setNull(true);
		} catch (Throwable e) {
		}
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
			if (v == null || v.isNull()) {
				v = callback.onGetCacheValueById(this, (K) key);
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
			if (v != null && v.getUniqueKey() != null)
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
				if (((Comparable<K>) nullValue.getId()).compareTo(key) >= 0)
					return null;
				v = mc.get(getCacheKey(key), clazz);
			}
			if (v == null) {
				v = callback.onGetCacheValueByName(this, (String) name);
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

	public Map<String, V> getByNameMap(List<String> ls) {
		List<V> s = getByNames(ls);
		Map<String, V> r = new HashMap<String, V>();
		for (V o : s)
			r.put(o.getUniqueKey(), o);
		return r;
	}

	@SuppressWarnings("unchecked")
	public List<V> getByNames(List<String> ls) {
		if (ls == null || ls.size() == 0)
			return new ArrayList<V>();
		try {
			List<String> keys = new ArrayList<String>();
			List<V> list = new ArrayList<V>();
			for (Object o : ls) {
				keys.add(getUKCacheKey(o));
			}
			Map<String, Object> map = new HashMap<String, Object>();
			mc.get(keys, map);
			Iterator<String> it = map.keySet().iterator();
			List<K> ids = new ArrayList<K>();
			List<String> ts = new ArrayList<String>();
			ts.addAll(ls);
			while (it.hasNext()) {
				String k = it.next();
				K o = (K) map.get(k);
				if (((Comparable<K>) nullValue.getId()).compareTo(o) < 0) {
					ids.add(o);
				}
				int index = keys.indexOf(k);
				ts.remove(index);
				keys.remove(index);
			}
			if (ids.size() > 0) {
				list.addAll(gets(ids));
			}
			if (ts.size() > 0) {// 未获取到的，再次从数据库中获取
				List<V> s = callback.onGetCacheValueByNameList(this, ts);
				if (s != null) {
					list.addAll(s);
					for (V o : s) {// 设置缓存
						ts.remove(o.getUniqueKey());
						put(o.getId(), o);
					}
				}
				for (String id : ts) { // 未找到的，设置为null缓存
					mc.set(getUKCacheKey(id), nullValue.getId(), null);
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

	@Override
	public V put(K key, V value) {
		try {
			mc.set(getCacheKey(key), value, null);
			if (!value.isNull())
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
