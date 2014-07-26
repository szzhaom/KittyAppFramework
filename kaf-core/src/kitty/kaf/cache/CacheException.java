package kitty.kaf.cache;

import java.io.IOException;

/**
 * 缓存异常
 * 
 * @author 赵明
 * 
 */
public class CacheException extends IOException {
	private static final long serialVersionUID = 1L;

	public CacheException() {
	}

	public CacheException(String arg0) {
		super(arg0);
	}

	public CacheException(Throwable arg0) {
		super(arg0);
	}

	public CacheException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
