package kitty.kaf.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.io.TreeNode;
import kitty.kaf.pools.memcached.MemcachedClient;

public class LocalCachedTreeMap<K extends Serializable, V extends CachedTreeNodeData<K>>
		extends LocalCachedMap<K, V> {
	@SuppressWarnings("unchecked")
	public LocalCachedTreeMap(String name, MemcachedClient mc,
			LocalCacheCallback callback,
			LocalCachedTreeMapChangedEventListener onChangeEventListener,
			int refreshInterval, Class<?> clazz) {
		super(name, mc, callback, onChangeEventListener, refreshInterval);
		this.clazz = (Class<TreeNode<K, V>>) clazz;
	}

	@SuppressWarnings("unchecked")
	public LocalCachedTreeMap(String name, MemcachedClient mc,
			LocalCacheCallback callback,
			LocalCachedTreeMapChangedEventListener onChangeEventListener,
			int refreshInterval, Class<?> clazz, boolean isNamable) {
		super(name, mc, callback, onChangeEventListener, refreshInterval,
				isNamable);
		this.clazz = (Class<TreeNode<K, V>>) clazz;
	}

	Class<TreeNode<K, V>> clazz;
	private static final long serialVersionUID = 1L;
	private TreeNode<K, V> tree;

	protected void updateTreeNode() throws Throwable {
		if (tree == null) {
			try {
				tree = clazz.newInstance();
			} catch (Throwable e) {
				throw new CoreException(e);
			}
		} else
			tree.clear();
		List<V> list = new ArrayList<V>();
		list.addAll(super.items);
		Collections.sort(list, new Comparator<V>() {
			@Override
			public int compare(V o1, V o2) {
				return o1.getIdent() - o2.getIdent();
			}
		});
		tree.buildByList(list);
		// 更新结点相关属性
		List<Object[]> needUpdateList = new ArrayList<Object[]>();
		List<TreeNode<K, V>> allList = new ArrayList<TreeNode<K, V>>();
		tree.getAll(allList);
		for (int i = 0; i < allList.size(); i++) {
			TreeNode<K, V> node = allList.get(i);
			if (node.getValue() != null) {
				boolean needUpdate = false;
				if (node.getValue().getDepth().intValue() != node.getDepth()) {
					node.getValue().setDepth(node.getDepth());
					needUpdate = true;
				}
				if (node.getValue().getIdent() != i) {
					node.getValue().setIdent(i);
					needUpdate = true;
				}
				int endIdent = node.getValue().getIdent();
				if (node.getChildren().size() > 0) {
					for (int j = i + 1; j < allList.size(); j++) {
						TreeNode<K, V> node1 = (TreeNode<K, V>) allList.get(j);
						if (node1.getDepth() > node.getDepth()) {
							endIdent = j;
						} else
							break;
					}
				}
				if (node.getValue().getEndIdent() != endIdent) {
					node.getValue().setEndIdent(endIdent);
					needUpdate = true;
				}
				if (needUpdate) {
					needUpdateList.add(new Object[] {
							node.getValue().getDepth(),
							node.getValue().getIdent(),
							node.getValue().getEndIdent(),
							node.getValue().getId() });
				}
			}
			tree.sort(true);
		}
		if (!needUpdateList.isEmpty() && onChangeEventListener != null) {
			((LocalCachedTreeMapChangedEventListener) onChangeEventListener)
					.updateTreeDataToDatabase(this, needUpdateList);
		}
	}

	@Override
	protected void changed() throws Throwable {
		updateTreeNode();
		super.changed();
	}

	public TreeNode<K, V> getTree() {
		if (tree == null)
			try {
				tree = clazz.newInstance();
			} catch (Throwable e) {
				throw new CoreException(e);
			}
		refresh();
		return tree;
	}

}
