package kitty.kaf.dao.tools.datatypes;

import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.Statement;

import java.util.List;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.cg.ClassGenerator;

/**
 * 专用于从数据库中提取字段类型时用，这个时候dataType即dbDataType
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class DbColumnDataType extends ColumnDataType {

	public DbColumnDataType(Column column, String dbDataType) {
		super(column, dbDataType, null);
	}

	@Override
	public String toJavaClassName() {
		return null;
	}

	@Override
	public String getDbDataType() {
		return dataType;
	}

	@Override
	public Statement generateGetIdStringCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Statement generateSetIdStringCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Statement> generateCompareIdCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MethodCallExpr generateReadFromStreamCode(MethodCallExpr stmt) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MethodCallExpr generateWriteToStreamCode(MethodCallExpr stmt) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MethodCallExpr generateReadFromDbCode(MethodCallExpr stmt, String columnName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MethodCallExpr generateReadFromRequestCode(MethodCallExpr stmt, String columnName, ClassGenerator generator) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getShortName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MethodCallExpr generateForeignVarReadFromStreamCode(MethodCallExpr stmt) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MethodCallExpr generateForeignVarWriteToStreamCode(MethodCallExpr stmt) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MethodCallExpr generateForeignVarReadFromRequestCode(MethodCallExpr stmt, String columnName,
			ClassGenerator generator) {
		throw new UnsupportedOperationException();
	}
}
