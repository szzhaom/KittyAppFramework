package kitty.kaf.io;

import java.io.Serializable;

/**
 * 包含有唯一键属性的接口
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public interface UniqueKeyable extends Serializable {
	/**
	 * 获取唯一键
	 */
	String getUniqueKey();

	/**
	 * 设置唯一键
	 * 
	 * @param v
	 *            新键
	 */
	void setUniqueKey(String v);
}
