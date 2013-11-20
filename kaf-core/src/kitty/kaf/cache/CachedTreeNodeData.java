package kitty.kaf.cache;

import java.io.Serializable;

import kitty.kaf.io.TreeNodeData;

public interface CachedTreeNodeData<E extends Serializable> extends
		LocalCachable<E>, TreeNodeData<E> {

	public void setDepth(Integer depth);

	public Integer getIdent();

	public Integer getEndIdent();

	public void setIdent(Integer v);

	public void setEndIdent(Integer v);

	public Integer getOrderNo();

	public void setOrderNo(Integer v);
}
