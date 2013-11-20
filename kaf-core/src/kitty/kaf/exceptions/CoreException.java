package kitty.kaf.exceptions;

/**
 * 用于框架的基类异常
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class CoreException extends RuntimeException {
	private static final long serialVersionUID = 8324817294616028113L;
	Object data;

	public CoreException() {
	}

	public CoreException(String msg) {
		super(msg);
	}

	public CoreException(Throwable cause) {
		super(cause);
	}

	public CoreException(String msg, Object data) {
		super(msg);
		this.data = data;
	}

	public CoreException(Throwable cause, Object data) {
		super(cause);
		this.data = data;
	}

	public CoreException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CoreException(String msg, Throwable cause, Object data) {
		super(msg, cause);
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
