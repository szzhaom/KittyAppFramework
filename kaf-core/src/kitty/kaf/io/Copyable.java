package kitty.kaf.io;

import java.io.Serializable;

/**
 * 具备数据复制能力的接口
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public interface Copyable<E> extends Serializable {
	/**
	 * 从other中拷贝数据到接口中
	 * 
	 * @param other
	 *            拷贝源
	 */
	void copyData(E other);

	/**
	 * 返回一份拷贝的对象，数据完全一致
	 * 
	 * @return 拷贝的对象
	 */
	E copy();
}
