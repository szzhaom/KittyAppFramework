package kitty.kaf.dao.tools.datatypes;

import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.UnaryExpr.Operator;
import japa.parser.ast.stmt.Statement;

import java.sql.SQLException;
import java.util.LinkedList;
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

	public int getGafDataType() {
		if (dataType.equals("boolean")) {
			return 0;
		} else if (dataType.equals("byte")) {
			return 1;
		} else if (dataType.equals("short")) {
			return 2;
		} else if (dataType.equals("int")) {
			return 3;
		} else if (dataType.equals("long")) {
			return 4;
		} else if (dataType.equals("float")) {
			return 5;
		} else if (dataType.equals("double")) {
			return 6;
		} else if (dataType.equals("date")) {
			return 7;
		} else {
			return 8;
		}
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

	public Expression getDefaultInit(String def, ClassGenerator generator) {
		if (customJavaClassName != null) {
			if (def == null)
				def = this.column.getDef();
			if (def != null) {
				List<Expression> args = new LinkedList<Expression>();
				args.add(new IntegerLiteralExpr(def));
				return new MethodCallExpr(new NameExpr(customJavaClassName), "valueOf", args);
			} else
				return null;
		} else
			return doGetDefaultInit(def, generator);
	}

	protected abstract Expression doGetDefaultInit(String def, ClassGenerator generator);

	protected MethodCallExpr getRequestGetParameterCode(String columnName, String getMethodName,
			ClassGenerator generator) {
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr(columnName));
		if (this.column.getDef() != null && !this.column.getDef().trim().isEmpty()) {
			args.add(new BooleanLiteralExpr(true));
		} else if (column.isAutoIncrement() || column.isNullable()) {
			args.add(new BooleanLiteralExpr(true));
		} else if (column.isEditEmptyNotChange())
			args.add(new UnaryExpr(new NameExpr("isCreate"), Operator.not));
		else
			args.add(new BooleanLiteralExpr(false));
		args.add(new MethodCallExpr(null, column.getDataType().getGetMethodName(column.getVarName())));
		return new MethodCallExpr(new NameExpr("request"), getMethodName, args);
	}

	public String getGetMethodName(String varName) {
		return "get" + StringHelper.firstWordCap(varName);
	}

	public String getSetMethodName(String varName) {
		return "set" + StringHelper.firstWordCap(varName);
	}

	public boolean isDefaultSame(String def, String def1) {
		if (def == null)
			return def1 == null;
		else
			return def.equals(def1);
	}
}
