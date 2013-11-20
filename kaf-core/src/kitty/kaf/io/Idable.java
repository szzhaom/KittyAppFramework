package kitty.kaf.io;

import java.io.Serializable;

/**
 * 具备ID能力的数据
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * @param <E>
 */
public interface Idable<E> extends Serializable, Comparable<Idable<E>> {
	/**
	 * 获取ID
	 */
	E getId();

	/**
	 * 设置ID
	 * 
	 * @param id
	 *            新的ID
	 */
	void setId(E id);

	/**
	 * 获取id的字符值
	 */
	String getIdString();

	/**
	 * 通过解析字符串v，设置id
	 * 
	 * @param v
	 *            包含id的字符串
	 */
	void setIdString(String v);
}
