package kitty.kaf.io;

public abstract class IdCheckObject<E> extends IdObject<E> implements
		IdCheckable<E> {
	private static final long serialVersionUID = 1L;
	private boolean checked;

	public IdCheckObject() {
		super();
	}

	public IdCheckObject(E id) {
		super(id);
	}

	@Override
	public void copyData(Idable<E> other) {
		super.copyData(other);
		if (!(other instanceof IdCheckObject<?>))
			return;
		IdCheckObject<E> d = (IdCheckObject<E>) other;
		checked = d.checked;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
