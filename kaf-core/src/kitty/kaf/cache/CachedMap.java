package kitty.kaf.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.io.Cachable;

/**
 * 基于缓存的Map，可以让缓存操作象操作一个Map一样简单。keyPrefix用于将缓存的key和V.getId()作转换，一定要设置，
 * 并避免与其他类型数据重复。
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 * @param <K>
 * @param <V>
 */
public class CachedMap<K extends Serializable, V extends Cachable<K>> implements Map<K, V> {
	protected CacheClient cacheClient;
	protected String keyPrefix;
	protected Class<V> clazz;
	protected CacheCallback<K, V> callback;

	/**
	 * 
	 * @return 获取当前使用的缓存客户端
	 */
	public CacheClient getCacheClient() {
		return cacheClient;
	}

	/**
	 * 
	 * @return 获取缓存键前缀
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}

	/**
	 * 
	 * @return 获取当前的生成V实例的类
	 */
	public Class<V> getClazz() {
		return clazz;
	}

	/**
	 * 构建缓存Map
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
	public CachedMap(CacheCallback<K, V> callback, CacheClient cacheClient, String keyPrefix, Class<V> clazz) {
		super();
		this.cacheClient = cacheClient;
		this.keyPrefix = keyPrefix;
		this.clazz = clazz;
		this.callback = callback;
	}

	@Deprecated
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean containsKey(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean containsValue(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 获取缓存key，即：keyPrefix+"."+key
	 * 
	 * @param key
	 *            对象key，通常为对象的id
	 * @return 缓存key
	 */
	public String getCacheKey(Object key) {
		return keyPrefix + "." + key;
	}

	/**
	 * 获取数据，如果缓存里有id对象的数据，则返回，如果缓存里没有数据，则通过callback接口，获取该id的数据，并将此数据设置到缓存中，再返回，
	 * 下次取数据的时候，就可以直接缓存中取出。
	 * <p>
	 * 说明：如果从数据库取出的数据为null，此函数同样会设置缓存key为null，这样下次也不会继续调用callback从数据库中取数据，
	 * 减轻数据库的压力。所以， 有数据新增时，也需要删除缓存key，以确保下次能正常取到数据库里的最新值。
	 * </p>
	 * 
	 * @return 找到的数据。为null，表示数据不存在。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object id) {
		try {
			String k = getCacheKey(id);
			V v = cacheClient.get(k, clazz);
			if (v == null) {
				v = callback.onGetCacheValueById(this, (K) id);
				if (v == null) {
					v = clazz.newInstance();
					v.setNull(true);
				}
				cacheClient.set(k, v, null);
			}
			return v.isNull() ? null : v;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Deprecated
	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 设置缓存值
	 * 
	 * @param k
	 *            键
	 * @param v
	 *            值
	 */
	@Override
	public V put(K k, V v) {
		try {
			cacheClient.set(getCacheKey(k), v, null);
			return null;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 将values中的值，一次性设置到缓存中
	 * 
	 * @param values
	 *            要设置的数据Map
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> values) {
		try {
			Iterator<? extends K> it = values.keySet().iterator();
			while (it.hasNext()) {
				K k = it.next();
				cacheClient.set(getCacheKey(k), values.get(k), null);
			}
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 移除缓存
	 * 
	 * @param id
	 *            对象ID
	 */
	@Override
	public V remove(Object id) {
		try {
			cacheClient.delete(getCacheKey(id), null);
			return null;
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 移除多项缓存数据
	 * 
	 * @param c
	 *            包含多个id的键列表
	 */
	public void removeAll(List<?> c) {
		try {
			for (Object k : c)
				cacheClient.delete(getCacheKey(k), null);
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 移除多项缓存数据
	 * 
	 * @param c
	 *            包含多个id的键数组
	 */
	public void removeAll(Object[] c) {
		try {
			for (Object k : c)
				cacheClient.delete(getCacheKey(k), null);
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	@Deprecated
	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 获取多项数据。如果idList列表中某些id没有数据，则返回列表中不包含。
	 * 
	 * @param idList
	 *            需要获取的数据id列表
	 * @return 获取的数据列表
	 */
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
			Map<String, V> map = cacheClient.get(keys, clazz);
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
