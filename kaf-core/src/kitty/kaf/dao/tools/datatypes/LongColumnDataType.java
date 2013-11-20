package kitty.kaf.dao.tools.datatypes;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;

import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.cg.ClassGenerator;

public class LongColumnDataType extends ColumnDataType {
	public LongColumnDataType(Column column, String dataType, String customJavaClassName) {
		super(column, dataType, customJavaClassName);
	}

	@Override
	public String toJavaClassName() {
		return "Long";
	}

	@Override
	public String getDbDataType() {
		return "bigint";
	}

	@Override
	public Statement generateGetIdStringCode() {
		MethodCallExpr callExpr = new MethodCallExpr(null, "getId");
		List<Expression> ps = new LinkedList<Expression>();
		ps.add(callExpr);
		Statement st = new ReturnStmt(new MethodCallExpr(new NameExpr("Long"), "toString", ps));
		return st;
	}

	@Override
	public Statement generateSetIdStringCode() {
		List<Expression> ps = new LinkedList<Expression>();
		ps.add(new NameExpr("v"));
		MethodCallExpr callExpr = new MethodCallExpr(new NameExpr("Long"), "valueOf", ps);
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
		ls.add(new MethodCallExpr(new NameExpr("stream"), "readLong"));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public MethodCallExpr generateWriteToStreamCode(MethodCallExpr stmt) {
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(stmt);
		return new MethodCallExpr(new NameExpr("stream"), "writeLong", ls);
	}

	@Override
	public MethodCallExpr generateReadFromDbCode(MethodCallExpr stmt, String columnName) {
		List<Expression> ls = new LinkedList<Expression>();
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr(columnName));
		ls.add(new MethodCallExpr(new NameExpr("rset"), "getLong", args));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public MethodCallExpr generateReadFromRequestCode(MethodCallExpr stmt, String columnName, ClassGenerator generator) {
		List<Expression> ls = new LinkedList<Expression>();
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr(columnName));
		String def = "";
		if (this.column.getDef() != null && !this.column.getDef().trim().isEmpty()) {
			args.add(new LongLiteralExpr(this.column.getDef().trim()+"L"));
			def = "Def";
		} else if (column.isAutoIncrement()) {
			args.add(new NullLiteralExpr());
			def = "Def";
		}
		ls.add(new MethodCallExpr(new NameExpr("request"), "getParameterLong" + def, args));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public String getShortName() {
		return "Long";
	}
}
