package kitty.kaf.io;

public abstract class ListItem<E> extends IdCheckObject<E> implements
		ListItemable<E> {
	private static final long serialVersionUID = 1L;
	private boolean disabled;
	private boolean selected;

	public ListItem() {
		super();
	}

	public ListItem(E id) {
		super(id);
	}

	@Override
	public void copyData(Idable<E> other) {
		super.copyData(other);
		if (!(other instanceof ListItem<?>))
			return;
		ListItem<E> d = (ListItem<E>) other;
		disabled = d.disabled;
		selected = d.selected;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
