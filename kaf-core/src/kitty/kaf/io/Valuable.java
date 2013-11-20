package kitty.kaf.io;

import java.io.Serializable;

/**
 * 包含有value属性的接口
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 * @param <E>
 */
public interface Valuable<E> extends Serializable {
	/**
	 * 获取value
	 */
	E getValue();

	/**
	 * 设置value
	 * 
	 * @param v
	 *            新的value值
	 */
	void setValue(E v);
}
