package kitty.kaf.io;

/**
 * 指示每项可为null的接口
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public interface Nullable {
	/**
	 * 返回当前的数据是否为null
	 */
	boolean isNull();

	/**
	 * 设置当前值是否为null
	 * 
	 * @param v
	 *            是否为null
	 */
	void setNull(boolean v);
}
