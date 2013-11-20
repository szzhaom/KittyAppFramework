package kitty.kaf.io;

/**
 * 包含id属性的对象。抽象类，未全部实现Idable的接口。该对象用id重载了hashCode(),equals()两个方法，方便对象比较
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 * @param <E>
 *            id的类
 */
abstract public class IdObject<E> implements Idable<E>, Copyable<Idable<E>> {
	private static final long serialVersionUID = 1L;
	E id;

	public IdObject() {
		super();
	}

	public IdObject(E id) {
		super();
		this.id = id;
	}

	@Override
	public E getId() {
		return id;
	}

	@Override
	public void setId(E id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		IdObject<?> other = (IdObject<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Idable<E> obj) {
		if (this == obj)
			return 0;
		if (obj == null)
			throw new NullPointerException();
		if (getClass() != obj.getClass())
			throw new ClassCastException();
		IdObject<?> other = (IdObject<?>) obj;
		if (id == null) {
			if (other.id != null)
				return 1;
		} else
			return compareId(id, other.id);
		return 0;
	}

	/**
	 * 比较两个id的大小。compareTo函数中会调用该接口比较两个对象的大小
	 * 
	 * @param id1
	 *            第1个id
	 * @param id2
	 *            第2个id
	 * @return 0 - id1=id2<br>
	 *         负数 - id1<id2<br>
	 *         正数 - id1>id2
	 */
	abstract protected int compareId(E id1, Object id2);

	/**
	 * 新创建一个实例
	 * 
	 * @return 新创建的实例
	 */
	abstract protected IdObject<E> newInstance();

	@Override
	public void copyData(Idable<E> other) {
		if (other == null)
			throw new NullPointerException();
		setId(other.getId());
	}

	@Override
	public Idable<E> copy() {
		IdObject<E> o = newInstance();
		o.copyData(this);
		return o;
	}

}
