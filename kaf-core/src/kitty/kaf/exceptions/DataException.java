package kitty.kaf.exceptions;

/**
 * 数据异常。用于数据处理或数据通讯时出现问题抛出
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class DataException extends CoreException {
	private static final long serialVersionUID = 1L;

	public DataException() {
		super();
	}

	public DataException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DataException(String arg0) {
		super(arg0);
	}

	public DataException(Throwable arg0) {
		super(arg0);
	}

}
