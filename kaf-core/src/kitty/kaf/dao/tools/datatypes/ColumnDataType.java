package kitty.kaf.dao.tools.datatypes;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.Statement;

import java.sql.SQLException;
import java.util.List;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.cg.ClassGenerator;
import kitty.kaf.helper.StringHelper;

/**
 * 用于生成代码的类定义
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
abstract public class ColumnDataType {
	String dataType;
	Column column;
	String customJavaClassName;

	public ColumnDataType(Column column, String dataType, String customJavaClassName) {
		super();
		this.column = column;
		this.dataType = dataType;
		this.customJavaClassName = customJavaClassName;
	}

	static public ColumnDataType getColumnDataType(Column column, String dataType, String customJavaClassName)
			throws SQLException {
		if (dataType.equals("long")) {
			return new LongColumnDataType(column, dataType, customJavaClassName);
		} else if (dataType.equals("int")) {
			return new IntegerColumnDataType(column, dataType, customJavaClassName);
		} else if (dataType.equals("short")) {
			return new ShortColumnDataType(column, dataType, customJavaClassName);
		} else if (dataType.equals("byte")) {
			return new ByteColumnDataType(column, dataType, customJavaClassName);
		} else if (dataType.equals("boolean")) {
			return new BooleanColumnDataType(column, dataType, customJavaClassName);
		} else if (dataType.equals("float")) {
			return new FloatColumnDataType(column, dataType, customJavaClassName);
		} else if (dataType.equals("double")) {
			return new DoubleColumnDataType(column, dataType, customJavaClassName);
		} else if (dataType.equals("string")) {
			return new StringColumnDataType(column, dataType, customJavaClassName);
		} else if (dataType.equals("date")) {
			return new DateColumnDataType(column, dataType, customJavaClassName);
		} else
			throw new SQLException("Unsupported Data Types [" + dataType + "]");
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public String getCustomJavaClassName() {
		return customJavaClassName;
	}

	public void setCustomJavaClassName(String customJavaClassName) {
		this.customJavaClassName = customJavaClassName;
	}

	public String getJavaClassName() {
		if (customJavaClassName != null)
			return customJavaClassName;
		else
			return toJavaClassName();
	}

	/**
	 * 获取字段的Java类名
	 */
	abstract public String toJavaClassName();

	abstract public String getDbDataType();

	abstract public Statement generateGetIdStringCode();

	abstract public Statement generateSetIdStringCode();

	abstract public List<Statement> generateCompareIdCode();

	abstract public MethodCallExpr generateReadFromStreamCode(MethodCallExpr stmt);

	abstract public MethodCallExpr generateWriteToStreamCode(MethodCallExpr stmt);

	abstract public MethodCallExpr generateReadFromDbCode(MethodCallExpr stmt, String columnName);

	abstract public MethodCallExpr generateReadFromRequestCode(MethodCallExpr stmt, String columnName,
			ClassGenerator generator);

	abstract public String getShortName();

	abstract public MethodCallExpr generateForeignVarReadFromStreamCode(MethodCallExpr stmt);

	abstract public MethodCallExpr generateForeignVarWriteToStreamCode(MethodCallExpr stmt);

	abstract public MethodCallExpr generateForeignVarReadFromRequestCode(MethodCallExpr stmt, String columnName,
			ClassGenerator generator);

	abstract public Expression getDefaultInit(String def);

	public String getGetMethodName(String varName) {
		return "get" + StringHelper.firstWordCap(varName);
	}

	public String getSetMethodName(String varName) {
		return "set" + StringHelper.firstWordCap(varName);
	}
}
