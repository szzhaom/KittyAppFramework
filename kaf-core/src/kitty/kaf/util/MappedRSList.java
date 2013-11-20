package kitty.kaf.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import kitty.kaf.io.Idable;


public class MappedRSList<K extends Serializable, V extends Idable<K>>
		extends RSList<V> {
	private static final long serialVersionUID = 1L;
	ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();

	public MappedRSList() {
		super();
	}

	@Override
	public void add(int index, V element) {
		super.add(index, element);
		map.put(element.getId(), element);
	}

	@Override
	public boolean add(V e) {
		if (super.add(e)) {
			map.put(e.getId(), e);
			return true;
		} else
			return false;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		if (super.addAll(c)) {
			for (V o : c)
				map.put(o.getId(), o);
			return true;
		} else
			return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends V> c) {
		if (super.addAll(index, c)) {
			for (V o : c)
				map.put(o.getId(), o);
			return true;
		} else
			return false;
	}

	@Override
	public int addAllAbsent(Collection<? extends V> c) {
		int r = super.addAllAbsent(c);
		for (V o : c) {
			if (!map.containsKey(o.getId()))
				map.put(o.getId(), o);
		}
		return r;
	}

	@Override
	public boolean addIfAbsent(V e) {
		if (super.addIfAbsent(e)) {
			map.put(e.getId(), e);
			return true;
		} else
			return false;
	}

	@Override
	public void clear() {
		super.clear();
		map.clear();
	}

	@Override
	public V remove(int index) {
		V o = super.remove(index);
		if (o != null)
			map.remove(o.getId());
		return o;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		if (super.remove(o)) {
			map.remove(((V) o).getId());
			return true;
		} else
			return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(Collection<?> c) {
		if (super.removeAll(c)) {
			for (Object o : c) {
				map.remove(((V) o).getId());
			}
			return true;
		} else
			return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(Collection<?> c) {
		if (super.retainAll(c)) {
			for (Object o : c) {
				if (!super.contains(o))
					map.remove(((V) o).getId());
			}
			return true;
		} else
			return false;
	}

}
