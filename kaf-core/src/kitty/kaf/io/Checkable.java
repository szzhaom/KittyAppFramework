package kitty.kaf.io;

/**
 * 使数据对象具备复选的能力。
 * <p>
 * 通过实现<code>Checkable</code>接口，使数据对象具备复选的功能
 * </p>
 * 
 * @author 赵明
 * @version 1.0 , 2008/05/06
 */
public interface Checkable {
	/**
	 * 返回对象当前是否是选中状态
	 */
	public boolean isChecked();

	/**
	 * 设置对象是否选中状态
	 */
	public void setChecked(boolean newValue);
}
