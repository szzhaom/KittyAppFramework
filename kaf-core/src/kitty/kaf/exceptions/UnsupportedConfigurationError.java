package kitty.kaf.exceptions;

/**
 * 没有找到配置项目错误
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class UnsupportedConfigurationError extends LinkageError {
	private static final long serialVersionUID = 1L;

	public UnsupportedConfigurationError() {
		super();
	}

	public UnsupportedConfigurationError(String message, Throwable t) {
		super(message, t);
	}

	public UnsupportedConfigurationError(String message) {
		super(message);
	}

}
