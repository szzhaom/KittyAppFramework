package kitty.kaf.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import kitty.kaf.dao.table.TableObject;
import kitty.kaf.exceptions.CoreException;
import kitty.kaf.io.UniqueKeyable;
import kitty.kaf.json.JSONArray;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.listeners.ItemChangedEventListener;
import kitty.kaf.util.DateTime;

/**
 * 带本地缓存的Map
 * 
 * @author 赵明
 * 
 * @param <K>
 * @param <V>
 */
public class LocalCachedMap<K extends Serializable, V extends LocalCachable<K>> extends ConcurrentHashMap<K, V> {
	private static final long serialVersionUID = 1L;
	protected int refreshInterval;
	protected Date lastRefreshTime = null, lastModifiedTime = null;
	protected CopyOnWriteArrayList<V> items = new CopyOnWriteArrayList<V>();
	protected ConcurrentHashMap<String, V> uniqueKeyMap = null;
	LocalCacheCallback callback;
	ReentrantLock lock = new ReentrantLock();
	protected int queryPageRecordCount = 10000;
	ItemChangedEventListener onChangeEventListener;
	private CacheClient cacheClient;
	String name;

	public Date getLastRefreshTime() {
		return lastRefreshTime;
	}

	public void setLastRefreshTime(Date lastRefreshTime) {
		if (this.lastModifiedTime == null)
			return;
		if (lastRefreshTime == null)
			lastRefreshTime = new Date(0);
		this.lastRefreshTime = lastRefreshTime;
	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public int getQueryPageRecordCount() {
		return queryPageRecordCount;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * 构造本地缓存映射
	 * 
	 * @param name
	 *            名称
	 * @param cacheClient
	 *            保存最后修改的memcached客户端对象
	 * @param callback
	 *            回调
	 * @param onChangeEventListener
	 *            当缓存内容改变时触发的事件
	 * @param refreshInterval
	 *            刷新时间，以秒为单位
	 */
	public LocalCachedMap(String name, CacheClient cacheClient, LocalCacheCallback callback,
			ItemChangedEventListener onChangeEventListener, int refreshInterval) {
		this.cacheClient = cacheClient;
		this.name = name;
		this.refreshInterval = refreshInterval;
		this.callback = callback;
		this.onChangeEventListener = onChangeEventListener;
	}

	public LocalCachedMap(String name, CacheClient cacheClient, LocalCacheCallback callback,
			ItemChangedEventListener onChangeEventListener, int refreshInterval, boolean isUniqueKeyable) {
		this.cacheClient = cacheClient;
		this.name = name;
		this.refreshInterval = refreshInterval;
		this.callback = callback;
		this.onChangeEventListener = onChangeEventListener;
		if (isUniqueKeyable)
			uniqueKeyMap = new ConcurrentHashMap<String, V>();
	}

	public String getName() {
		return name;
	}

	/**
	 * 刷新缓存
	 * 
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	public void refresh() {
		lock.lock();
		try {
			Date now = new Date();
			if (lastRefreshTime == null || DateTime.secondsBetween(lastRefreshTime, now) >= refreshInterval) {
				Date sourceLastModified = null;
				if (lastRefreshTime != null) {
					sourceLastModified = (Date) cacheClient.get("$cache.localcachec." + name);
				}
				lastRefreshTime = now;
				if (lastModifiedTime != null && sourceLastModified != null
						&& !sourceLastModified.after(lastModifiedTime))
					return;
				boolean modified = false;
				CacheValueList<?, ?> ls = callback
						.onGetCacheValueList(this, -1, queryPageRecordCount, lastModifiedTime);
				Date maxLastModified = sourceLastModified == null ? new Date(0) : sourceLastModified;
				if (ls != null) {
					int leftCount = ls.getTotalCount();
					int index = 0;
					while (true) {
						for (Object v : ls.getModifiedList()) {
							V o = (V) v;
							V old = super.get(o.getId());
							if (onChangeEventListener != null) {
								if (old != null)
									onChangeEventListener.edit(this, old, o);
								else
									onChangeEventListener.add(this, o);
							}
							super.put(o.getId(), o);
							items.remove(old);
							items.add(o);
							if (o.getLastModifiedTime() != null && maxLastModified.before(o.getLastModifiedTime())) {
								maxLastModified = o.getLastModifiedTime();
							}
						}
						for (Object v : ls.getDeletedList()) {
							V o = (V) v;
							super.remove(o.getId());
							items.remove(o);
							if (onChangeEventListener != null)
								onChangeEventListener.remove(this, o);
							if (o.getLastModifiedTime() != null && maxLastModified.before(o.getLastModifiedTime())) {
								maxLastModified = o.getLastModifiedTime();
							}
						}
						int c = ls.getModifiedList().size() + ls.getDeletedList().size();
						if (!modified)
							modified = c > 0;
						leftCount -= c;
						if (leftCount > 0) {
							if (index == 0)
								index = c;
							else
								index += c;
							ls = callback.onGetCacheValueList(this, index, queryPageRecordCount, lastModifiedTime);
							if (ls == null)
								break;
						} else
							break;
					}
				}
				if (modified) {
					changed();
				}
				setSourceLastModified(maxLastModified);
				lastModifiedTime = maxLastModified;
			}
		} catch (Throwable e) {
			throw new CoreException(e);
		} finally {
			lock.unlock();
		}
	}

	protected void changed() throws Throwable {
		List<V> list = new ArrayList<V>();
		list.addAll(items);
		Collections.sort(list);
		items.clear();
		items.addAll(list);
		if (uniqueKeyMap != null) {
			uniqueKeyMap.clear();
			for (V o : list) {
				uniqueKeyMap.put(((UniqueKeyable) o).getUniqueKey(), o);
			}
		}
		list = null;
		if (onChangeEventListener != null)
			onChangeEventListener.change(this);
	}

	public V getByName(String name) {
		refresh();
		if (uniqueKeyMap != null)
			return uniqueKeyMap.get(name);
		else
			return null;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<V> elements() {
		refresh();
		return super.elements();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		refresh();
		return super.entrySet();
	}

	@Override
	public V get(Object key) {
		refresh();
		return super.get(key);
	}

	@Override
	public boolean isEmpty() {
		refresh();
		return super.isEmpty();
	}

	@Override
	public Enumeration<K> keys() {
		refresh();
		return super.keys();
	}

	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V putIfAbsent(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V replace(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		refresh();
		return super.size();
	}

	@Override
	public Collection<V> values() {
		refresh();
		return super.values();
	}

	public List<V> getItems() {
		refresh();
		return items;
	}

	/**
	 * 获取当前本地缓存的源信息的最后修改时间
	 * 
	 */
	protected Date getSourceLastModified() {
		try {
			return new Date((Long) cacheClient.get("$cache.localcachec." + name));
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 设置当前本地缓存的源信息的最后改变时间
	 * 
	 * @param date
	 *            最后改变时间
	 */
	public void setSourceLastModified(Date lastModified) {
		if (lastModified == null)
			return;
		try {
			cacheClient.set("$cache.localcachec." + name, lastModified, null);
		} catch (Throwable e) {
			throw new CoreException(e);
		}
	}

	public void toJson(JSONArray a) throws JSONException {
		for (V o : items) {
			if (o instanceof TableObject) {
				JSONObject j = new JSONObject();
				((TableObject) o).toJson(j);
				a.put(j);
			}
		}
	}

	public String getJsonString() throws JSONException {
		JSONArray a = new JSONArray();
		toJson(a);
		return a.toString();
	}

	public List<V> gets(List<K> list) {
		List<V> r = new ArrayList<V>();
		for (V o : values()) {
			if (list.contains(o.getId())) {
				r.add(o);
			}
		}
		return r;
	}
}
