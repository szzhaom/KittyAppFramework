package kitty.kaf.dao.tools.datatypes;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;

import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.cg.ClassGenerator;

public class DateColumnDataType extends ColumnDataType {

	public DateColumnDataType(Column column, String dataType, String customJavaClassName) {
		super(column, dataType, customJavaClassName);
	}

	@Override
	public String toJavaClassName() {
		return "Date";
	}

	@Override
	public String getDbDataType() {
		return "datetime";
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
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(new MethodCallExpr(new NameExpr("stream"), "readDate"));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public MethodCallExpr generateWriteToStreamCode(MethodCallExpr stmt) {
		List<Expression> ls = new LinkedList<Expression>();
		ls.add(stmt);
		return new MethodCallExpr(new NameExpr("stream"), "writeDate", ls);
	}

	@Override
	public MethodCallExpr generateReadFromDbCode(MethodCallExpr stmt, String columnName) {
		List<Expression> ls = new LinkedList<Expression>();
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr(columnName));
		ls.add(new MethodCallExpr(new NameExpr("rset"), "getDate", args));
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
			String d = column.getDef().trim();
			if (d.equalsIgnoreCase("${now}"))
				args.add(new ObjectCreationExpr(null, new ClassOrInterfaceType("Date"), null));
			else {
				generator.addImport("kitty.kaf.helper.StringHelper");
				List<Expression> args1 = new LinkedList<Expression>();
				args1.add(new StringLiteralExpr(d));
				args.add(new MethodCallExpr(new NameExpr("StringHelper"), "parseDateTime", args1));
			}
			def = "Def";
		} else if (column.isAutoIncrement()) {
			args.add(new NullLiteralExpr());
			def = "Def";
		}
		ls.add(new MethodCallExpr(new NameExpr("request"), "getParameterDate" + def, args));
		stmt.setArgs(ls);
		return stmt;
	}

	@Override
	public String getShortName() {
		return "Date";
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
