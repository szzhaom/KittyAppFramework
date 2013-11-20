package kitty.kaf.io;

import java.util.Collection;

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

public interface TreeNodeable<E, V extends TreeNodeData<E>> extends
		TreeItemable<E> {

	public Collection<?> getChildren();

	public TreeNodeable<E, V> getParent();

	public TreeNodeable<E, V> add(E id, int index);

	public TreeNodeable<E, V> find(Object id, boolean includeChildren);

	public TreeNodeable<E, V> remove(E id);

	public TreeNodeable<E, V> getLastChild();

	public TreeNodeable<E, V> getFirstChild();

	public boolean isEmpty();

	public void clear();

	public boolean remove(TreeNodeable<E, V> o);

	public void setValue(V o);

	public V getValue();
}
