package kitty.kaf.cache;

import java.util.Date;

import kitty.kaf.io.Cachable;

public interface LocalCachable<E> extends Cachable<E> {
	public Date getLastModifiedTime();
}
