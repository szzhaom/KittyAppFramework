package kitty.kaf.dao.resultset;

import java.io.Serializable;

/**
 * 数据库访问的参数
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class DaoParameter implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 参数的数据类型。
	 * 
	 * @see java.sql.Types
	 */
	int dataType;
	/**
	 * 参数值
	 */
	Object value;

	/**
	 * 构建DaoParameter
	 */
	public DaoParameter() {
		super();
	}

	/**
	 * 构建DaoParameter
	 * 
	 * @param dataType
	 *            参数的数据类型
	 * @param value
	 *            参数值
	 */
	public DaoParameter(int dataType, Object value) {
		super();
		this.dataType = dataType;
		this.value = value;
	}

	/**
	 * 获取参数的数据类型
	 */
	public int getDataType() {
		return dataType;
	}

	/**
	 * 设置参数的数据类型
	 * 
	 * @param dataType
	 *            参数的数据类型
	 */
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	/**
	 * 获取参数值
	 * 
	 * @return 参数值
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * 设置参数值
	 * 
	 * @param value
	 *            参数值
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}
