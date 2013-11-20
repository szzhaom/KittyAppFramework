package kitty.kaf.listeners;

public interface ItemChangedEventListener extends ChangedEventListener {
	public void add(Object sender, Object item) throws Throwable;

	public void remove(Object sender, Object item) throws Throwable;

	public void edit(Object sender, Object item, Object newValue)
			throws Throwable;
}
