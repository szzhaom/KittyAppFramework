package kitty.kaf.cache;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.helper.SecurityHelper;
import kitty.kaf.io.UnuqieKeyCachable;

/**
 * 包含名字和关键字的缓存Map，即除了可以按id来定位数据外，还可以同时使用唯一的名称来定位数据。比较常见的场景就是会员，会员登录时用会员名来查找会员登录，
 * 登录后，则用会员id来查找数据
 * 
 * @author 赵明
 * @version 1.0
 * @param <K>
 * @param <V>
 */
public class UniqueKeyCachedMap<K extends Serializable, N extends Serializable, V extends UnuqieKeyCachable<K>> extends
		CachedMap<K, V> {
	V nullValue;

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
	public UniqueKeyCachedMap(CacheCallback<K, V> callback, CacheClient cacheClient, String keyPrefix, Class<V> clazz) {
		super(callback, cacheClient, keyPrefix, clazz);
		try {
			nullValue = clazz.newInstance();
			nullValue.setNull(true);
		} catch (Throwable e) {
		}
	}

	/**
	 * 获取基于名称的缓存Key
	 * 
	 * @param key
	 *            对象id
	 * @return 缓存Key
	 * @throws NoSuchAlgorithmException
	 */
	protected String getUKCacheKey(Object key) throws NoSuchAlgorithmException {
		return keyPrefix + ".uk." + SecurityHelper.md5(key.toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		try {
			String k = getCacheKey(key);
			V v = cacheClient.get(k, clazz);
			if (v == null || v.isNull()) {
				v = callback.onGetCacheValueById(this, (K) key);
				if (v == null) {
					v = clazz.newInstance();
					v.setNull(true);
				}
				if (!v.isNull())
					cacheClient.set(getUKCacheKey(v.getUniqueKey()), v.getId(), null);
				cacheClient.set(k, v, null);
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
			V v = cacheClient.get(ck, clazz);
			cacheClient.delete(ck);
			if (v != null && v.getUniqueKey() != null)
				cacheClient.delete(getUKCacheKey(v.getUniqueKey()));
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
				V v = cacheClient.get(ck, clazz);
				cacheClient.delete(ck);
				if (v != null)
					cacheClient.delete(getUKCacheKey(v.getUniqueKey()));
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
				V v = cacheClient.get(ck, clazz);
				cacheClient.delete(ck);
				if (v != null)
					cacheClient.delete(getUKCacheKey(v.getUniqueKey()));
			}
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public V getByName(Object name) {
		try {
			String nk = getUKCacheKey(name);
			K key = (K) cacheClient.get(nk);
			V v = null;
			if (key != null) {
				if (((Comparable<K>) nullValue.getId()).compareTo(key) >= 0)
					return null;
				v = cacheClient.get(getCacheKey(key), clazz);
			}
			if (v == null) {
				v = callback.onGetCacheValueByName(this, (String) name);
				if (v == null) {
					v = clazz.newInstance();
					v.setNull(true);
				}
				if (!v.isNull()) {
					cacheClient.set(getCacheKey(v.getId()), v, null);
				}
				cacheClient.set(nk, v.getId(), null);
			}
			return v.isNull() ? null : v;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 根据名称map，获取名称对象的数据列表，如果名称对应的数据不存在，则返回数据列表中不包含此项名称
	 * 
	 * @param ls
	 *            要获取数据的名称列表
	 * @return 获取的数据Map
	 * @see UniqueKeyCachedMap#getByNames(List)
	 */
	public Map<String, V> getByNameMap(List<String> ls) {
		List<V> s = getByNames(ls);
		Map<String, V> r = new HashMap<String, V>();
		for (V o : s)
			r.put(o.getUniqueKey(), o);
		return r;
	}

	/**
	 * 根据名称list，获取名称对象的数据列表，如果名称对应的数据不存在，则返回数据列表中不包含此项名称
	 * 
	 * @param ls
	 *            要获取数据的名称列表
	 * @return 获取的数据列表
	 */
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
			cacheClient.get(keys, map);
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
					cacheClient.set(getUKCacheKey(id), nullValue.getId(), null);
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
			cacheClient.set(getCacheKey(key), value, null);
			if (!value.isNull())
				cacheClient.set(getUKCacheKey(value.getUniqueKey()), value.getId(), null);
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
				cacheClient.set(getCacheKey(k), o, null);
				cacheClient.set(getUKCacheKey(o.getUniqueKey()), o.getId(), null);
			}
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 移除名称对应的数据
	 * 
	 * @param name
	 *            数据的名称
	 */
	public void removeUniqueKey(Object name) {
		try {
			String nk = getUKCacheKey(name);
			cacheClient.delete(nk);
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}
}
