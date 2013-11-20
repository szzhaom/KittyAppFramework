package kitty.kaf.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kitty.kaf.io.Cachable;

public class CacheValueList<K extends Serializable, V extends Cachable<K>>
		implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<V> modifiedList;
	private List<V> deletedList;
	private int totalCount;

	public CacheValueList() {
		super();
		this.modifiedList = new ArrayList<V>();
		this.deletedList = new ArrayList<V>();
	}

	public CacheValueList(List<V> modifiedList, List<V> deletedList) {
		super();
		this.modifiedList = modifiedList;
		this.deletedList = deletedList;
	}

	public List<V> getModifiedList() {
		return modifiedList;
	}

	public void setModifiedList(List<V> modifiedList) {
		this.modifiedList = modifiedList;
	}

	public List<V> getDeletedList() {
		return deletedList;
	}

	public void setDeletedList(List<V> deletedList) {
		this.deletedList = deletedList;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public boolean isEmpty() {
		return modifiedList.size() + deletedList.size() == 0;
	}
}
