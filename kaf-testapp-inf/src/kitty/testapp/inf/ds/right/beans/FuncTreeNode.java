package kitty.testapp.inf.ds.right.beans;

import java.util.Collection;
import java.util.Iterator;

import kitty.kaf.io.IdObject;
import kitty.kaf.io.Idable;
import kitty.kaf.io.TreeNode;

public class FuncTreeNode extends TreeNode<Long, Func> {
	private static final long serialVersionUID = -8079123921440052565L;
	Func function;

	public FuncTreeNode() {
		super();
	}

	public Func getFunction() {
		return function;
	}

	public void setFunction(Func f) {
		function = f;
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(Long.valueOf(id));
	}

	@Override
	public String getText() {
		if (getFunction() == null)
			return "";
		return getFunction().getFuncDesp();
	}

	/**
	 * 添加一个功能集合到功能树中
	 * 
	 * @param orgCollection
	 */
	public String add(Collection<Func> collection, boolean expanded) {
		Iterator<Func> it = collection.iterator();
		StringBuffer checked = new StringBuffer();
		if (it.hasNext()) {
			Func e = it.next();
			int depth = e.getFuncDepth();
			TreeNode<Long, Func> curNode = this;
			TreeNode<Long, Func> node = (TreeNode<Long, Func>) curNode.add(e.getId());
			node.setExpanded(true);
			node.setDisabled(false);
			((FuncTreeNode) node).function = e;
			node.setChecked(e.isChecked());
			if (e.isChecked())
				checked.append(e.getId() + ",");
			while (it.hasNext()) {
				e = it.next();
				if (e.getFuncDepth() > depth) {
					curNode = curNode.getLastChild();
					depth = e.getFuncDepth();
				} else if (depth > e.getFuncDepth()) {
					for (int i = 0; i < depth - e.getFuncDepth(); i++)
						curNode = curNode.getParent();
					depth = e.getFuncDepth();
				}
				node = curNode.add(e.getId());
				node.setExpanded(expanded);
				((FuncTreeNode) node).function = e;
				node.setChecked(e.isChecked());
				if (e.isChecked())
					checked.append(e.getId() + ",");
			}
		}
		return checked.toString();
	}

	public void addEnabledChildrenDataTo(Collection<Func> list) {
		Iterator<TreeNode<Long, Func>> it = getChildren().iterator();
		while (it.hasNext()) {
			FuncTreeNode node = (FuncTreeNode) it.next();
			if (!node.isChildrenDisabled()) {
				list.add(node.function);
				node.addEnabledChildrenDataTo(list);
			}
		}
	}

	public void addDataTo(Collection<Func> list) {
		Iterator<TreeNode<Long, Func>> it = getChildren().iterator();
		while (it.hasNext()) {
			FuncTreeNode node = (FuncTreeNode) it.next();
			list.add(node.function);
			node.addDataTo(list);
		}
	}

	@Override
	public void setText(String text) {
		if (getFunction() == null)
			return;
		getFunction().setFuncDesp(text);
	}

	public boolean isAllocEnabled() {
		return getFunction().isAllocEnabled();
	}

	@Override
	public void setValue(Func o) {
		function = o;
	}

	@Override
	public Func getValue() {
		return function;
	}

	@Override
	protected int compareId(Long id1, Object id2) {
		return id1.compareTo((Long) id2);
	}

	@Override
	protected IdObject<Long> newInstance() {
		return new FuncTreeNode();
	}

	@Override
	public void copyData(Idable<Long> src) {
		super.copyData(src);
		FuncTreeNode node = (FuncTreeNode) src;
		function = node.function;
	}

}
