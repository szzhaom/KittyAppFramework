package kitty.kaf.dao.table;

import java.util.Date;
import java.util.regex.Pattern;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.helper.StringHelper;

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
	ColumnDataType dataType;
	int length;
	int digits;
	String sequence;
	boolean isUiqueKeyField, nullable;
	boolean isToStringField;
	int index;
	boolean isSecret;
	int updateMode; // 0-添加修改；1-仅创建；2-创建修改;3-非空修改
	Pattern regExp;
	String errorPrompt;
	Object maxValue, minValue;
	int minLength;
	boolean isAutoIncrement;
	String serialKey;

	public TableColumnDef() {
		super();
	}

	public TableColumnDef(int index, String columnDesp, String columnName, int dataType, int length, int digits,
			boolean isUiqueKeyField, String sequence, boolean isSecret, int updateMode, boolean isToStringField,
			int minLength, String minValue, String maxValue, String errorPrompt, String regExp,
			boolean isAutoIncrement, boolean nullable, String serialKey) {
		super();
		this.columnDesp = columnDesp;
		this.columnName = columnName;
		this.dataType = ColumnDataType.valueOf(dataType);
		this.length = length;
		this.digits = digits;
		this.index = index;
		this.isUiqueKeyField = isUiqueKeyField;
		this.sequence = sequence;
		this.isSecret = isSecret;
		this.updateMode = updateMode;
		this.isToStringField = isToStringField;
		this.minLength = minLength;
		this.regExp = regExp != null && !regExp.trim().isEmpty() ? Pattern.compile(regExp.trim()) : null;
		this.errorPrompt = errorPrompt;
		this.isAutoIncrement = isAutoIncrement;
		this.nullable = nullable;
		this.serialKey = serialKey;
		switch (this.dataType) {
		case BYTE: {
			if (minValue != null) {
				this.minValue = Byte.valueOf(minValue);
			}
			if (maxValue != null) {
				this.maxValue = Byte.valueOf(maxValue);
			}
		}
			break;
		case SHORT: {
			if (minValue != null) {
				this.minValue = Short.valueOf(minValue);
			}
			if (maxValue != null) {
				this.maxValue = Short.valueOf(maxValue);
			}
		}
			break;
		case INT: {
			if (minValue != null) {
				this.minValue = Integer.valueOf(minValue);
			}
			if (maxValue != null) {
				this.maxValue = Integer.valueOf(maxValue);
			}
		}
			break;
		case LONG: {
			if (minValue != null) {
				this.minValue = Long.valueOf(minValue);
			}
			if (maxValue != null) {
				this.maxValue = Long.valueOf(maxValue);
			}
		}
			break;
		case FLOAT: {
			if (minValue != null) {
				this.minValue = Float.valueOf(minValue);
			}
			if (maxValue != null) {
				this.maxValue = Float.valueOf(maxValue);
			}
		}
			break;
		case DOUBLE: {
			if (minValue != null) {
				this.minValue = Double.valueOf(minValue);
			}
			if (maxValue != null) {
				this.maxValue = Double.valueOf(maxValue);
			}
		}
			break;
		case DATE: {
			if (minValue != null) {
				this.minValue = StringHelper.parseDateTime(minValue);
			}
			if (maxValue != null) {
				this.maxValue = StringHelper.parseDateTime(maxValue);
			}
		}
			break;
		case BOOLEAN: {
			if (minValue != null) {
				this.minValue = Boolean.valueOf(minValue);
			}
			if (maxValue != null) {
				this.maxValue = Boolean.valueOf(maxValue);
			}
		}
			break;
		default:
			break;
		}
	}

	public TableColumnDef(int index, String columnDesp, String columnName, ColumnDataType dataType, int length,
			int digits) {
		super();
		this.columnDesp = columnDesp;
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.digits = digits;
		this.index = index;
	}

	public TableColumnDef(int index, String columnDesp, String columnName, ColumnDataType dataType, int length,
			int digits, boolean isUiqueKeyField) {
		super();
		this.columnDesp = columnDesp;
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.digits = digits;
		this.isUiqueKeyField = isUiqueKeyField;
		this.index = index;
	}

	public TableColumnDef(int index, String columnDesp, String columnName, ColumnDataType dataType, int length,
			int digits, String sequence) {
		super();
		this.columnDesp = columnDesp;
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.digits = digits;
		this.sequence = sequence;
		this.index = index;
	}

	public String getSerialKey() {
		return serialKey;
	}

	public void testByte(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			Byte v = value == null ? null : (value instanceof Byte ? (Byte) value : Byte.valueOf(value.toString()));
			if (v == null) {
				if (minLength > 0)
					throw new CoreException(columnDesp + " -> 不能为空");
			} else {
				if (maxValue != null) {
					if (v.compareTo((Byte) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数字越界");
				}
				if (minValue != null) {
					if (v.compareTo((Byte) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数据越界");
				}
				if (length > 0) {
					if (v.toString().length() > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是整数", e);
		}
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public void testShort(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			Short v = value == null ? null : (value instanceof Short ? (Short) value : Short.valueOf(value.toString()));
			if (v == null) {
				if (minLength > 0)
					throw new CoreException(columnDesp + " -> 不能为空");
			} else {
				if (maxValue != null) {
					if (v.compareTo((Short) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数字越界");
				}
				if (minValue != null) {
					if (v.compareTo((Short) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数据越界");
				}
				if (length > 0) {
					if (v.toString().length() > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是整数", e);
		}
	}

	public void testInt(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			Integer v = value == null ? null : (value instanceof Integer ? (Integer) value : Integer.valueOf(value
					.toString()));
			if (v == null) {
				if (minLength > 0)
					throw new CoreException(columnDesp + " -> 不能为空");
			} else {
				if (maxValue != null) {
					if (v.compareTo((Integer) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数字越界");
				}
				if (minValue != null) {
					if (v.compareTo((Integer) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数据越界");
				}
				if (length > 0) {
					if (v.toString().length() > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是整数", e);
		}
	}

	public void testLong(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			Long v = value == null ? null : (value instanceof Long ? (Long) value : Long.valueOf(value.toString()));
			if (v == null) {
				if (minLength > 0)
					throw new CoreException(columnDesp + " -> 不能为空");
			} else {
				if (maxValue != null) {
					if (v.compareTo((Long) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数字越界");
				}
				if (minValue != null) {
					if (v.compareTo((Long) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数据越界");
				}
				if (length > 0) {
					if (v.toString().length() > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是整数", e);
		}
	}

	public void testFloat(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			Float v = value == null ? null : (value instanceof Float ? (Float) value : Float.valueOf(value.toString()));
			if (v == null) {
				if (minLength > 0)
					throw new CoreException(columnDesp + " -> 不能为空");
			} else {
				if (maxValue != null) {
					if (v.compareTo((Float) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数字越界");
				}
				if (minValue != null) {
					if (v.compareTo((Float) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数据越界");
				}
				if (length > 0) {
					if (v.toString().length() > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是浮点数", e);
		}
	}

	public void testDouble(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			Double v = value == null ? null : (value instanceof Double ? (Double) value : Double.valueOf(value
					.toString()));
			if (v == null) {
				if (minLength > 0)
					throw new CoreException(columnDesp + " -> 不能为空");
			} else {
				if (maxValue != null) {
					if (v.compareTo((Double) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数字越界");
				}
				if (minValue != null) {
					if (v.compareTo((Double) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数据越界");
				}
				if (length > 0) {
					if (v.toString().length() > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是浮点数", e);
		}
	}

	public void testDate(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			Date v = value == null ? null : (value instanceof Date ? (Date) value : StringHelper.parseDateTime(value
					.toString()));
			if (v == null) {
				if (minLength > 0)
					throw new CoreException(columnDesp + " -> 不能为空");
			} else {
				if (maxValue != null) {
					if (v.compareTo((Date) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数字越界");
				}
				if (minValue != null) {
					if (v.compareTo((Date) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数据越界");
				}
				if (length > 0) {
					if (v.toString().length() > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是日期或时间", e);
		}
	}

	public void testBoolean(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			Boolean v = value == null ? null : (value instanceof Boolean ? (Boolean) value : Boolean.valueOf(value
					.toString()));
			if (v == null) {
				if (minLength > 0)
					throw new CoreException(columnDesp + " -> 不能为空");
			} else {
				if (maxValue != null) {
					if (v.compareTo((Boolean) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数字越界");
				}
				if (minValue != null) {
					if (v.compareTo((Boolean) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 数据越界");
				}
				if (length > 0) {
					if (v.toString().length() > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是布尔数据", e);
		}
	}

	public void testString(Object value, boolean isCreate) throws CoreException {
		try {
			if (isCreate && isAutoIncrement)
				return;
			String v = value == null ? null : (value instanceof String ? (String) value : value.toString());
			if (v == null) {
				if (!nullable && minLength > 0) {
					if (!(!isCreate && isSecret))
						throw new CoreException(columnDesp + " -> 不能为空");
				}
			} else {
				if (maxValue != null) {
					if (v.compareTo((String) maxValue) > 0)
						throw new CoreException(columnDesp + " -> 数据错误: 字符串越界");
				}
				if (minValue != null) {
					if (v.compareTo((String) minValue) < 0)
						throw new CoreException(columnDesp + " -> 数据错误: 字符串越界");
				}
				byte[] b = v.getBytes();
				if (length > 0) {
					if (b.length > length)
						throw new CoreException(columnDesp + " -> 数据错误: 数据太长");
				}
				if (((nullable && b.length > 0) || !nullable) && b.length < minLength)
					throw new CoreException(columnDesp + " -> 数据错误: 数据太短");
				if (regExp != null) {
					if (!regExp.matcher(v).matches())
						throw new CoreException(columnDesp + " -> 数据格式不匹配");
				}
			}
		} catch (CoreException e) {
			throw e;
		} catch (Throwable e) {
			throw new CoreException(columnDesp + " -> 格式错误: 必须是布尔数据", e);
		}
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

	public boolean isNullable() {
		return nullable;
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
	public ColumnDataType getDataType() {
		return dataType;
	}

	/**
	 * 设置数据类型
	 * 
	 * @param v
	 *            新数据类型
	 */
	public void setDataType(ColumnDataType v) {
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

	public Pattern getRegExp() {
		return regExp;
	}

	public void setRegExp(Pattern regExp) {
		this.regExp = regExp;
	}

	public String getErrorPrompt() {
		if (errorPrompt == null) {
			switch (dataType) {
			case BYTE:
			case SHORT:
			case INT:
			case LONG:
				errorPrompt = "数据错误: 必须是整数";
				break;
			case FLOAT:
			case DOUBLE:
				errorPrompt = "数据错误: 必须是浮点数";
				break;
			case DATE:
				errorPrompt = "数据错误: 必须是日期或时间";
				break;
			default:
				errorPrompt = "数据格式错误";
			}
		}
		return errorPrompt;
	}

	public void setErrorPrompt(String errorPrompt) {
		this.errorPrompt = errorPrompt;
	}

	public Object getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Object maxValue) {
		this.maxValue = maxValue;
	}

	public Object getMinValue() {
		return minValue;
	}

	public void setMinValue(Object minValue) {
		this.minValue = minValue;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

}
