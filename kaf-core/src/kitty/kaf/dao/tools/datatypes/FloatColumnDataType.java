package kitty.kaf.dao.tools.datatypes;

import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;

import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.cg.ClassGenerator;
import kitty.kaf.helper.StringHelper;

public class FloatColumnDataType extends ColumnDataType {
	public FloatColumnDataType(Column column, String dataType, String customJavaClassName) {
		super(column, dataType, customJavaClassName);
	}

	@Override
	public String toJavaClassName() {
		return "Float";
	}

	@Override
	public String getDbDataType() {
		return "float";
	}

	@Override
	public Statement generateGetIdStringCode() {
		MethodCallExpr callExpr = new MethodCallExpr(null, "getId");
		List<Expression> ps = new LinkedList<Expression>();
		ps.add(callExpr);
		Statement st = new ReturnStmt(new MethodCallExpr(new NameExpr("Float"), "toString", ps));
		return st;
	}

	@Override
	public Statement generateSetIdStringCode() {
		List<Expression> ps = new LinkedList<Expression>();
		ps.add(new NameExpr("v"));
		MethodCallExpr callExpr = new MethodCallExpr(new NameExpr("Float"), "valueOf", ps);
		ps = new LinkedList<Expression>();
		ps.add(callExpr);
		MethodCallExpr expr = new MethodCallExpr(null, "setId", ps);
		ExpressionStmt st = new ExpressionStmt(expr);
		return st;
	}

	@Override
	public List<Statement> generateCompareIdCode() {
		List<Statement> ret = new LinkedList<Statement>();
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(new NameExpr("id2"));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("id1"), "compareTo", ls);
		ret.add(new ReturnStmt(mc));
		return ret;
	}

	@Override
	public MethodCallExpr generateReadFromStreamCode(MethodCallExpr stmt) {
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(new MethodCallExpr(new NameExpr("stream"), "readFloat"));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public MethodCallExpr generateWriteToStreamCode(MethodCallExpr stmt) {
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(stmt);
		return new MethodCallExpr(new NameExpr("stream"), "writeFloat", ls);
	}

	@Override
	public MethodCallExpr generateReadFromDbCode(MethodCallExpr stmt, String columnName) {
		List<Expression> ls = new LinkedList<Expression>();
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr(columnName));
		ls.add(new MethodCallExpr(new NameExpr("rset"), "getFloat", args));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public MethodCallExpr generateReadFromRequestCode(MethodCallExpr stmt, String columnName, ClassGenerator generator) {
		List<Expression> ls = new LinkedList<Expression>();
		MethodCallExpr expr = getRequestGetParameterCode(columnName, "getParameterFloat", generator);
		ls.add(new MethodCallExpr(new NameExpr("tableDef"), "test", new StringLiteralExpr(StringHelper
				.toVarName(columnName)), expr, new NameExpr("isCreate")));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public String getShortName() {
		return "Float";
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

	@Override
	protected Expression doGetDefaultInit(String def, ClassGenerator generator) {
		if (def == null)
			def = this.column.getDef();
		if (def != null) {
			return new DoubleLiteralExpr(def.trim());
		} else
			return null;
	}

	@Override
	public boolean isDefaultSame(String def, String def1) {
		Float f = Float.valueOf(def);
		Float f1 = Float.valueOf(def1);
		return f.equals(f1);
	}
}
