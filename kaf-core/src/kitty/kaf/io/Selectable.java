package kitty.kaf.io;

/**
 * 使数据对象具备选择的能力。
 * <p>
 * 通过实现<code>Selectable</code>接口，使数据对象具备选择的功能
 * </p>
 * 
 * @author 赵明
 * @version 1.0 , 2008/05/06
 */
public interface Selectable {
	/**
	 * 返回对象当前是否是选中状态
	 */
	public boolean isSelected();

	/**
	 * 设置对象是否选中状态
	 */
	public void setSelected(boolean newValue);
}
