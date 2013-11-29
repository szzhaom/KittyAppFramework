package kitty.kaf.dao.table;

/**
 * 表的一列定义
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class TableColumnDef {
	String columnName, columnDesp;
	int dataType;
	int length;
	int digits;
	String sequence;
	boolean isUiqueKeyField;
	boolean isToStringField;
	int index;
	boolean isSecret;
	int updateMode; // 0-添加修改；1-仅创建；2-创建修改;3-非空修改

	public TableColumnDef() {
		super();
	}

	public TableColumnDef(int index, String columnDesp, String columnName, int dataType, int length, int digits,
			boolean isUiqueKeyField, String sequence, boolean isSecret, int updateMode, boolean isToStringField) {
		super();
		this.columnDesp = columnDesp;
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.digits = digits;
		this.index = index;
		this.isUiqueKeyField = isUiqueKeyField;
		this.sequence = sequence;
		this.isSecret = isSecret;
		this.updateMode = updateMode;
		this.isToStringField = isToStringField;
	}

	public TableColumnDef(int index, String columnDesp, String columnName, int dataType, int length, int digits) {
		super();
		this.columnDesp = columnDesp;
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.digits = digits;
		this.index = index;
	}

	public TableColumnDef(int index, String columnDesp, String columnName, int dataType, int length, int digits,
			boolean isUiqueKeyField) {
		super();
		this.columnDesp = columnDesp;
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.digits = digits;
		this.isUiqueKeyField = isUiqueKeyField;
		this.index = index;
	}

	public TableColumnDef(int index, String columnDesp, String columnName, int dataType, int length, int digits,
			String sequence) {
		super();
		this.columnDesp = columnDesp;
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.digits = digits;
		this.sequence = sequence;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(int updateMode) {
		this.updateMode = updateMode;
	}

	public boolean isToStringField() {
		return isToStringField;
	}

	public void setToStringField(boolean isToStringField) {
		this.isToStringField = isToStringField;
	}

	/**
	 * 获取列名
	 * 
	 * @return 列名
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * 设置列名
	 * 
	 * @param v
	 *            新列名
	 */
	public void setColumnName(String v) {
		this.columnName = v;
	}

	/**
	 * 获取数据类型
	 * 
	 * @return 数据类型
	 */
	public int getDataType() {
		return dataType;
	}

	/**
	 * 设置数据类型
	 * 
	 * @param v
	 *            新数据类型
	 */
	public void setDataType(int v) {
		dataType = v;
	}

	/**
	 * 获取列长度
	 * 
	 * @return 列长度
	 */
	public int getLength() {
		return length;
	}

	/**
	 * 设置列长度
	 * 
	 * @param v
	 *            列长度
	 */
	public void setLength(int v) {
		this.length = v;
	}

	/**
	 * 获取小数位数
	 * 
	 * @return 小数位数
	 */
	int getDigits() {
		return digits;
	}

	/**
	 * 设置小数位数
	 * 
	 * @param v
	 *            小数位数
	 */
	void setDigits(int v) {
		this.digits = v;
	}

	public String getColumnDesp() {
		return columnDesp;
	}

	public void setColumnDesp(String columnDesp) {
		this.columnDesp = columnDesp;
	}

	public boolean isUiqueKeyField() {
		return isUiqueKeyField;
	}

	public void setUiqueKeyField(boolean isUiqueKeyField) {
		this.isUiqueKeyField = isUiqueKeyField;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public boolean isSecret() {
		return isSecret;
	}

	public void setSecret(boolean isSecret) {
		this.isSecret = isSecret;
	}
}
