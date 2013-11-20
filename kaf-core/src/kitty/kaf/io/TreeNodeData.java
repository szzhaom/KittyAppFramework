package kitty.kaf.io;

public interface TreeNodeData<E> extends Idable<E> {
	public E getId();

	public E getParentId();

	public boolean isNullId(E id);

	public Number getDepth();

	public void copyDataToTreeNode(TreeNode<?, ?> node);

	public void copyDataFromTreeNode(TreeNode<?, ?> node);
}
