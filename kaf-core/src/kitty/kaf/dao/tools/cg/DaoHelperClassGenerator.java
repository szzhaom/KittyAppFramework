package kitty.kaf.dao.tools.cg;

import japa.parser.ASTHelper;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.ForeignKey;
import kitty.kaf.dao.tools.Table;
import kitty.kaf.helper.JPHelper;
import kitty.kaf.helper.StringHelper;

/**
 * Bean 类生成器
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class DaoHelperClassGenerator extends ClassGenerator {
	Table table;
	String pkClass;
	List<Column> pkColumns = null;
	CodeGenerator generator;
	Column pkColumn;

	public DaoHelperClassGenerator(CodeGenerator generator, Table table) {
		super();
		this.generator = generator;
		this.table = table;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		String path = generator.workspaceDir + def.getEjbProjectName() + "/src/"
				+ def.getDaoPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + table.getJavaClassName() + "DaoHelper.java";
		classFile = new File(fileName);
		if (!classFile.getParentFile().exists())
			classFile.getParentFile().mkdirs();
		if (classFile.exists())
			cu = parse(classFile);
		if (cu == null)
			cu = new CompilationUnit();
		if (cu.getImports() == null)
			cu.setImports(new LinkedList<ImportDeclaration>());
		if (cu.getTypes() == null)
			cu.setTypes(new LinkedList<TypeDeclaration>());
		if (cu.getComments() == null)
			cu.setComments(new LinkedList<Comment>());
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(def.getDaoPackageName())));

		pkClass = null;
		if (table.getPk() != null) {
			try {
				pkColumns = table.getPk().getTableColumns();
				if (pkColumns.size() == 1) {
					pkColumn = pkColumns.get(0);
					pkClass = pkColumns.get(0).getDataType().getJavaClassName();
				}
			} catch (SQLException e) {
				throw new IOException(e);
			}
		}
		addImport("java.sql.SQLException");
		addImport("kitty.kaf.dao.Dao");
		addImport("java.util.List");
		addImport("kitty.kaf.io.KeyValue");
		addImport("kitty.kaf.cache.CacheValueList");
		addImport("java.util.Date");
		addImport(table.getFullBeanClassName());
		if (table.getMemcachedConfig() != null
				|| (table.getLocalCache() != null && !table.getLocalCache().trim().isEmpty())) {
			addImport(table.getFullHelperClassName());
		}
		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu, table.getJavaClassName() + "DaoHelper",
				false, ModifierSet.PUBLIC);
		return type;
	}

	public void generateBody() throws IOException, ParseException {
		generateDeleteCode();
		generateFindByIdCode();
		generateFindByUniqueKeyCode();
		generateInsertCode();
		generateEditCode();
		generateQueryCode();
		generateQueryPageCode();
		generateExecuteCode();
		generateQueryLatestCode();
	}

	private void generateQueryLatestCode() {
		ClassOrInterfaceType type = new ClassOrInterfaceType("CacheValueList");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		type.getTypeArgs().add(new WildcardType());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, "queryLatest",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters()
				.add(new Parameter(new PrimitiveType(Primitive.Long), new VariableDeclaratorId("firstIndex")));
		md.getParameters().add(new Parameter(new PrimitiveType(Primitive.Int), new VariableDeclaratorId("maxResults")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Date")), new VariableDeclaratorId(
						"lastModified")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new NullLiteralExpr()));
		type = new ClassOrInterfaceType("CacheValueList");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		type.getTypeArgs().add(new WildcardType());
		ExpressionStmt stmt = new ExpressionStmt(new VariableDeclarationExpr(type, vars));
		ls.add(stmt);
		List<Statement> oldList = findStatementsAtKeywordComment(md.getBody().getStmts(),
				"autogenerated:body(queryLastest)");
		IfStmt ifStmt = null;
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("default"));
		Expression condition = new MethodCallExpr(new NameExpr("cmd"), "equalsIgnoreCase", args);
		args = new LinkedList<Expression>();
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName()))));
		args.add(new StringLiteralExpr(table.getName() + " a"));
		args.add(new StringLiteralExpr(""));
		args.add(new StringLiteralExpr(""));
		args.add(new NameExpr("firstIndex"));
		args.add(new NameExpr("maxResults"));
		args.add(new NameExpr("lastModified"));
		AssignExpr ae = new AssignExpr(new NameExpr("ret"),
				new MethodCallExpr(new NameExpr("dao"), "queryLatest", args), AssignExpr.Operator.assign);
		Statement thenStmt = new ExpressionStmt(ae), elseStmt = null;
		if (oldList != null && oldList.size() > 1 && oldList.get(1) instanceof IfStmt) {
			ifStmt = (IfStmt) oldList.get(1);
			if (JPHelper.findIfStmtByCondition(ifStmt, condition) == null) {
				elseStmt = ifStmt;
				ifStmt = null;
			}
		}
		if (ifStmt == null)
			ifStmt = new IfStmt(condition, thenStmt, elseStmt);
		ls.add(ifStmt);
		autoGenerateStatements(md.getBody(), "autogenerated:body(queryLastest)", ls);
		ls = new LinkedList<Statement>();
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(queryLastest)", ls);
	}

	private void generateExecuteCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType("Object")), "execute", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(new ReferenceType(type), new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new NullLiteralExpr()));

		ExpressionStmt stmt = new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType("Object")), vars));
		ls.add(stmt);
		autoGenerateStatements(md.getBody(), "autogenerated:body(execute)", ls);
		ls = new LinkedList<Statement>();
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(execute)", ls);
	}

	private void generateQueryPageCode() {
		ClassOrInterfaceType type = new ClassOrInterfaceType("KeyValue");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("Integer")));
		ClassOrInterfaceType type1 = new ClassOrInterfaceType("List");
		type1.setTypeArgs(new LinkedList<Type>());
		type1.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		type.getTypeArgs().add(type1);
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, "queryPage",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters()
				.add(new Parameter(new PrimitiveType(Primitive.Long), new VariableDeclaratorId("firstIndex")));
		md.getParameters().add(new Parameter(new PrimitiveType(Primitive.Int), new VariableDeclaratorId("maxResults")));
		md.getParameters().add(new Parameter(new ReferenceType(type), new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new NullLiteralExpr()));
		type = new ClassOrInterfaceType("KeyValue");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("Integer")));
		type1 = new ClassOrInterfaceType("List");
		type1.setTypeArgs(new LinkedList<Type>());
		type1.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		type.getTypeArgs().add(type1);
		ExpressionStmt stmt = new ExpressionStmt(new VariableDeclarationExpr(type, vars));
		ls.add(stmt);
		List<Statement> oldList = findStatementsAtKeywordComment(md.getBody().getStmts(),
				"autogenerated:body(queryPage)");
		IfStmt ifStmt = null;
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("default"));
		Expression condition = new MethodCallExpr(new NameExpr("cmd"), "equalsIgnoreCase", args);
		args = new LinkedList<Expression>();
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName()))));
		args.add(new StringLiteralExpr("from " + table.getName() + " a where a.is_deleted=0"));
		args.add(new StringLiteralExpr(""));
		args.add(new NameExpr("firstIndex"));
		args.add(new NameExpr("maxResults"));
		List<Expression> args1 = new LinkedList<Expression>();
		args1.add(new IntegerLiteralExpr("0"));
		args.add(new MethodCallExpr(new NameExpr("params"), "get", args1));
		AssignExpr ae = new AssignExpr(new NameExpr("ret"), new MethodCallExpr(new NameExpr("dao"), "keywordQuery",
				args), AssignExpr.Operator.assign);
		Statement thenStmt = new ExpressionStmt(ae), elseStmt = null;
		if (oldList != null && oldList.size() > 1 && oldList.get(1) instanceof IfStmt) {
			ifStmt = (IfStmt) oldList.get(1);
			if (JPHelper.findIfStmtByCondition(ifStmt, condition) == null) {
				elseStmt = ifStmt;
				ifStmt = null;
			} else
				ifStmt.setThenStmt(thenStmt);
		}
		if (ifStmt == null)
			ifStmt = new IfStmt(condition, thenStmt, elseStmt);
		ls.add(ifStmt);
		autoGenerateStatements(md.getBody(), "autogenerated:body(queryPage)", ls);
		ls = new LinkedList<Statement>();
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(queryPage)", ls);
	}

	private void generateQueryCode() {
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, "query",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(new PrimitiveType(Primitive.Int), new VariableDeclaratorId("maxResults")));
		md.getParameters().add(new Parameter(new ReferenceType(type), new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new NullLiteralExpr()));
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		ExpressionStmt stmt = new ExpressionStmt(new VariableDeclarationExpr(type, vars));
		ls.add(stmt);
		List<Statement> oldList = findStatementsAtKeywordComment(md.getBody().getStmts(), "autogenerated:body(query)");
		IfStmt ifStmt = null;
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("default"));
		Expression condition = new MethodCallExpr(new NameExpr("cmd"), "equalsIgnoreCase", args);
		args = new LinkedList<Expression>();
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName()))));
		args.add(new StringLiteralExpr("from " + table.getName() + " a where a.is_delete=0"));
		args.add(new StringLiteralExpr(""));
		args.add(new NameExpr("maxResults"));
		AssignExpr ae = new AssignExpr(new NameExpr("ret"), new MethodCallExpr(new NameExpr("dao"), "query", args),
				AssignExpr.Operator.assign);
		Statement thenStmt = new ExpressionStmt(ae), elseStmt = null;
		if (oldList != null && oldList.size() > 1 && oldList.get(1) instanceof IfStmt) {
			ifStmt = (IfStmt) oldList.get(1);
			if (JPHelper.findIfStmtByCondition(ifStmt, condition) == null) {
				elseStmt = ifStmt;
				ifStmt = null;
			} else
				ifStmt.setThenStmt(thenStmt);
		}
		if (ifStmt == null)
			ifStmt = new IfStmt(condition, thenStmt, elseStmt);
		ls.add(ifStmt);
		autoGenerateStatements(md.getBody(), "autogenerated:body(query)", ls);
		ls = new LinkedList<Statement>();
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(query)", ls);
	}

	private void generateFindByUniqueKeyCode() {
		Column uk = table.getUniqueKeyColumn();
		if (uk == null)
			return;
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), "findByUniqueKey", new LinkedList<Parameter>(),
				null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(uk.getDataType().getJavaClassName())),
						new VariableDeclaratorId("keyCode")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("dao"), "findByUniqueKey", new LinkedList<Expression>());
		mce.getArgs().add(new ClassExpr(new ClassOrInterfaceType(table.getJavaClassName())));
		mce.getArgs().add(new NameExpr("keyCode"));
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), mce));
		ExpressionStmt stmt = new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), vars));
		ls.add(stmt);
		autoGenerateStatements(md.getBody(), "autogenerated:body(findByUniqueKey)", ls);
		ls = new LinkedList<Statement>();
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findByUniqueKey)", ls);
	}

	private void generateEditCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), "edit", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
						new VariableDeclaratorId("o")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("dao"), "edit", new LinkedList<Expression>());
		mce.getArgs().add(new NameExpr("o"));
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), mce));
		ExpressionStmt stmt = new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), vars));
		ls.add(stmt);
		if (table.getForeignGenVars().size() > 0) {
			for (ForeignKey fk : table.getForeignGenVars()) {
				if (fk.getIdListVarName() != null) {
					String idGetVarName = "get"
							+ StringHelper.firstWordCap(StringHelper.toVarName(fk.getIdListVarName()));
					List<Statement> stmts = new LinkedList<Statement>();
					IfStmt ifStmt = new IfStmt(new BinaryExpr(new MethodCallExpr(new NameExpr("o"), idGetVarName),
							new NullLiteralExpr(), Operator.notEquals), new BlockStmt(stmts), null);
					ls.add(ifStmt);
					List<Expression> args = new LinkedList<Expression>();
					stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("dao"), "execute", args)));
					args.add(new StringLiteralExpr("delete from " + fk.getTable().getName() + " where "
							+ fk.getVarBindColumn() + "=?"));
					args.add(new MethodCallExpr(new NameExpr("o"), "getId"));
					List<VariableDeclarator> ves = new LinkedList<VariableDeclarator>();
					ves.add(new VariableDeclarator(new VariableDeclaratorId("id")));
					args = new LinkedList<Expression>();
					args.add(new StringLiteralExpr("insert into " + fk.getTable().getName() + "("
							+ fk.getVarBindColumn() + "," + fk.getColumn()
							+ ",last_modified_time,creation_time) values(?,?,${now},${now})"));
					args.add(new MethodCallExpr(new NameExpr("o"), "getId"));
					args.add(new NameExpr("id"));
					ForeachStmt fstmt = new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(
							new ClassOrInterfaceType("Object")), ves), new MethodCallExpr(new NameExpr("o"),
							idGetVarName), new ExpressionStmt(new MethodCallExpr(new NameExpr("dao"), "execute", args)));
					stmts.add(fstmt);
				}
			}
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(edit)", ls);
		ls = new LinkedList<Statement>();
		if (table.getMemcachedConfig() != null) {
			List<Expression> args = new LinkedList<Expression>();
			args.add(new MethodCallExpr(new NameExpr("o"), "getId"));
			mce = new MethodCallExpr(new FieldAccessExpr(new NameExpr(table.getJavaClassName() + "Helper"),
					StringHelper.firstWordLower(table.getJavaClassName()) + "Map"), "remove", args);
			ls.add(new ExpressionStmt(mce));
		}
		if (table.getLocalCache() != null && !table.getLocalCache().trim().isEmpty()) {
			mce = new MethodCallExpr(new FieldAccessExpr(new NameExpr(table.getJavaClassName() + "Helper"), "local"
					+ table.getJavaClassName() + "Map"), "setSourceLastModified", new MethodCallExpr(
					new NameExpr("dao"), "getLastModified", new ClassExpr(new ReferenceType(new ClassOrInterfaceType(
							table.getJavaClassName()))), new MethodCallExpr(new NameExpr("ret"), "getId")));
			ls.add(new ExpressionStmt(mce));
		}
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(edit)", ls);
	}

	private void generateInsertCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), "insert", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
						new VariableDeclaratorId("o")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("dao"), "insert", new LinkedList<Expression>());
		mce.getArgs().add(new NameExpr("o"));
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), mce));
		ExpressionStmt stmt = new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), vars));
		ls.add(stmt);
		if (table.getForeignGenVars().size() > 0) {
			for (ForeignKey fk : table.getForeignGenVars()) {
				if (fk.getIdListVarName() != null) {
					String idGetVarName = "get"
							+ StringHelper.firstWordCap(StringHelper.toVarName(fk.getIdListVarName()));
					List<Statement> stmts = new LinkedList<Statement>();
					IfStmt ifStmt = new IfStmt(new BinaryExpr(new MethodCallExpr(new NameExpr("o"), idGetVarName),
							new NullLiteralExpr(), Operator.notEquals), new BlockStmt(stmts), null);
					ls.add(ifStmt);
					List<VariableDeclarator> ves = new LinkedList<VariableDeclarator>();
					ves.add(new VariableDeclarator(new VariableDeclaratorId("id")));
					List<Expression> args = new LinkedList<Expression>();
					args.add(new StringLiteralExpr("insert into " + fk.getTable().getName() + "("
							+ fk.getVarBindColumn() + "," + fk.getColumn()
							+ ",last_modified_time,creation_time) values(?,?,${now},${now})"));
					args.add(new MethodCallExpr(new NameExpr("o"), "getId"));
					args.add(new NameExpr("id"));
					ForeachStmt fstmt = new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(
							new ClassOrInterfaceType("Object")), ves), new MethodCallExpr(new NameExpr("o"),
							idGetVarName), new ExpressionStmt(new MethodCallExpr(new NameExpr("dao"), "execute", args)));
					stmts.add(fstmt);
				}
			}
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(insert)", ls);

		ls = new LinkedList<Statement>();
		if (table.getMemcachedConfig() != null) {
			List<Expression> args = new LinkedList<Expression>();
			args.add(new MethodCallExpr(new NameExpr("o"), "getId"));
			mce = new MethodCallExpr(new FieldAccessExpr(new NameExpr(table.getJavaClassName() + "Helper"),
					StringHelper.firstWordLower(table.getJavaClassName() + "Map")), "remove", args);
			ls.add(new ExpressionStmt(mce));
		}
		if (table.getLocalCache() != null && !table.getLocalCache().trim().isEmpty()) {
			mce = new MethodCallExpr(new FieldAccessExpr(new NameExpr(table.getJavaClassName() + "Helper"), "local"
					+ table.getJavaClassName() + "Map"), "setSourceLastModified", new MethodCallExpr(
					new NameExpr("dao"), "getLastModified", new ClassExpr(new ReferenceType(new ClassOrInterfaceType(
							table.getJavaClassName()))), new MethodCallExpr(new NameExpr("ret"), "getId")));
			ls.add(new ExpressionStmt(mce));
		}
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(insert)", ls);
	}

	private void generateFindByIdCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), "findById", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(pkColumn.getDataType().getJavaClassName())),
						new VariableDeclaratorId("id")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("dao"), "findById", new LinkedList<Expression>());
		mce.getArgs().add(new ClassExpr(new ClassOrInterfaceType(table.getJavaClassName())));
		mce.getArgs().add(new NameExpr("id"));
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), mce));
		ExpressionStmt stmt = new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), vars));
		ls.add(stmt);
		if (table.getForeignGenVars().size() > 0) {
			addImport("kitty.kaf.dao.resultset.DaoResultSet");
			List<Statement> ls1 = new LinkedList<Statement>();
			IfStmt ifStmt = new IfStmt(new BinaryExpr(new NameExpr("ret"), new NullLiteralExpr(), Operator.notEquals),
					new BlockStmt(ls1), null);
			ls.add(ifStmt);
			int i = 0;
			for (ForeignKey fk : table.getForeignGenVars()) {
				String varName = StringHelper.toVarName(fk.getIdListVarName());
				List<Expression> args = new LinkedList<Expression>();
				args.add(new IntegerLiteralExpr("0"));
				args.add(new StringLiteralExpr("select " + fk.getColumn() + " from " + fk.getTable().getName()
						+ " where " + table.getPkColumn().getName() + "=?"));
				args.add(new NameExpr("id"));
				if (i == 0) {
					vars = new LinkedList<VariableDeclarator>();
					VariableDeclarator ve = new VariableDeclarator(new VariableDeclaratorId("rset"),
							new MethodCallExpr(new NameExpr("dao"), "query", args));
					vars.add(ve);
					ls1.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
							"DaoResultSet")), vars)));
				} else
					ls1.add(new ExpressionStmt(new AssignExpr(new NameExpr("rset"), new MethodCallExpr(new NameExpr(
							"dao"), "query", args), japa.parser.ast.expr.AssignExpr.Operator.assign)));
				List<Expression> args1 = new LinkedList<Expression>();
				args1.add(new IntegerLiteralExpr("0"));
				args = new LinkedList<Expression>();
				Table ft = generator.database.getTables().get(fk.getTableRef());
				args.add(new MethodCallExpr(new NameExpr("rset"), "get" + ft.getPkColumn().getDataType().getShortName()
						+ "List", args1));
				ls1.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("ret"), ft.getPkColumn().getDataType()
						.getSetMethodName(varName), args)));
				i++;
			}
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(findById)", ls);
		ls = new LinkedList<Statement>();
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findById)", ls);
	}

	private void generateDeleteCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new VoidType(), "delete",
				new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Dao")), new VariableDeclaratorId("dao")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
		md.getParameters().add(new Parameter(type, new VariableDeclaratorId("idList")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		// 外键关联检查
		List<ForeignKey> fks = table.getDatabase().findForeignKeys(table);
		if (fks.size() > 0) { // 有外键关联
			addImport("kitty.kaf.helper.SQLHelper");
			List<Statement> ls = new LinkedList<Statement>();
			ls.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("dao"), "beginUpdateDatabase")));
			for (ForeignKey fk : fks) {
				if (fk.getDelOption().equalsIgnoreCase("true")) {
					List<Expression> args = new LinkedList<Expression>();
					List<Expression> args1 = new LinkedList<Expression>();
					args1.add(new StringLiteralExpr(fk.getTable().getName()));
					args1.add(new StringLiteralExpr(fk.getColumn()));
					args1.add(new NameExpr("idList"));
					args.add(new MethodCallExpr(new NameExpr("SQLHelper"), "buildDeleteOrListSql", args1));
					ls.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("dao"), "execute", args)));
				} else {
					List<Expression> args = new LinkedList<Expression>();
					List<Expression> args1 = new LinkedList<Expression>();
					args1.add(new StringLiteralExpr(fk.getTable().getName()));
					args1.add(new StringLiteralExpr(fk.getColumn()));
					args1.add(new NameExpr("idList"));
					args.add(new IntegerLiteralExpr("1"));
					args.add(new MethodCallExpr(new NameExpr("SQLHelper"), "buildSelectOrListSql", args1));
					args.add(new NameExpr("idList"));
					Expression condition = new MethodCallExpr(new MethodCallExpr(new NameExpr("dao"), "query", args),
							"next");
					args = new LinkedList<Expression>();
					args.add(new StringLiteralExpr("删除" + table.getDesp() + "失败：" + fk.getPrompt()));
					ls.add(new IfStmt(condition, new ThrowStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType(
							"SQLException"), args)), null));
				}
			}
			List<Statement> ls1 = new LinkedList<Statement>();
			ls1.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("dao"), "endUpdateDatabase")));
			TryStmt tryStmt = new TryStmt(new BlockStmt(ls), null, new BlockStmt(ls1));
			ls = new LinkedList<Statement>();
			ls.add(tryStmt);
			IfStmt ifStmt = new IfStmt(new BinaryExpr(new BinaryExpr(new NameExpr("idList"), new NullLiteralExpr(),
					Operator.notEquals), new BinaryExpr(new MethodCallExpr(new NameExpr("idList"), "size"),
					new IntegerLiteralExpr("0"), Operator.greater), Operator.and), new BlockStmt(ls), null);
			ls = new LinkedList<Statement>();
			ls.add(ifStmt);
			autoGenerateStatements(md.getBody(), "autogenerated:body(deleteCheck)", ls, "autogenerated:body(delete)",
					true);
		}
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("dao"), "delete", new LinkedList<Expression>());
		mce.getArgs().add(new FieldAccessExpr(new NameExpr(table.getJavaClassName()), "tableDef"));
		mce.getArgs().add(new NameExpr("idList"));
		stmts.add(new ExpressionStmt(mce));
		autoGenerateStatements(md.getBody(), "autogenerated:body(delete)", stmts);
		if (table.getMemcachedConfig() != null
				|| (table.getLocalCache() != null && !table.getLocalCache().trim().isEmpty())) {
			stmts = new LinkedList<Statement>();
			if (table.getMemcachedConfig() != null) {
				List<Expression> args = new LinkedList<Expression>();
				args.add(new NameExpr("idList"));
				mce = new MethodCallExpr(new FieldAccessExpr(new NameExpr(table.getJavaClassName() + "Helper"),
						StringHelper.firstWordLower(table.getJavaClassName()) + "Map"), "removeAll", args);
				stmts.add(new ExpressionStmt(mce));
			}
			if (table.getLocalCache() != null && !table.getLocalCache().trim().isEmpty()) {
				mce = new MethodCallExpr(new FieldAccessExpr(new NameExpr(table.getJavaClassName() + "Helper"), "local"
						+ table.getJavaClassName() + "Map"), "setSourceLastModified", new MethodCallExpr(new NameExpr(
						"dao"), "getLastModified", new ClassExpr(new ReferenceType(new ClassOrInterfaceType(
						table.getJavaClassName()))), new NameExpr("idList")));
				stmts.add(new ExpressionStmt(mce));
			}
			autoGenerateStatements(md.getBody(), "autogenerated:body(deleteAfter)", stmts);
		}
	}
}
