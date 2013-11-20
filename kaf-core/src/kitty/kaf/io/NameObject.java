package kitty.kaf.io;

/**
 * 从IdObject派生的，实现了Namable接口的对象。
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 * @param <E>
 *            id的类
 */
abstract public class NameObject<E> extends IdObject<E> implements UniqueKeyable {
	private static final long serialVersionUID = 1L;
	String name;

	public NameObject() {
		super();
	}

	public NameObject(E id) {
		super(id);
	}

	@Override
	public String getUniqueKey() {
		return name;
	}

	@Override
	public void setUniqueKey(String v) {
		this.name = v;
	}

}
