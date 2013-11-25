package kitty.kaf.io;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.json.JSONArray;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;

/**
 * 列表数据元素类。本类可以存储包含ID和复选属性的数据，通常应用于选择列表或表格中。
 * 
 * @author 赵明
 * @param <E>
 *            列表元素的ID属性类型
 * @version 1.0
 */
abstract public class TreeNode<E, V extends TreeNodeData<E>> extends TreeItem<E> implements TreeNodeable<E, V> {
	private static final long serialVersionUID = -8219738537237143693L;
	private TreeNode<E, V> parent;
	private CopyOnWriteArrayList<TreeNode<E, V>> children = new CopyOnWriteArrayList<TreeNode<E, V>>();
	private int depth;
	private boolean childrenDisabled;

	public TreeNode() {
		super();
		depth = 0;
		childrenDisabled = false;
	}

	public TreeNode(E id) {
		super(id);
		depth = 0;
		childrenDisabled = false;
	}

	public void buildByList(List<V> list) {
		this.clear();
		while (list.size() > 0) {
			V o = list.get(0);
			if (o.getParentId() != null && !o.isNullId(o.getParentId())) {
				TreeNode<E, V> p = find(o.getParentId(), true);
				if (p != null) {
					p.add(o.getId()).setValue(o);
					list.remove(0);
				} else {
					boolean exists = false;
					for (V v : list)
						if (v.getId().equals(o.getParentId())) {
							exists = true;
							break;
						}
					if (!exists)
						throw new CoreException("找不到结点[" + o.getIdString() + "]的父结点[" + o.getParentId() + "]");
					list.remove(0);
					list.add(o); // 移至末尾，以便父机构加入后再加
				}
			} else {
				add(o.getId()).setValue(o);
				list.remove(0);
			}
		}
	}

	public <C extends TreeNodeData<E>> void buildBySortedList(List<C> ls) {
		this.clear();
		Iterator<C> it = ls.iterator();
		if (it.hasNext()) {
			C e = it.next();
			int depth = e.getDepth().intValue();
			TreeNode<E, V> curNode = this;
			TreeNode<E, V> node = add(e.getId());
			e.copyDataToTreeNode(node);
			while (it.hasNext()) {
				e = it.next();
				if (e.getDepth().intValue() > depth) {
					if (curNode.getLastChild() == null) {
						throw new NullPointerException("node[" + e.getIdString() + "] has not children");
					}
					curNode = curNode.getLastChild();
					depth = e.getDepth().intValue();
				} else if (depth > e.getDepth().intValue()) {
					for (int i = 0; i < depth - e.getDepth().intValue(); i++) {
						if (curNode.getParent() == null) {
							throw new NullPointerException("org[" + e.getIdString() + "] has not parent");
						}
						curNode = curNode.getParent();
					}
					depth = e.getDepth().intValue();
				}
				if (e.getId() == null)
					throw new NullPointerException();
				else if (curNode == null)
					throw new NullPointerException();
				node = curNode.add(e.getId());
				e.copyDataToTreeNode(node);
			}
		}
	}

	public int compare(TreeNode<E, V> o2) {
		throw new UnsupportedOperationException("在TreeNode类中不支持该操作，请继承compre方法，实现该操作。");
	}

	protected int sortCompare(TreeNode<E, V> other) {
		return compare(other);
	}

