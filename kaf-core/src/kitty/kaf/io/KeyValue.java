package kitty.kaf.io;

/**
 * 具备键、值对保存的数据对象
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 * @param <K>
 *            key的类
 * @param <V>
 *            value的类
 */
public class KeyValue<K, V> extends ValueObject<V> {
	private static final long serialVersionUID = 1L;
	K key;

	public KeyValue() {
		super();
	}

	public KeyValue(K key, V value) {
		super(value);
		this.key = key;
	}

	/**
	 * 获取key值
	 * 
	 * @return 获取的key值
	 */
	public K getKey() {
		return key;
	}

	/**
	 * 设置key值
	 * 
	 * @param key
	 *            要设置的key值
	 */
	public void setKey(K key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyValue<?, ?> other = (KeyValue<?, ?>) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
