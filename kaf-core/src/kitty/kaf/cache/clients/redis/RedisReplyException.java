package kitty.kaf.cache.clients.redis;

import java.io.IOException;

public class RedisReplyException extends IOException {
	private static final long serialVersionUID = 1L;

	public RedisReplyException() {
	}

	public RedisReplyException(String arg0) {
		super(arg0);
	}

	public RedisReplyException(Throwable arg0) {
		super(arg0);
	}

	public RedisReplyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
