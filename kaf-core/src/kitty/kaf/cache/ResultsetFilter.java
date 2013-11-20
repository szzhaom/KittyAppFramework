package kitty.kaf.cache;

import java.io.Serializable;

abstract public class ResultsetFilter<V> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResultsetFilter() {
		super();
	}

	public ResultsetFilter(String keyword) {
		super();
		this.keyword = keyword;
	}

	public String keyword;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	abstract public boolean contains(V o);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultsetFilter<?> other = (ResultsetFilter<?>) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return keyword;
	}

	public void fromString(String keyword) {
		this.keyword = keyword;
	}
}