	public void sort(boolean includeChildren) {
		ArrayList<TreeNode<E, V>> ls = new ArrayList<TreeNode<E, V>>();
		ls.addAll(children);
		Collections.sort(ls, new Comparator<TreeNode<E, V>>() {
			@Override
			public int compare(TreeNode<E, V> o1, TreeNode<E, V> o2) {
				return o1.sortCompare(o2);
			}
		});
		children.clear();
		children.addAll(ls);
		if (includeChildren) {
			for (TreeNode<E, V> o : children)
				o.sort(includeChildren);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyData(Idable<E> src) {
		super.copyData(src);
		if (!(src instanceof TreeNode<?, ?>))
			return;
		TreeNode<E, V> d = (TreeNode<E, V>) src;
		childrenDisabled = d.childrenDisabled;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Idable<E> copy() {
		Idable<E> r = super.copy();
		TreeNode<E, V> d = (TreeNode<E, V>) r;
		Iterator<TreeNode<E, V>> it = children.iterator();
		while (it.hasNext()) {
			d.add((TreeNode<E, V>) it.next().copy());
		}
		return r;
	}

	public TreeNode<E, V> add(E id) {
		if (id == null)
			throw new NullPointerException();
		@SuppressWarnings("unchecked")
		TreeNode<E, V> o = (TreeNode<E, V>) newInstance();
		children.add(o);
		o.parent = this;
		o.depth = depth + 1;
		o.setId(id);
		return o;
	}

	public void add(TreeNode<E, V> o) {
		children.add(o);
		o.parent = this;
		o.depth = depth + 1;
	}

	@Override
	public TreeNode<E, V> add(E id, int index) {
		if (id == null)
			throw new NullPointerException();
		@SuppressWarnings("unchecked")
		TreeNode<E, V> o = (TreeNode<E, V>) newInstance();
		children.add(index, o);
		o.parent = this;
		o.depth = depth + 1;
		o.setId(id);
		return o;
	}

	/**
	 * 判断当前结点是否是最后一个子结点
	 */
	public boolean isLastChild() {
		if (parent != null)
			return parent.getLastChild() == this;
		else
			return true;
	}

	/**
	 * 判断当前结点是否是第一个子结点
	 */
	public boolean isFirstChild() {
		if (parent != null)
			return parent.getFirstChild() == this;
		else
			return true;
	}

	public TreeNode<E, V> find(String keyword) {
		if (getText() != null && getText().contains(keyword))
			return this;
		return infind(keyword);
	}

	@SuppressWarnings("unchecked")
	private TreeNode<E, V> infind(String keyword) {
		Iterator<?> it = (Iterator<?>) getChildren().iterator();
		while (it.hasNext()) {
			TreeNode<E, V> node = (TreeNode<E, V>) it.next();
			if (node.getText().contains(keyword))
				return node;
			node = node.infind(keyword);
			if (node != null)
				return node;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeNode<E, V> find(Object id, boolean includeChildren) {
		if (id.equals(getId()))
			return this;
		return infind((E) id, includeChildren);
	}

	@SuppressWarnings("unchecked")
	private TreeNode<E, V> infind(E id, boolean includeChildren) {
		Iterator<?> it = (Iterator<?>) getChildren().iterator();
		while (it.hasNext()) {
			TreeNode<E, V> node = (TreeNode<E, V>) it.next();
			if (id.equals(node.getId()))
				return node;
			if (includeChildren) {
				node = node.infind(id, includeChildren);
				if (node != null)
					return node;
			}
		}
		return null;
	}

	@Override
	public TreeNode<E, V> remove(E id) {
		TreeNode<E, V> o = find(id, false);
		if (o != null)
			remove(o);
		return o;
	}

	@Override
	public boolean remove(TreeNodeable<E, V> o) {
		return children.remove(o);
	}

	@Override
	public List<TreeNode<E, V>> getChildren() {
		return children;
	}

	public void getChildren(List<TreeNode<E, V>> ls, int depth) {
		for (TreeNode<E, V> o : getChildren()) {
			ls.add(o);
			if (depth > 1 && !o.getChildren().isEmpty())
				o.getChildren(ls, depth - 1);
		}
	}

	@Override
	public TreeNode<E, V> getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		Iterator<?> it = children.iterator();
		while (it.hasNext()) {
			TreeNode<E, V> child = (TreeNode<E, V>) it.next();
			child.clear();
		}
		children.clear();
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
	}

	@Override
	public void setDisabled(boolean disabled) {
		super.setDisabled(disabled);
		if (disabled == false) {
			childrenDisabled = false;
			TreeNode<E, V> p = getParent();
			while (p != null && p.childrenDisabled) {
				p.childrenDisabled = false;
				p = p.getParent();
			}
		} else
			childrenDisabled = true;
	}

	@SuppressWarnings("unchecked")
	public void cleanChildrenDisabledNodes() {
		Iterator<?> it = getChildren().iterator();
		while (it.hasNext()) {
			TreeNode<E, V> e = (TreeNode<E, V>) it.next();
			if (e.childrenDisabled)
				remove(e);
			else {
				e.cleanChildrenDisabledNodes();
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return getChildren().isEmpty();
	}

	public boolean isChildrenDisabled() {
		return childrenDisabled;
	}

	public TreeNode<E, V> getNext() {
		int index = getParent().children.indexOf(this);
		if (index < getParent().children.size() - 1)
			return getParent().children.get(index + 1);
		else
			return null;
	}

	public TreeNode<E, V> getPrev() {
		int index = getParent().children.indexOf(this);
		if (index > 0)
			return getParent().children.get(index - 1);
		else
			return null;
	}

	public TreeNode<E, V> getLastChild() {
		if (isEmpty())
			return null;
		else
			return children.get(children.size() - 1);
	}

	public TreeNode<E, V> getFirstChild() {
		if (isEmpty())
			return null;
		else
			return children.get(0);
	}

	public boolean isParent(TreeNode<E, V> child) {
		TreeNode<E, V> parent = child;
		while (parent != null) {
			if (parent == this)
				return true;
			parent = parent.getParent();
		}
		return false;
	}

	private TreeNode<E, V> inGetFirstEnabledChild() {
		Iterator<TreeNode<E, V>> it = children.iterator();
		while (it.hasNext()) {
			TreeNode<E, V> o = it.next();
			if (!o.isDisabled())
				return o;
			o = o.inGetFirstEnabledChild();
			if (o != null)
				return o;
		}
		return null;
	}

	public TreeNode<E, V> getFirstEnabledChild() {
		if (!this.isDisabled() && getId() != null)
			return this;
		return inGetFirstEnabledChild();
	}

	public String getPath(String splitter, int toDepth) {
		TreeNode<E, V> p = this;
		StringBuffer sb = new StringBuffer(getText() == null ? "" : getText());
		while ((p = p.getParent()) != null && p.getDepth() >= toDepth)
			sb.insert(0, p.getText() + splitter);
		return sb.toString();
	}

	@Override
	public String toString() {
		return getText() + "[id=" + getId() + "]";
	}

	public TreeNode<E, V> get(int index) {
		return children.get(index);
	}

	public void getAll(List<TreeNode<E, V>> list) {
		list.add(this);
		for (TreeNode<E, V> o : children) {
			o.getAll(list);
		}
	}

	public void getAll(List<TreeNode<E, V>> list, int depth) {
		list.add(this);
		if (depth > 0) {
			for (TreeNode<E, V> o : children) {
				o.getAll(list, depth - 1);
			}
		}
	}

	public void printTree(PrintStream ps, String prefix) {
		if (this.getId() != null) {
			ps.println(prefix + getText() + "(" + getId() + ")");
		}
		Iterator<TreeNode<E, V>> it = getChildren().iterator();
		prefix += "  ";
		while (it.hasNext()) {
			it.next().printTree(ps, prefix);
		}
	}

	protected void toJsonData(JSONObject o) throws JSONException {
		o.put("text", this.getText());
		o.put("id", this.getId());
		o.put("haschild", !this.getChildren().isEmpty());
	}

	public void toJson(JSONObject o, int depths) throws JSONException {
		JSONArray a = null;
		if (this.getId() != null) {
			JSONObject j = new JSONObject();
			toJsonData(j);
			a = new JSONArray();
			o.put("items", a);
			a.put(j);
			a = new JSONArray();
			j.put("items", a);
		} else {
			a = new JSONArray();
			o.put("items", a);
		}
		toJson(a, depths);
	}

	public void toJson(JSONArray o, int depths) throws JSONException {
		for (TreeNode<E, V> treeNode : getChildren()) {
			JSONObject j = new JSONObject();
			o.put(j);
			treeNode.toJsonData(j);
			if (!treeNode.childrenDisabled && treeNode.getChildren().size() > 0 && depths > 1) {
				JSONArray a = new JSONArray();
				j.put("items", a);
				treeNode.toJson(a, depths - 1);
			}
		}
	}

	public void disableAll() {
		setDisabled(true);
		for (TreeNode<E, V> o : getChildren()) {
			o.disableAll();
		}
	}

	public void noCheckAll() {
		super.setChecked(false);
		for (TreeNode<E, V> o : getChildren()) {
			o.noCheckAll();
		}
	}

	public void enableAll(int depths) {
		setDisabled(false);
		depths--;
		if (depths > 0) {
			for (TreeNode<E, V> o : getChildren()) {
				o.enableAll(depths);
			}
		}
	}

	public void enableDepths(List<?> ls, int enableDepths) {
		disableAll();
		if (ls != null) {
			for (Object id : ls) {
				TreeNode<E, V> n = find(id, true);
				if (n != null)
					n.setDisabled(false);
			}
		}
		enableAll(enableDepths);
	}

	public void enableList(List<?> ls) {
		disableAll();
		if (ls != null) {
			for (Object id : ls) {
				TreeNode<E, V> n = find(id, true);
				if (n != null)
					n.setDisabled(false);
			}
		}
		cleanChildrenDisabledNodes();
	}

	public void checkList(List<?> ls) {
		noCheckAll();
		if (ls == null)
			return;
		for (Object id : ls) {
			TreeNode<E, V> n = find(id, true);
			if (n != null)
				n.setChecked(true);
		}
	}

	public void find(List<Object> outList, List<?> idList, boolean includeChildren) {
		if (idList == null || idList.size() == 0)
			return;
		if (this.getId() != null) {
			if (idList.contains(this.getId())) {
				idList.remove(this.getId());
				outList.add(this);
			}
		}
		if (includeChildren && idList.size() > 0) {
			for (TreeNode<E, V> o : getChildren())
				o.find(outList, idList, includeChildren);
		}
	}

	public String getJsonString2() throws JSONException {
		JSONArray a = new JSONArray();
		toJson(a, 2);
		return a.toString();
	}

	public String getJsonString3() throws JSONException {
		JSONArray a = new JSONArray();
		toJson(a, 3);
		return a.toString();
	}

	public String getJsonString4() throws JSONException {
		JSONArray a = new JSONArray();
		toJson(a, 4);
		return a.toString();
	}

	public String getJsonString5() throws JSONException {
		JSONArray a = new JSONArray();
		toJson(a, 5);
		return a.toString();
	}
}
