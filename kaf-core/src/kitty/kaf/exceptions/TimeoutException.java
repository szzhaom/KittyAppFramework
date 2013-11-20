package kitty.kaf.exceptions;

import java.io.IOException;

/**
 * 超时异常
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class TimeoutException extends IOException {
	private static final long serialVersionUID = 1L;

	public TimeoutException() {
		super();
	}

	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeoutException(String message) {
		super(message);
	}

	public TimeoutException(Throwable cause) {
		super(cause);
	}

}
