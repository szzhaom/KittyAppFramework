package kitty.kaf.pools.memcached;

import java.io.IOException;

public class MemcachedException extends IOException {
	private static final long serialVersionUID = -5283961559628153679L;

	public MemcachedException() {
		super();
	}

	public MemcachedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MemcachedException(String arg0) {
		super(arg0);
	}

	public MemcachedException(Throwable arg0) {
		super(arg0);
	}

}
