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
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
public class TradeExecutorClassGenerator extends ClassGenerator {
	TradeExecutorConfig config;
	CodeGenerator generator;

	public TradeExecutorClassGenerator(CodeGenerator generator, TradeExecutorConfig config) {
		super();
		this.generator = generator;
		this.config = config;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		String path = generator.workspaceDir + config.getProjectName() + "/src/"
				+ config.getPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + config.getClassName() + ".java";
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
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(config.getPackageName())));

		addImport("javax.servlet.http.HttpServletRequest");
		addImport("javax.servlet.http.HttpServletResponse");
		addImport("kitty.kaf.exceptions.CoreException");
		addImport("kitty.kaf.json.JSONObject");
		addImport("kitty.kaf.logging.Logger");
		addImport("kitty.kaf.trade.pack.HttpRequest");
		addImport("kitty.kaf.trade.web.JsonExecutor");
		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu, config.getClassName(), false,
				ModifierSet.PUBLIC);
		type.setExtends(new LinkedList<ClassOrInterfaceType>());
		type.getExtends().add(new ClassOrInterfaceType("JsonExecutor"));
		return type;
	}

	public void generateBody() throws IOException, ParseException {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "doExecute",
				new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getThrows().add(new NameExpr("Throwable"));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpRequest")), new VariableDeclaratorId(
						"request")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpServletResponse")),
						new VariableDeclaratorId("response")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"c")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"r")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		Iterator<String> it = config.getTables().keySet().iterator();
		IfStmt ifStmt = null;
		while (it.hasNext()) {
			String group = it.next();
			List<Expression> args = new LinkedList<Expression>();
			args.add(new NameExpr("session"));
			args.add(new NameExpr("cmd"));
			args.add(new NameExpr("request"));
			args.add(new NameExpr("response"));
			args.add(new NameExpr("c"));
			args.add(new NameExpr("r"));
			MethodCallExpr mce = new MethodCallExpr(new NameExpr(StringHelper.firstWordCap(group) + "Executor"),
					"doExecute", args);
			args = new LinkedList<Expression>();
			args.add(new StringLiteralExpr(group));
			addImport(config.getPackageName() + ".executors." + StringHelper.firstWordCap(group) + "Executor");
			if (ifStmt == null) {
				ifStmt = new IfStmt(new MethodCallExpr(new NameExpr("group"), "equalsIgnoreCase", args),
						new ExpressionStmt(mce), null);
				stmts.add(ifStmt);
			} else {
				IfStmt stmt = new IfStmt(new MethodCallExpr(new NameExpr("group"), "equalsIgnoreCase", args),
						new ExpressionStmt(mce), null);
				ifStmt.setElseStmt(stmt);
				ifStmt = stmt;
			}
			new GroupExecutorClassGenerator(generator, config, group).generate();
		}
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("未知的参数[group]"));
		ifStmt.setElseStmt(new ThrowStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType("CoreException"), args)));
		autoGenerateStatements(md.getBody(), "autogenerated:body(doExecute)", stmts);
	}
}

class GroupExecutorClassGenerator extends ClassGenerator {
	TradeExecutorConfig config;
	String group;
	CodeGenerator generator;

