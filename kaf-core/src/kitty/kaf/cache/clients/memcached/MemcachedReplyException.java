package kitty.kaf.cache.clients.memcached;

import java.io.IOException;

public class MemcachedReplyException extends IOException {
	private static final long serialVersionUID = 1L;

	public MemcachedReplyException() {
	}

	public MemcachedReplyException(String arg0) {
		super(arg0);
	}

	public MemcachedReplyException(Throwable arg0) {
		super(arg0);
	}

	public MemcachedReplyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
