package kitty.kaf.io;

import java.io.Serializable;

/**
 * 包含有Text属性的接口
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 * @param <E>
 */
public interface Textable extends Serializable {
	/**
	 * 获取text
	 */
	String getText();

	/**
	 * 设置text
	 * 
	 * @param v
	 *            新的text值
	 */
	void setText(String v);
}
