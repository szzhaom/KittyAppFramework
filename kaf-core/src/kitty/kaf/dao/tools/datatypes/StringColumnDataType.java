package kitty.kaf.dao.tools.datatypes;

import japa.parser.ast.expr.Expression;
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
import kitty.kaf.helper.StringHelper;

public class StringColumnDataType extends ColumnDataType {
	public StringColumnDataType(Column column, String dataType, String customJavaClassName) {
		super(column, dataType, customJavaClassName);
	}

	@Override
	public String toJavaClassName() {
		return "String";
	}

	@Override
	public String getDbDataType() {
		if (column.getMinLength() == column.getLength())
			return "char";
		else {
			String type = column.getDaoSource().getType();
			if (type.equalsIgnoreCase("mysql")) {
				if (column.getLength() <= 255)
					return "varchar";
				else {
					column.setLength(65535);
					column.setSqllength(0);
					return "text";
				}
			} else if (type.equalsIgnoreCase("oracle")) {
				if (column.getLength() < 4000)
					return "varchar2";
				else {
					column.setSqllength(0);
					return "clob";
				}
			}
			return "varchar";
		}
	}

	@Override
	public Statement generateGetIdStringCode() {
		Statement st = new ReturnStmt(new MethodCallExpr(null, "getId"));
		return st;
	}

	@Override
	public Statement generateSetIdStringCode() {
		List<Expression> ps = new LinkedList<Expression>();
		ps.add(new NameExpr("v"));
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
		String s;
		if (column.getLength() <= 255)
			s = "readPacketByteLenString";
		else if (column.getLength() <= 65535)
			s = "readPacketShortLenString";
		else
			s = "readPacketInttLenString";
		ls.add(new MethodCallExpr(new NameExpr("stream"), s));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public MethodCallExpr generateWriteToStreamCode(MethodCallExpr stmt) {
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(stmt);
		String s;
		if (column.getLength() <= 255)
			s = "writePacketByteLenString";
		else if (column.getLength() <= 65535)
			s = "writePacketShortLenString";
		else
			s = "writePacketInttLenString";
		return new MethodCallExpr(new NameExpr("stream"), s, ls);
	}

	@Override
	public MethodCallExpr generateReadFromDbCode(MethodCallExpr stmt, String columnName) {
		List<Expression> ls = new LinkedList<Expression>();
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr(columnName));
		ls.add(new MethodCallExpr(new NameExpr("rset"), "getString", args));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public MethodCallExpr generateReadFromRequestCode(MethodCallExpr stmt, String columnName, ClassGenerator generator) {
		List<Expression> ls = new LinkedList<Expression>();
		MethodCallExpr expr = getRequestGetParameterCode(columnName, "getParameter", generator);
		if (this.column.isMd5()) {
			generator.addImport("kitty.kaf.helper.SecurityHelper");
			expr = new MethodCallExpr(new NameExpr("SecurityHelper"), "md5", expr);
		}
		ls.add(new MethodCallExpr(new NameExpr("tableDef"), "test", new StringLiteralExpr(StringHelper
				.toVarName(columnName)), expr, new NameExpr("isCreate")));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public String getShortName() {
		return "String";
	}

	@Override
	public MethodCallExpr generateForeignVarReadFromStreamCode(MethodCallExpr stmt) {
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(new MethodCallExpr(new NameExpr("stream"), "readPacketByteLenStringList"));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public MethodCallExpr generateForeignVarWriteToStreamCode(MethodCallExpr stmt) {
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(stmt);
		return new MethodCallExpr(new NameExpr("stream"), "writePacketByteLenStringList", ls);
	}

	@Override
	public MethodCallExpr generateForeignVarReadFromRequestCode(MethodCallExpr stmt, String columnName,
			ClassGenerator generator) {
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(new MethodCallExpr(new NameExpr("request"), "getParameterDef", new StringLiteralExpr(columnName),
				new NullLiteralExpr()));
		ls.add(new StringLiteralExpr(","));
		List<Expression> as = new LinkedList<Expression>();
		as.add(new MethodCallExpr(new NameExpr("StringHelper"), "splitToStringList", ls));
		stmt.setArgs(as);
		return stmt;
	}

	@Override
	protected Expression doGetDefaultInit(String def, ClassGenerator generator) {
		if (def == null)
			def = this.column.getDef();
		if (def != null) {
			return new StringLiteralExpr(def);
		} else
			return null;
	}
}
