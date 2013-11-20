package kitty.kaf.io;

/**
 * 包含value属性的对象。
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 * @param <E>
 *            value的类
 */
public class ValueObject<E> implements Valuable<E> {
	private static final long serialVersionUID = 1L;
	E value;

	public ValueObject() {
		super();
	}

	public ValueObject(E value) {
		super();
		this.value = value;
	}

	@Override
	public E getValue() {
		return value;
	}

	@Override
	public void setValue(E v) {
		this.value = v;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ValueObject<?> other = (ValueObject<?>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
