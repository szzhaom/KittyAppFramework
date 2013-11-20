package kitty.kaf.cache;

import java.util.List;

import kitty.kaf.listeners.ItemChangedEventListener;

public interface LocalCachedTreeMapChangedEventListener extends
		ItemChangedEventListener {
	public void updateTreeDataToDatabase(Object source,
			List<Object[]> needUpdatedList) throws Throwable;
}
