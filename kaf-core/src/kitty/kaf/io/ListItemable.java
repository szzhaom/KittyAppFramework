package kitty.kaf.io;

/**
 * 具备列表元素能力的数据接口
 * 
 * @author 赵明
 * @param <E>
 *            项目ID类
 * @param <V>
 *            项目类
 * @version 1.0
 */

public interface ListItemable<E> extends Checkable, Copyable<Idable<E>>,
		Selectable, Idable<E> {
	public String getText();

	public void setText(String text);

}