	public GroupExecutorClassGenerator(CodeGenerator generator, TradeExecutorConfig config, String group) {
		super();
		this.generator = generator;
		this.config = config;
		this.group = group;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		String path = generator.workspaceDir + config.getProjectName() + "/src/"
				+ config.getGroupPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + StringHelper.firstWordCap(group) + "Executor.java";
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
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(config.getGroupPackageName())));

		addImport("javax.servlet.http.HttpServletResponse");
		addImport("kitty.kaf.exceptions.CoreException");
		addImport("kitty.kaf.json.JSONObject");
		addImport("kitty.kaf.json.JSONArray");
		addImport("kitty.kaf.trade.pack.HttpRequest");
		addImport("kitty.kaf.session.RequestSession");
		addImport("kitty.kaf.io.KeyValue");
		addImport("kitty.kaf.helper.StringHelper");
		addImport("java.util.List");
		addImport("java.util.ArrayList");
		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu, StringHelper.firstWordCap(group)
				+ "Executor", false, ModifierSet.PUBLIC);
		return type;
	}

	@Override
	void generateBody() throws ParseException, IOException {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new VoidType(),
				"doExecute", new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		ClassOrInterfaceType type = new ClassOrInterfaceType("RequestSession");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(type, new VariableDeclaratorId("session")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpRequest")), new VariableDeclaratorId(
						"request")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpServletResponse")),
						new VariableDeclaratorId("response")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"c")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"r")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Table> tables = config.getTables().get(group);
		List<Statement> stmts1 = findStatementsAtKeywordComment(md.getBody().getStmts(),
				"autogenerated:body(doExecute)");
		IfStmt ifStmt = stmts1 != null ? (IfStmt) stmts1.get(0) : null;
		IfStmt rootIfStmt = ifStmt;
		String cmds[] = new String[] { "insert", "edit", "remove", "query" };
		List<Statement> stmts = new ArrayList<Statement>();
		if (stmts1 != null) {
			stmts.addAll(stmts1);
		}
		for (Table table : tables) {
			for (String cmd : cmds) {
				MethodCallExpr mce[] = createIfMethod(table, cmd);
				if (ifStmt == null) {
					ifStmt = new IfStmt(mce[1], new ExpressionStmt(mce[0]), null);
					stmts.add(ifStmt);
				} else {
					if (rootIfStmt != null) {
						ifStmt = JPHelper.findIfStmtByCondition(rootIfStmt, mce[1]);
						if (ifStmt != null)
							ifStmt.setThenStmt(new ExpressionStmt(mce[0]));
						else {
							ifStmt = JPHelper.getIfStmtLastIf(rootIfStmt);
							IfStmt stmt = new IfStmt(mce[1], new ExpressionStmt(mce[0]), null);
							ifStmt.setElseStmt(stmt);
							ifStmt = stmt;
						}
					} else {
						IfStmt stmt = new IfStmt(mce[1], new ExpressionStmt(mce[0]), null);
						ifStmt.setElseStmt(stmt);
						ifStmt = stmt;
					}
				}
			}
			addImport(table.getFullBeanClassName());
			addImport(table.getFullHelperClassName());
			ceateInsertMethod(table);
			ceateEditMethod(table);
			ceateRemoveMethod(table);
			ceateQueryMethod(table);
		}
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("未知的参数[cmd]"));
		if (rootIfStmt != null)
			ifStmt = JPHelper.getIfStmtLastIf(rootIfStmt);
		ifStmt.setElseStmt(new ThrowStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType("CoreException"), args)));
		autoGenerateStatements(md.getBody(), "autogenerated:body(doExecute)", stmts);
	}

	private void ceateInsertMethod(Table table) {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new VoidType(), "insert"
				+ table.getJavaClassName(), new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(),
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		ClassOrInterfaceType type = new ClassOrInterfaceType("RequestSession");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(type, new VariableDeclaratorId("session")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpRequest")), new VariableDeclaratorId(
						"request")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpServletResponse")),
						new VariableDeclaratorId("response")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"c")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"r")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		ObjectCreationExpr init = new ObjectCreationExpr(null, new ClassOrInterfaceType(table.getJavaClassName()));
		VariableDeclarator vd = new VariableDeclarator(new VariableDeclaratorId("o"), init);
		vars.add(vd);
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
				table.getJavaClassName())), vars);
		List<Statement> stmts = new LinkedList<Statement>();
		Long l = generator.rightDef.rightMap.get(table.getRightConfig().getInsert());
		if (l != null) {
			List<Expression> args = new LinkedList<Expression>();
			args.add(new LongLiteralExpr(l + "L"));
			List<Expression> args1 = new LinkedList<Expression>();
			args1.add(new StringLiteralExpr("对不起，您无此权限。"));
			IfStmt ifStmt = new IfStmt(new UnaryExpr(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"),
					"getUser"), "hasRight", args), japa.parser.ast.expr.UnaryExpr.Operator.not), new ThrowStmt(
					new ObjectCreationExpr(null, new ClassOrInterfaceType("Exception"), args1)), null);
			stmts.add(ifStmt);
		}

		stmts.add(new ExpressionStmt(vde));
		List<Expression> args = new LinkedList<Expression>();
		args = new LinkedList<Expression>();
		args.add(new NameExpr("request"));
		args.add(new BooleanLiteralExpr(true));
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("o"), "readFromRequest", args);
		stmts.add(new ExpressionStmt(mce));
		args = new LinkedList<Expression>();
		args.add(new NullLiteralExpr());
		args.add(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"), "getUser"), "getUserId"));
		args.add(new NameExpr("o"));
		AssignExpr ae = new AssignExpr(new NameExpr("o"), new MethodCallExpr(new NameExpr(table.getJavaClassName()
				+ "Helper"), "insert", args), AssignExpr.Operator.assign);
		stmts.add(new ExpressionStmt(ae));
		vars = new LinkedList<VariableDeclarator>();
		init = new ObjectCreationExpr(null, new ClassOrInterfaceType("JSONObject"));
		vd = new VariableDeclarator(new VariableDeclaratorId("json"), init);
		vars.add(vd);
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("JSONObject")), vars);
		stmts.add(new ExpressionStmt(vde));
		args = new LinkedList<Expression>();
		args.add(new NameExpr("json"));
		stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("o"), "toJson", args)));
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("data"));
		args.add(new NameExpr("json"));
		stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("c"), "put", args)));
		autoGenerateStatements(md.getBody(), "autogenerated:body(insert" + table.getJavaClassName() + ")", stmts);
	}

	private void ceateEditMethod(Table table) {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new VoidType(), "edit"
				+ table.getJavaClassName(), new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(),
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		ClassOrInterfaceType type = new ClassOrInterfaceType("RequestSession");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(type, new VariableDeclaratorId("session")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpRequest")), new VariableDeclaratorId(
						"request")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpServletResponse")),
						new VariableDeclaratorId("response")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"c")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"r")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		MethodCallExpr init = null;
		LinkedList<Expression> args = new LinkedList<Expression>(), args1 = new LinkedList<Expression>();
		args1.add(new StringLiteralExpr(table.getPkColumn().getName()));
		args.add(new MethodCallExpr(new NameExpr("request"), "getParameter"
				+ table.getPkColumn().getDataType().getShortName(), args1));
		if (table.getLocalCache() != null)
			init = new MethodCallExpr(new FieldAccessExpr(new NameExpr(table.getJavaClassName() + "Helper"), "local"
					+ table.getJavaClassName() + "Map"), "get", args);
		else if (table.getMemcachedConfig() != null)
			init = new MethodCallExpr(new FieldAccessExpr(new NameExpr(table.getJavaClassName() + "Helper"),
					StringHelper.firstWordLower(table.getJavaClassName() + "Map")), "get", args);
		else {
			args.add(0, new NullLiteralExpr());
			args.add(0, new NullLiteralExpr());
			init = new MethodCallExpr(new NameExpr(table.getJavaClassName() + "Helper"), "findById", args);
		}
		VariableDeclarator vd = new VariableDeclarator(new VariableDeclaratorId("o"), init);
		vars.add(vd);
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
				table.getJavaClassName())), vars);
		List<Statement> stmts = new LinkedList<Statement>();
		Long l = generator.rightDef.rightMap.get(table.getRightConfig().getEdit());
		if (l != null) {
			args = new LinkedList<Expression>();
			args.add(new LongLiteralExpr(l + "L"));
			args1 = new LinkedList<Expression>();
			args1.add(new StringLiteralExpr("对不起，您无此权限。"));
			IfStmt ifStmt = new IfStmt(new UnaryExpr(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"),
					"getUser"), "hasRight", args), japa.parser.ast.expr.UnaryExpr.Operator.not), new ThrowStmt(
					new ObjectCreationExpr(null, new ClassOrInterfaceType("Exception"), args1)), null);
			stmts.add(ifStmt);
		}
		stmts.add(new ExpressionStmt(vde));
		args = new LinkedList<Expression>();
		args = new LinkedList<Expression>();
		args.add(new NameExpr("request"));
		args.add(new BooleanLiteralExpr(false));
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("o"), "readFromRequest", args);
		stmts.add(new ExpressionStmt(mce));
		args = new LinkedList<Expression>();
		args.add(new NullLiteralExpr());
		args.add(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"), "getUser"), "getUserId"));
		args.add(new NameExpr("o"));
		AssignExpr ae = new AssignExpr(new NameExpr("o"), new MethodCallExpr(new NameExpr(table.getJavaClassName()
				+ "Helper"), "edit", args), AssignExpr.Operator.assign);
		stmts.add(new ExpressionStmt(ae));
		vars = new LinkedList<VariableDeclarator>();
		ObjectCreationExpr oinit = new ObjectCreationExpr(null, new ClassOrInterfaceType("JSONObject"));
		vd = new VariableDeclarator(new VariableDeclaratorId("json"), oinit);
		vars.add(vd);
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("JSONObject")), vars);
		stmts.add(new ExpressionStmt(vde));
		args = new LinkedList<Expression>();
		args.add(new NameExpr("json"));
		stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("o"), "toJson", args)));
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("data"));
		args.add(new NameExpr("json"));
		stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("c"), "put", args)));
		autoGenerateStatements(md.getBody(), "autogenerated:body(edit" + table.getJavaClassName() + ")", stmts);
	}

	private void ceateRemoveMethod(Table table) {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new VoidType(), "remove"
				+ table.getJavaClassName(), new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(),
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		ClassOrInterfaceType type = new ClassOrInterfaceType("RequestSession");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(type, new VariableDeclaratorId("session")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpRequest")), new VariableDeclaratorId(
						"request")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpServletResponse")),
						new VariableDeclaratorId("response")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"c")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"r")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		Long l = generator.rightDef.rightMap.get(table.getRightConfig().getDelete());
		if (l != null) {
			List<Expression> args = new LinkedList<Expression>();
			args.add(new LongLiteralExpr(l + "L"));
			List<Expression> args1 = new LinkedList<Expression>();
			args1.add(new StringLiteralExpr("对不起，您无此权限。"));
			IfStmt ifStmt = new IfStmt(new UnaryExpr(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"),
					"getUser"), "hasRight", args), japa.parser.ast.expr.UnaryExpr.Operator.not), new ThrowStmt(
					new ObjectCreationExpr(null, new ClassOrInterfaceType("Exception"), args1)), null);
			stmts.add(ifStmt);
		}
		List<Expression> args = new LinkedList<Expression>();
		args = new LinkedList<Expression>();
		List<Expression> args1 = new LinkedList<Expression>(), args2 = new LinkedList<Expression>();
		args.add(new NullLiteralExpr());
		args.add(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"), "getUser"), "getUserId"));
		args.add(new MethodCallExpr(new NameExpr("StringHelper"), "splitTo"
				+ table.getPkColumn().getDataType().getShortName() + "List", args1));
		args1.add(new MethodCallExpr(new NameExpr("request"), "getParameter", args2));
		args1.add(new StringLiteralExpr(","));
		args2.add(new StringLiteralExpr("id_list"));
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(table.getJavaClassName() + "Helper"), "delete", args);
		stmts.add(new ExpressionStmt(mce));
		autoGenerateStatements(md.getBody(), "autogenerated:body(remove" + table.getJavaClassName() + ")", stmts);
	}

	private void ceateQueryMethod(Table table) {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new VoidType(), "query"
				+ table.getJavaClassName(), new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(),
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		ClassOrInterfaceType type = new ClassOrInterfaceType("RequestSession");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(type, new VariableDeclaratorId("session")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpRequest")), new VariableDeclaratorId(
						"request")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpServletResponse")),
						new VariableDeclaratorId("response")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"c")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"r")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		Long l = generator.rightDef.rightMap.get(table.getRightConfig().getQuery());
		if (l != null) {
			List<Expression> args = new LinkedList<Expression>();
			args.add(new LongLiteralExpr(l + "L"));
			List<Expression> args1 = new LinkedList<Expression>();
			args1.add(new StringLiteralExpr("对不起，您无此权限。"));
			IfStmt ifStmt = new IfStmt(new UnaryExpr(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"),
					"getUser"), "hasRight", args), japa.parser.ast.expr.UnaryExpr.Operator.not), new ThrowStmt(
					new ObjectCreationExpr(null, new ClassOrInterfaceType("Exception"), args1)), null);
			stmts.add(ifStmt);
		}
		VariableDeclarator vd = new VariableDeclarator(new VariableDeclaratorId("queryCmd"), new StringLiteralExpr(
				table.getTradeConfig().getQueryDefaultCmd()));
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		vars.add(vd);
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(new ClassOrInterfaceType("String")), vars);
		stmts.add(new ExpressionStmt(vde));

		List<Expression> args = new ArrayList<Expression>();
		args.add(new StringLiteralExpr("firstindex"));
		args.add(new LongLiteralExpr("-1L"));
		vars = new LinkedList<VariableDeclarator>();
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("request"), "getParameterLongDef", args);
		vd = new VariableDeclarator(new VariableDeclaratorId("firstIndex"), mce);
		vars.add(vd);
		vde = new VariableDeclarationExpr(new PrimitiveType(Primitive.Long), vars);
		stmts.add(new ExpressionStmt(vde));

		args = new ArrayList<Expression>();
		args.add(new StringLiteralExpr("maxresults"));
		args.add(new IntegerLiteralExpr("20"));
		vars = new LinkedList<VariableDeclarator>();
		mce = new MethodCallExpr(new NameExpr("request"), "getParameterIntDef", args);
		vd = new VariableDeclarator(new VariableDeclaratorId("maxResults"), mce);
		vars.add(vd);
		vde = new VariableDeclarationExpr(new PrimitiveType(Primitive.Int), vars);
		stmts.add(new ExpressionStmt(vde));

		type = new ClassOrInterfaceType("ArrayList");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("Object")));
		ObjectCreationExpr oce = new ObjectCreationExpr(null, type);
		vd = new VariableDeclarator(new VariableDeclaratorId("params"), oce);
		vars = new LinkedList<VariableDeclarator>();
		vars.add(vd);
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("Object")));
		vde = new VariableDeclarationExpr(new ReferenceType(type), vars);
		stmts.add(new ExpressionStmt(vde));
		type = new ClassOrInterfaceType("String");
		vars = new LinkedList<VariableDeclarator>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("keyword"));
		args.add(new NullLiteralExpr());
		vars.add(new VariableDeclarator(new VariableDeclaratorId("keyword"), new MethodCallExpr(
				new NameExpr("request"), "getParameterDef", args)));
		vde = new VariableDeclarationExpr(type, vars);
		stmts.add(new ExpressionStmt(vde));
		args = new LinkedList<Expression>();
		args.add(new NameExpr("keyword"));
		stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("params"), "add", args)));
		autoGenerateStatements(md.getBody(), "autogenerated:def(query" + table.getJavaClassName() + ")", stmts);

		stmts = new LinkedList<Statement>();
		ClassOrInterfaceType type1 = new ClassOrInterfaceType("List");
		args = new LinkedList<Expression>();
		args.add(new NullLiteralExpr());
		args.add(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"), "getUser"), "getUserId"));
		args.add(new NameExpr("queryCmd"));
		args.add(new NameExpr("firstIndex"));
		args.add(new NameExpr("maxResults"));
		args.add(new NameExpr("params"));
		mce = new MethodCallExpr(new NameExpr(table.getJavaClassName() + "Helper"), "queryPage", args);
		vars = new LinkedList<VariableDeclarator>();
		vd = new VariableDeclarator(new VariableDeclaratorId("ret"), mce);
		vars.add(vd);
		type1.setTypeArgs(new LinkedList<Type>());
		type1.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		type = new ClassOrInterfaceType("KeyValue");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("Integer")));
		type.getTypeArgs().add(new ReferenceType(type1));
		vde = new VariableDeclarationExpr(new ReferenceType(type), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:exec(query" + table.getJavaClassName() + ")", stmts);

		stmts = new LinkedList<Statement>();
		BlockStmt thenStmt = new BlockStmt(new LinkedList<Statement>());
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("json"), new ObjectCreationExpr(null,
				new ClassOrInterfaceType("JSONArray"))));
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("JSONArray")), vars);
		thenStmt.getStmts().add(new ExpressionStmt(vde));
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("count"));
		args.add(new MethodCallExpr(new NameExpr("ret"), "getKey"));
		mce = new MethodCallExpr(new NameExpr("c"), "put", args);
		thenStmt.getStmts().add(new ExpressionStmt(mce));
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("items"));
		args.add(new NameExpr("json"));
		mce = new MethodCallExpr(new NameExpr("c"), "put", args);
		thenStmt.getStmts().add(new ExpressionStmt(mce));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("obj")));
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())), vars);
		mce = new MethodCallExpr(new NameExpr("ret"), "getValue");
		List<Statement> fsBody = new LinkedList<Statement>();
		ForeachStmt fs = new ForeachStmt(vde, mce, new BlockStmt(fsBody));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("j"), new ObjectCreationExpr(null,
				new ClassOrInterfaceType("JSONObject"))));
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("JSONObject")), vars);
		fsBody.add(new ExpressionStmt(vde));
		args = new LinkedList<Expression>();
		args.add(new NameExpr("j"));
		fsBody.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("obj"), "toJson", args)));
		args = new LinkedList<Expression>();
		args.add(new NameExpr("j"));
		fsBody.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("json"), "put", args)));
		thenStmt.getStmts().add(fs);
		IfStmt ifStmt = new IfStmt(new BinaryExpr(new NameExpr("ret"), new NullLiteralExpr(), Operator.notEquals),
				thenStmt, null);
		stmts.add(ifStmt);
		autoGenerateStatements(md.getBody(), "autogenerated:assign(query" + table.getJavaClassName() + ")", stmts);
	}

	MethodCallExpr[] createIfMethod(Table table, String prefix) {
		List<Expression> args = new LinkedList<Expression>();
		args.add(new NameExpr("session"));
		args.add(new NameExpr("cmd"));
		args.add(new NameExpr("request"));
		args.add(new NameExpr("response"));
		args.add(new NameExpr("c"));
		args.add(new NameExpr("r"));
		List<Expression> args1 = new LinkedList<Expression>();
		args1.add(new StringLiteralExpr(prefix + table.getJavaClassName()));
		return new MethodCallExpr[] { new MethodCallExpr(null, prefix + table.getJavaClassName(), args),
				new MethodCallExpr(new NameExpr("cmd"), "equalsIgnoreCase", args1) };
	}
}