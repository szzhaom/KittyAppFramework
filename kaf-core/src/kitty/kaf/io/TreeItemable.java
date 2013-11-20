package kitty.kaf.io;


/**
 * 实现树元素的能力
 * 
 * @author 赵明
 * @param <E>
 *            树ID
 * @param <V>
 *            树元素
 * @version 1.0
 */

public interface TreeItemable<E> extends ListItemable<E> {
	public void setDepth(int value);

	public int getDepth();

	public void setExpanded(boolean value);

	public boolean isExpanded();
}
