package kitty.kaf.exceptions;

/**
 * 不支持的配置错误
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class NoConfigDefFoundError extends LinkageError {
	private static final long serialVersionUID = 1L;

	public NoConfigDefFoundError() {
		super();
	}

	public NoConfigDefFoundError(String message, Throwable t) {
		super(message, t);
	}

	public NoConfigDefFoundError(String message) {
		super(message);
	}

}
