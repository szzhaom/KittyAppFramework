package kitty.kaf.dao.tools.cg;

import japa.parser.ASTHelper;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
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
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.EnclosedExpr;
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
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
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
public class BeanHelperClassGenerator extends ClassGenerator {
	Table table;
	String pkClass;
	List<Column> pkColumns = null;
	CodeGenerator generator;
	Column pkColumn;
	String beanRemote;

	public BeanHelperClassGenerator(CodeGenerator generator, Table table) {
		super();
		this.generator = generator;
		this.table = table;
		this.beanRemote = table.getJavaClassName() + "BeanRemote";
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		String path = generator.workspaceDir + def.getInfProjectName() + "/src/"
				+ def.getInfPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + table.getJavaClassName() + "Helper.java";
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
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(def.getInfPackageName())));

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
		addImport("javax.servlet.http.HttpServletRequest");
		addImport("javax.servlet.http.HttpServletResponse");
		addImport("kitty.kaf.pools.jndi.JndiConnectionFactory");
		addImport("kitty.kaf.session.AbstractRequestSession");
		addImport("kitty.kaf.session.RequestSession");
		addImport("java.util.List");
		addImport("java.util.ArrayList");
		addImport("java.util.Date");
		addImport("kitty.kaf.io.KeyValue");
		addImport("kitty.kaf.pools.jndi.JndiConnectionFactory");
		addImport("kitty.kaf.pools.jndi.Lookuper");
		addImport(table.getFullBeanClassName());
		addImport("kitty.kaf.cache.CacheValueList");
		if (table.getCacheConfig() != null) {
			addImport("kitty.kaf.cache.CacheClient");
			addImport("kitty.kaf.cache.CacheCallback");
			if (table.getUniqueKeyColumn() != null) {
				addImport("kitty.kaf.cache.UniqueKeyCachedMap");
			} else
				addImport("kitty.kaf.cache.CachedMap");
		}
		if (table.getLocalCache() != null) {
			if (table.isTreeCache()) {
				addImport("kitty.kaf.cache.LocalCachedTreeMapChangedEventListener");
				addImport("kitty.kaf.cache.LocalCachedTreeMap");
			} else {
				addImport("kitty.kaf.listeners.ItemChangedEventListener");
				addImport("kitty.kaf.cache.LocalCachedMap");
			}
			addImport("kitty.kaf.cache.CacheClient");
			addImport("kitty.kaf.cache.LocalCacheCallback");
		}
		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu, table.getJavaClassName() + "Helper", false,
				ModifierSet.PUBLIC);
		return type;
	}

	public void generateBody() throws IOException, ParseException {
		generateStaticCode();
		generateDeleteCode();
		generateFindByIdCode();
		generateFindByUniqueKeyCode();
		generateFindByIdListCode();
		generateFindByUniqueKeyListCode();
		generateInsertCode();
		generateEditCode();
		generateQueryCode();
		generateQueryLatestCode();
		generateQueryPageCode();
		generateExecuteCode();
		generateCacheValueCompeteCode();
		generateInsertOrEditPageProcessCode();
	}

	private void generateInsertOrEditPageProcessCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new VoidType(),
				"insertOrEditPageProcess", new LinkedList<Parameter>(), null, null);
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpServletRequest")),
						new VariableDeclaratorId("request")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpServletResponse")),
						new VariableDeclaratorId("response")));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("id"));
		vars.add(new VariableDeclarator(new VariableDeclaratorId("id"), new MethodCallExpr(new NameExpr("request"),
				"getParameter", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(new ClassOrInterfaceType("String")), vars);
		List<Statement> stmts = new LinkedList<Statement>();
		stmts.add(new ExpressionStmt(vde));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("error"), new NullLiteralExpr()));
		stmts.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("String")),
				vars)));
		BlockStmt block = new BlockStmt(new LinkedList<Statement>());
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("data"));
		args.add(new ObjectCreationExpr(null, new ClassOrInterfaceType(table.getJavaClassName())));
		IfStmt ifStmt = new IfStmt(new BinaryExpr(new NameExpr("id"), new NullLiteralExpr(), Operator.notEquals),
				block, new ExpressionStmt(new MethodCallExpr(new NameExpr("request"), "setAttribute", args)));
		stmts.add(ifStmt);
		BlockStmt tryBlock = new BlockStmt(new LinkedList<Statement>());
		args = new LinkedList<Expression>();
		ClassOrInterfaceType type = new ClassOrInterfaceType("RequestSession");
		type.setTypeArgs(new LinkedList<Type>());
		vars = new LinkedList<VariableDeclarator>();
		args.add(new NameExpr("request"));
		vars.add(new VariableDeclarator(new VariableDeclaratorId("session"), new MethodCallExpr(new NameExpr(
				"AbstractRequestSession"), "getCurrentSession", args)));
		type.getTypeArgs().add(new WildcardType());
		vde = new VariableDeclarationExpr(new ReferenceType(type), vars);
		tryBlock.getStmts().add(new ExpressionStmt(vde));
		args = new LinkedList<Expression>();
		args.add(new NullLiteralExpr());
		args.add(new MethodCallExpr(new MethodCallExpr(new NameExpr("session"), "getUser"), "getUserId"));
		LinkedList<Expression> args1 = new LinkedList<Expression>();
		args1.add(new NameExpr("id"));
		args.add(new MethodCallExpr(new NameExpr(table.getPkColumn().getDataType().getJavaClassName()), "valueOf",
				args1));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("p"), new MethodCallExpr(new NameExpr(table
				.getJavaClassName() + "Helper"), "findById", args)));
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())), vars);
		tryBlock.getStmts().add(new ExpressionStmt(vde));
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("data"));
		args.add(new NameExpr("p"));
		ifStmt = new IfStmt(new BinaryExpr(new NameExpr("p"), new NullLiteralExpr(), Operator.equals),
				new ExpressionStmt(new AssignExpr(new NameExpr("error"), new BinaryExpr(new BinaryExpr(
						new StringLiteralExpr("找不到" + table.getDesp() + "信息[id="), new NameExpr("id"), Operator.plus),
						new StringLiteralExpr("]"), Operator.plus), japa.parser.ast.expr.AssignExpr.Operator.assign)),
				new ExpressionStmt(new MethodCallExpr(new NameExpr("request"), "setAttribute", args)));
		tryBlock.getStmts().add(ifStmt);
		BlockStmt catchBlock = new BlockStmt(new LinkedList<Statement>());
		CatchClause cc = new CatchClause(new Parameter(new ReferenceType(new ClassOrInterfaceType("Throwable")),
				new VariableDeclaratorId("e")), catchBlock);
		catchBlock.getStmts().add(
				new ExpressionStmt(new AssignExpr(new NameExpr("error"), new MethodCallExpr(new NameExpr("e"),
						"getMessage"), japa.parser.ast.expr.AssignExpr.Operator.assign)));
		List<CatchClause> ccList = new LinkedList<CatchClause>();
		ccList.add(cc);
		block.getStmts().add(new TryStmt(tryBlock, ccList, null));
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("error"));
		args.add(new NameExpr("error"));
		ifStmt = new IfStmt(new BinaryExpr(new NameExpr("error"), new NullLiteralExpr(), Operator.notEquals),
				new ExpressionStmt(new MethodCallExpr(new NameExpr("request"), "setAttribute", args)), null);
		stmts.add(ifStmt);
		autoGenerateStatements(md.getBody(), "autogenerated:body(insertOrEditPageProcess)", stmts);
	}

	private void generateQueryLatestCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		ClassOrInterfaceType type = new ClassOrInterfaceType("CacheValueList");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		type.getTypeArgs().add(new WildcardType());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new ReferenceType(type),
				"queryLatest", new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Exception"));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
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

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(queryLatest)", stmts);
		stmts.clear();
		type = new ClassOrInterfaceType("CacheValueList");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		type.getTypeArgs().add(new WildcardType());
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "queryLatest");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("cmd"));
		args.add(new NameExpr("firstIndex"));
		args.add(new NameExpr("maxResults"));
		args.add(new NameExpr("lastModified"));
		mc.setArgs(args);
		vde = new VariableDeclarationExpr(new ReferenceType(type), new VariableDeclarator(new VariableDeclaratorId(
				"ret"), mc));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(queryLatest)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(queryLatest)", stmts);
	}

	private void generateCacheValueCompeteCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new VoidType(),
				"getCacheValueCompete", new LinkedList<Parameter>(), null, null);
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
						new VariableDeclaratorId("v")));
		JPHelper.addOrUpdateMethod(mainClass, md, true);
	}

	private String[] generateCache(List<BodyDeclaration> members, String name1, String name2) {
		if (name1 == null)
			name1 = "";
		if (name2 == null)
			name2 = "";
		name1 = name1.trim();
		name2 = name2.trim();
		String[] ret = new String[] { null, null };
		if (name1.trim().length() > 0) {
			List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
			List<Expression> args = new LinkedList<Expression>();
			args.add(new StringLiteralExpr(table.getCacheConfig()));
			vars.add(new VariableDeclarator(new VariableDeclaratorId("cacheClient"), new ObjectCreationExpr(null,
					new ClassOrInterfaceType("CacheClient"), args)));
			FieldDeclaration fd = new FieldDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC | ModifierSet.FINAL,
					new ReferenceType(new ClassOrInterfaceType("CacheClient")), vars);
			members.add(fd);
			ret[0] = "cacheClient";
		}
		if (name2.trim().length() > 0) {
			if (!name2.equals(name1)) {
				List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
				List<Expression> args = new LinkedList<Expression>();
				args.add(new StringLiteralExpr(name2));
				if (name1.length() == 0)
					ret[1] = "cacheClient";
				else
					ret[1] = "cacheClient1";
				vars.add(new VariableDeclarator(new VariableDeclaratorId(ret[1]), new ObjectCreationExpr(null,
						new ClassOrInterfaceType("CacheClient"), args)));
				FieldDeclaration fd = new FieldDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC | ModifierSet.FINAL,
						new ReferenceType(new ClassOrInterfaceType("CacheClient")), vars);
				members.add(fd);
			} else
				ret[1] = "cacheClient";
		}
		return ret;
	}

	private void generateStaticCode() {
		List<BodyDeclaration> members = new LinkedList<BodyDeclaration>();
		String localCache = table.getLocalCache();
		String localCacheMc = null, localCacheInterval = "0";
		if (localCache != null) {
			String[] s = StringHelper.splitToStringArray(localCache, ",");
			localCacheMc = s[0];
			localCacheInterval = s[1];
		}
		String mc[] = generateCache(members, table.getCacheConfig(), localCacheMc);
		if (mc[0] != null) {
			generateCachedMap(mc[0], members);
		}
		if (localCacheMc != null)
			generateLocalCacheMap(mc[1], members, localCacheInterval);
		autoGenerateMembers(mainClass, "autogenerated:static(cached)", members);
		if (localCacheMc != null) {
			MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC, new VoidType(), "localCacheChanged", null,
					null, new LinkedList<NameExpr>());
			md.getThrows().add(new NameExpr("Throwable"));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);

			md = new MethodDeclaration(ModifierSet.STATIC, new VoidType(), "localCacheRemoved",
					new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
			md.getThrows().add(new NameExpr("Throwable"));
			md.getParameters().add(
					new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
							"item")));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);

			md = new MethodDeclaration(ModifierSet.STATIC, new VoidType(), "localCacheEdited",
					new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
			md.getThrows().add(new NameExpr("Throwable"));
			md.getParameters().add(
					new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
							"item")));
			md.getParameters().add(
					new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
							"newValue")));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);

			md = new MethodDeclaration(ModifierSet.STATIC, new VoidType(), "localCacheAdded",
					new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
			md.getThrows().add(new NameExpr("Throwable"));
			md.getParameters().add(
					new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
							"item")));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			if (table.isTreeCache()) {
				md = new MethodDeclaration(ModifierSet.STATIC, new VoidType(), "localCacheUpdateTreeDataToDatabase",
						new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
				md.getThrows().add(new NameExpr("Throwable"));
				md.getParameters().add(
						new Parameter(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
								new ClassOrInterfaceType("Object"), 1))), new VariableDeclaratorId("needUpdatedList")));
				md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			}
		}
	}

	private void generateLocalCacheMap(String mc, List<BodyDeclaration> members, String localCacheInterval) {
		String listenerClassName = table.isTreeCache() ? "LocalCachedTreeMapChangedEventListener"
				: "ItemChangedEventListener";
		ObjectCreationExpr init = new ObjectCreationExpr(null, new ClassOrInterfaceType(listenerClassName));
		init.setAnonymousClassBody(new LinkedList<BodyDeclaration>());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "change",
				new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"sender")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		md.getBody().getStmts().add(new ExpressionStmt(new MethodCallExpr(null, "localCacheChanged")));
		init.getAnonymousClassBody().add(md);

		md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "remove", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"sender")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId("item")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		List<Expression> args = new LinkedList<Expression>();
		args.add(new NameExpr("item"));
		md.getBody().getStmts().add(new ExpressionStmt(new MethodCallExpr(null, "localCacheRemoved", args)));
		init.getAnonymousClassBody().add(md);

		md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "edit", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"sender")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId("item")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"newValue")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		args = new LinkedList<Expression>();
		args.add(new NameExpr("item"));
		args.add(new NameExpr("newValue"));
		md.getBody().getStmts().add(new ExpressionStmt(new MethodCallExpr(null, "localCacheEdited", args)));
		init.getAnonymousClassBody().add(md);

		md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "add", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"sender")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId("item")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		args = new LinkedList<Expression>();
		args.add(new NameExpr("item"));
		md.getBody().getStmts().add(new ExpressionStmt(new MethodCallExpr(null, "localCacheAdded", args)));
		init.getAnonymousClassBody().add(md);

		if (table.isTreeCache()) {
			md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "updateTreeDataToDatabase",
					new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
			md.getThrows().add(new NameExpr("Throwable"));
			md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
			md.getParameters().add(
					new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
							"sender")));
			md.getParameters().add(
					new Parameter(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
							new ClassOrInterfaceType("Object"), 1))), new VariableDeclaratorId("needUpdatedList")));
			md.setBody(new BlockStmt(new LinkedList<Statement>()));
			md.getBody()
					.getStmts()
					.add(new ExpressionStmt(new MethodCallExpr(null, "localCacheUpdateTreeDataToDatabase",
							new NameExpr("needUpdatedList"))));
			init.getAnonymousClassBody().add(md);
		}

		LinkedList<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		VariableDeclarator vd = new VariableDeclarator(new VariableDeclaratorId("itemsChangedEventListener"), init);
		vars.add(vd);
		FieldDeclaration fd = new FieldDeclaration(ModifierSet.STATIC, new ReferenceType(new ClassOrInterfaceType(
				listenerClassName)), vars);
		members.add(fd);

		init = new ObjectCreationExpr(null, new ClassOrInterfaceType("LocalCacheCallback"));
		init.setAnonymousClassBody(new LinkedList<BodyDeclaration>());
		ClassOrInterfaceType type = new ClassOrInterfaceType("CacheValueList");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		type.getTypeArgs().add(new WildcardType());
		md = new MethodDeclaration(ModifierSet.PUBLIC, type, "onGetCacheValueList", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"source")));
		md.getParameters()
				.add(new Parameter(new PrimitiveType(Primitive.Long), new VariableDeclaratorId("firstIndex")));
		md.getParameters().add(new Parameter(new PrimitiveType(Primitive.Int), new VariableDeclaratorId("maxResults")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Date")), new VariableDeclaratorId(
						"lastModified")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		args = new LinkedList<Expression>();
		args.add(new NullLiteralExpr());
		args.add(new NullLiteralExpr());
		args.add(new StringLiteralExpr("default"));
		args.add(new NameExpr("firstIndex"));
		args.add(new NameExpr("maxResults"));
		args.add(new NameExpr("lastModified"));
		md.getBody()
				.getStmts()
				.add(new ReturnStmt(new MethodCallExpr(new NameExpr(table.getJavaClassName() + "Helper"),
						"queryLatest", args)));
		init.getAnonymousClassBody().add(md);
		md = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(Primitive.Boolean), "isNullId",
				new LinkedList<Parameter>());
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId("v")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		args = new LinkedList<Expression>();
		if (table.getNullId() == null)
			args.add(new NullLiteralExpr());
		else
			args.add(new LongLiteralExpr(table.getNullId()));
		MethodCallExpr mce = new MethodCallExpr(new EnclosedExpr(new CastExpr(new ReferenceType(
				new ClassOrInterfaceType(pkClass)), new NameExpr("v"))), "compareTo", args);
		BinaryExpr bexpr = new BinaryExpr(mce, new IntegerLiteralExpr("0"), Operator.lessEquals);
		md.getBody().getStmts().add(new ReturnStmt(bexpr));
		init.getAnonymousClassBody().add(md);
		vars = new LinkedList<VariableDeclarator>();
		vd = new VariableDeclarator(new VariableDeclaratorId("localCacheCallBack"), init);
		vars.add(vd);
		fd = new FieldDeclaration(ModifierSet.STATIC,
				new ReferenceType(new ClassOrInterfaceType("LocalCacheCallback")), vars);
		members.add(fd);
		type = new ClassOrInterfaceType(table.isTreeCache() ? "LocalCachedTreeMap" : "LocalCachedMap");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		vars = new LinkedList<VariableDeclarator>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr(table.getJavaClassName().toLowerCase()));
		args.add(new NameExpr(mc));
		args.add(new NameExpr("localCacheCallBack"));
		args.add(new NameExpr("itemsChangedEventListener"));
		args.add(new IntegerLiteralExpr(localCacheInterval));
		if (table.isTreeCache()) {
			args.add(new NameExpr(table.getTreeCacheClass() + ".class"));
			args.add(new BooleanLiteralExpr(true));
		} else if (table.getUniqueKeyColumn() != null)
			args.add(new BooleanLiteralExpr(true));
		init = new ObjectCreationExpr(null, type, args);
		vd = new VariableDeclarator(new VariableDeclaratorId("local" + table.getJavaClassName() + "Map"), init);
		vars.add(vd);

		type = new ClassOrInterfaceType(table.isTreeCache() ? "LocalCachedTreeMap" : "LocalCachedMap");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		fd = new FieldDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, vars);
		members.add(fd);
		generator.database.getLocalCacheTables().add(table);
	}

	private void generateCachedMap(String mc, List<BodyDeclaration> members) {
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		Column uk = table.getUniqueKeyColumn();

		ClassOrInterfaceType type = new ClassOrInterfaceType(uk == null ? "CachedMap" : "UniqueKeyCachedMap");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
		if (uk != null)
			type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("String")));
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		args = new LinkedList<Expression>();
		ObjectCreationExpr expr = new ObjectCreationExpr(null, new ClassOrInterfaceType("CacheCallback",
				new ReferenceType(new ClassOrInterfaceType(pkColumn.getDataType().getJavaClassName())),
				new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName()))));
		expr.setAnonymousClassBody(new LinkedList<BodyDeclaration>());
		// onGetCacheValueById
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType(
				table.getJavaClassName())), "onGetCacheValueById", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"source")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(pkColumn.getDataType().getJavaClassName())),
						new VariableDeclaratorId("id")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(null, "findById",
				new NullLiteralExpr(), new NullLiteralExpr(), new NameExpr("id"))));
		md.getBody()
				.getStmts()
				.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())), vars)));
		md.getBody().getStmts()
				.add(new ExpressionStmt(new MethodCallExpr(null, "getCacheValueCompete", new NameExpr("ret"))));
		md.getBody().getStmts().add(new ReturnStmt(new NameExpr("ret")));
		expr.getAnonymousClassBody().add(md);

		// onGetCacheValueByIdList
		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("List",
				new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())))), "onGetCacheValueByIdList",
				new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"source")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
						new ClassOrInterfaceType(pkColumn.getDataType().getJavaClassName())))),
						new VariableDeclaratorId("id")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(null, "findByIdList",
				new NullLiteralExpr(), new NullLiteralExpr(), new NameExpr("id"))));
		md.getBody()
				.getStmts()
				.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("List",
						new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())))), vars)));
		ForeachStmt fs = new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
				table.getJavaClassName())), new VariableDeclarator(new VariableDeclaratorId("o"))),
				new NameExpr("ret"), new ExpressionStmt(new MethodCallExpr(null, "getCacheValueCompete", new NameExpr(
						"o"))));
		md.getBody().getStmts().add(fs);
		md.getBody().getStmts().add(new ReturnStmt(new NameExpr("ret")));
		expr.getAnonymousClassBody().add(md);

		// onGetCacheValueByName
		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType(
				table.getJavaClassName())), "onGetCacheValueByName", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"source")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("id")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		if (uk != null) {
			vars = new LinkedList<VariableDeclarator>();
			vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(null,
					"findByUniqueKey", new NullLiteralExpr(), new NullLiteralExpr(), new NameExpr("id"))));
			md.getBody()
					.getStmts()
					.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
							table.getJavaClassName())), vars)));
			md.getBody().getStmts()
					.add(new ExpressionStmt(new MethodCallExpr(null, "getCacheValueCompete", new NameExpr("ret"))));
			md.getBody().getStmts().add(new ReturnStmt(new NameExpr("ret")));
		} else
			md.getBody().getStmts().add(new ReturnStmt(new NullLiteralExpr()));
		expr.getAnonymousClassBody().add(md);
		// onGetCacheValueByNameList
		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("List",
				new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())))), "onGetCacheValueByNameList",
				new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"source")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
						new ClassOrInterfaceType("String")))), new VariableDeclaratorId("id")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		if (uk != null) {
			vars = new LinkedList<VariableDeclarator>();
			vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(null,
					"findByUniqueKeyList", new NullLiteralExpr(), new NullLiteralExpr(), new NameExpr("id"))));
			md.getBody()
					.getStmts()
					.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
							"List", new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())))), vars)));
			fs = new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
					table.getJavaClassName())), new VariableDeclarator(new VariableDeclaratorId("o"))), new NameExpr(
					"ret"), new ExpressionStmt(new MethodCallExpr(null, "getCacheValueCompete", new NameExpr("o"))));
			md.getBody().getStmts().add(fs);
			md.getBody().getStmts().add(new ReturnStmt(new NameExpr("ret")));
		} else
			md.getBody().getStmts().add(new ReturnStmt(new NullLiteralExpr()));
		expr.getAnonymousClassBody().add(md);

		// map def
		type = new ClassOrInterfaceType(uk == null ? "CachedMap" : "UniqueKeyCachedMap");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
		if (uk != null)
			type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("String")));
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		args = new LinkedList<Expression>();
		args.add(expr);
		args.add(new NameExpr(mc));
		args.add(new FieldAccessExpr(new NameExpr(table.getJavaClassName()), "CACHE_KEY_PREFIX"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName()))));
		ObjectCreationExpr init = new ObjectCreationExpr(null, type, args);
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId(StringHelper.firstWordLower(table.getJavaClassName())
				+ "Map"), init));
		FieldDeclaration fd = new FieldDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC | ModifierSet.FINAL, type,
				vars);
		members.add(fd);
	}

	private void generateExecuteCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType("Object")), "execute", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(new ReferenceType(type), new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("Exception"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(execute)", stmts);
		stmts.clear();
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("Object")),
				new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(new NameExpr("bean"),
						"execute", new NameExpr("loginUserId"), new NameExpr("cmd"), new NameExpr("params"))));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(execute)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(execute)", stmts);

		md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType(
				"Object")), "execute", new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")),
						new VariableDeclaratorId("params"), true));
		md.getThrows().add(new NameExpr("Exception"));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		stmts.clear();
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ls"), new ObjectCreationExpr(null,
				new ClassOrInterfaceType("ArrayList", new ReferenceType(new ClassOrInterfaceType("Object"))))));
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
				new ClassOrInterfaceType("Object")))), vars);
		stmts.add(new ExpressionStmt(vde));
		stmts.add(new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("Object")),
				new VariableDeclarator(new VariableDeclaratorId("o"))), new NameExpr("params"), new ExpressionStmt(
				new MethodCallExpr(new NameExpr("ls"), "add", new NameExpr("o")))));
		stmts.add(new ReturnStmt(new MethodCallExpr(null, "execute", new NameExpr("caller"),
				new NameExpr("loginUserId"), new NameExpr("cmd"), new NameExpr("ls"))));
		autoGenerateStatements(md.getBody(), "autogenerated:return(execute...)", stmts);
	}

	private void generateQueryPageCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		ClassOrInterfaceType type = new ClassOrInterfaceType("KeyValue");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("Integer")));
		ClassOrInterfaceType type1 = new ClassOrInterfaceType("List");
		type1.setTypeArgs(new LinkedList<Type>());
		type1.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		type.getTypeArgs().add(type1);
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, "queryPage",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
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
		md.getThrows().add(new NameExpr("Exception"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(queryPage)", stmts);
		type = new ClassOrInterfaceType("KeyValue");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("Integer")));
		type1 = new ClassOrInterfaceType("List");
		type1.setTypeArgs(new LinkedList<Type>());
		type1.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		type.getTypeArgs().add(type1);
		stmts.clear();
		vde = new VariableDeclarationExpr(new ReferenceType(type), new VariableDeclarator(new VariableDeclaratorId(
				"ret"), new MethodCallExpr(new NameExpr("bean"), "queryPage", new NameExpr("loginUserId"),
				new NameExpr("cmd"), new NameExpr("firstIndex"), new NameExpr("maxResults"), new NameExpr("params"))));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(queryPage)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(queryPage)", stmts);

		type = new ClassOrInterfaceType("KeyValue", new ReferenceType(new ClassOrInterfaceType("Integer")),
				new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(new ClassOrInterfaceType(
						table.getJavaClassName())))));
		md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, "queryPage",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters()
				.add(new Parameter(new PrimitiveType(Primitive.Long), new VariableDeclaratorId("firstIndex")));
		md.getParameters().add(new Parameter(new PrimitiveType(Primitive.Int), new VariableDeclaratorId("maxResults")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")),
						new VariableDeclaratorId("params"), true));
		md.getThrows().add(new NameExpr("Exception"));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		stmts.clear();
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ls"), new ObjectCreationExpr(null,
				new ClassOrInterfaceType("ArrayList", new ReferenceType(new ClassOrInterfaceType("Object"))))));
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
				new ClassOrInterfaceType("Object")))), vars);
		stmts.add(new ExpressionStmt(vde));
		stmts.add(new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("Object")),
				new VariableDeclarator(new VariableDeclaratorId("o"))), new NameExpr("params"), new ExpressionStmt(
				new MethodCallExpr(new NameExpr("ls"), "add", new NameExpr("o")))));
		stmts.add(new ReturnStmt(new MethodCallExpr(null, "queryPage", new NameExpr("caller"), new NameExpr(
				"loginUserId"), new NameExpr("cmd"), new NameExpr("firstIndex"), new NameExpr("maxResults"),
				new NameExpr("ls"))));
		autoGenerateStatements(md.getBody(), "autogenerated:return(queryPage...)", stmts);
	}

	private void generateQueryCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, "query",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(new Parameter(new PrimitiveType(Primitive.Int), new VariableDeclaratorId("maxResults")));
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(new Parameter(new ReferenceType(type), new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("Exception"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(query)", stmts);
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "query");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("cmd"));
		args.add(new NameExpr("maxResults"));
		args.add(new NameExpr("params"));
		mc.setArgs(args);
		stmts.clear();
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		vde = new VariableDeclarationExpr(new ReferenceType(type), new VariableDeclarator(new VariableDeclaratorId(
				"ret"), mc));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(query)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(query)", stmts);

		type = new ClassOrInterfaceType("List", new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, "query", new LinkedList<Parameter>(),
				null, new LinkedList<NameExpr>());
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(new Parameter(new PrimitiveType(Primitive.Int), new VariableDeclaratorId("maxResults")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")),
						new VariableDeclaratorId("params"), true));
		md.getThrows().add(new NameExpr("Exception"));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		stmts.clear();
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ls"), new ObjectCreationExpr(null,
				new ClassOrInterfaceType("ArrayList", new ReferenceType(new ClassOrInterfaceType("Object"))))));
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
				new ClassOrInterfaceType("Object")))), vars);
		stmts.add(new ExpressionStmt(vde));
		stmts.add(new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("Object")),
				new VariableDeclarator(new VariableDeclaratorId("o"))), new NameExpr("params"), new ExpressionStmt(
				new MethodCallExpr(new NameExpr("ls"), "add", new NameExpr("o")))));
		stmts.add(new ReturnStmt(new MethodCallExpr(null, "query", new NameExpr("caller"), new NameExpr("loginUserId"),
				new NameExpr("cmd"), new NameExpr("maxResults"), new NameExpr("ls"))));
		autoGenerateStatements(md.getBody(), "autogenerated:return(query...)", stmts);
	}

	private void generateFindByUniqueKeyCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		Column uk = table.getUniqueKeyColumn();
		if (uk == null)
			return;
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), "findByUniqueKey", new LinkedList<Parameter>(),
				null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Exception"));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(uk.getDataType().getJavaClassName())),
						new VariableDeclaratorId("keyCode")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(findByUniqueKey)", stmts);
		stmts.clear();
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
				new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(new NameExpr("bean"),
						"findByUniqueKey", new NameExpr("loginUserId"), new NameExpr("keyCode"))));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(findByUniqueKey)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findByUniqueKey)", stmts);
	}

	private void generateEditCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), "edit", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Exception"));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
						new VariableDeclaratorId("o")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		boolean hasFileColumn = false;
		for (Column o : table.getColumns()) {
			if (o.isFile()) {
				Expression v;
				if (table.getLocalCache() != null)
					v = new MethodCallExpr(new NameExpr("local" + table.getJavaClassName() + "Map"), "get",
							new MethodCallExpr(new NameExpr("o"), "getId"));
				else if (table.getCacheConfig() != null)
					v = new MethodCallExpr(new NameExpr(StringHelper.firstWordLower(table.getJavaClassName()) + "Map"),
							"get", new MethodCallExpr(new NameExpr("o"), "getId"));
				else
					v = new MethodCallExpr(null, "find", new MethodCallExpr(new NameExpr("o"), "getId"));
				VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
						table.getJavaClassName())), new VariableDeclarator(new VariableDeclaratorId("old"), v));
				stmts.add(new ExpressionStmt(vde));
				hasFileColumn = true;
				break;
			}
		}
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(edit)", stmts);
		stmts.clear();
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
				new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(new NameExpr("bean"),
						"edit", new NameExpr("loginUserId"), new NameExpr("o"))));
		stmts.add(new ExpressionStmt(vde));
		if (hasFileColumn) {
			List<Statement> ls = new LinkedList<Statement>();
			stmts.add(new IfStmt(new BinaryExpr(new NameExpr("old"), new NullLiteralExpr(), Operator.notEquals),
					new BlockStmt(ls), null));
			for (Column o : table.getColumns()) {
				if (o.isFile()) {
					String getMethodName = o.getDataType().getGetMethodName(o.getVarName());
					List<Statement> bs = new LinkedList<Statement>();
					ls.add(new IfStmt(new BinaryExpr(new BinaryExpr(new MethodCallExpr(new NameExpr("old"),
							getMethodName), new NullLiteralExpr(), Operator.notEquals), new BinaryExpr(new UnaryExpr(
							new MethodCallExpr(new MethodCallExpr(new NameExpr("old"), getMethodName), "equals",
									new MethodCallExpr(new NameExpr("ret"), getMethodName)),
							japa.parser.ast.expr.UnaryExpr.Operator.not), new BinaryExpr(new MethodCallExpr(
							new NameExpr("old"), getMethodName + "HostId"), new IntegerLiteralExpr("-1"),
							Operator.greater), Operator.and), Operator.and), new BlockStmt(bs), null));
					bs.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("old"), StringHelper.toVarName("delete_"
							+ o.getName() + "_file"))));
					break;
				}
			}
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(edit)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(edit)", stmts);
	}

	private void generateInsertCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), "insert", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Exception"));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
						new VariableDeclaratorId("o")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(insert)", stmts);
		stmts.clear();
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
				new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(new NameExpr("bean"),
						"insert", new NameExpr("loginUserId"), new NameExpr("o"))));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(insert)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(insert)", stmts);
	}

	private void generateFindByIdCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())), "findById", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Exception"));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(pkColumn.getDataType().getJavaClassName())),
						new VariableDeclaratorId("id")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(findById)", stmts);
		stmts.clear();
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())),
				new VariableDeclarator(new VariableDeclaratorId("ret"), new MethodCallExpr(new NameExpr("bean"),
						"findById", new NameExpr("loginUserId"), new NameExpr("id"))));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(findById)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findById)", stmts);
	}

	private void generateFindByIdListCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC,
				new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())))), "findByIdList", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Exception"));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
						new ClassOrInterfaceType(pkColumn.getDataType().getJavaClassName())))),
						new VariableDeclaratorId("ls")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(findByIdList)", stmts);
		stmts.clear();
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())))), new VariableDeclarator(new VariableDeclaratorId(
				"ret"), new MethodCallExpr(new NameExpr("bean"), "findByIdList", new NameExpr("loginUserId"),
				new NameExpr("ls"))));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(findByIdList)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findByIdList)", stmts);
	}

	private void generateFindByUniqueKeyListCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC,
				new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())))), "findByUniqueKeyList", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Exception"));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
						new ClassOrInterfaceType("String")))), new VariableDeclaratorId("ls")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(findByUniqueKeyList)", stmts);
		stmts.clear();
		vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("List", new ReferenceType(
				new ClassOrInterfaceType(table.getJavaClassName())))), new VariableDeclarator(new VariableDeclaratorId(
				"ret"), new MethodCallExpr(new NameExpr("bean"), "findByUniqueKeyList", new NameExpr("loginUserId"),
				new NameExpr("ls"))));
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(findByUniqueKeyList)", stmts);
		stmts.clear();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findByUniqueKeyList)", stmts);
	}

	private void generateDeleteCode() {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, new VoidType(), "delete",
				new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Exception"));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"caller")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Long")), new VariableDeclaratorId(
						"loginUserId")));
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
		md.getParameters().add(new Parameter(type, new VariableDeclaratorId("idList")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		boolean hasFileColumn = false;
		List<Statement> stmts = new LinkedList<Statement>();
		for (Column o : table.getColumns()) {
			if (o.isFile()) {
				hasFileColumn = true;
				Expression v;
				if (table.getLocalCache() != null)
					v = new MethodCallExpr(new NameExpr("local" + table.getJavaClassName() + "Map"), "gets",
							new NameExpr("idList"));
				else if (table.getCacheConfig() != null)
					v = new MethodCallExpr(new NameExpr(StringHelper.firstWordLower(table.getJavaClassName()) + "Map"),
							"gets", new NameExpr("idList"));
				else
					v = new MethodCallExpr(null, "find", new NameExpr("idList"));
				VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
						"List", new ClassOrInterfaceType(table.getJavaClassName()))), new VariableDeclarator(
						new VariableDeclaratorId("ls"), v));
				stmts.add(new ExpressionStmt(vde));
				break;
			}
		}
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(def.getEjbProjectName() + "/" + table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:begin(delete)", stmts);
		stmts.clear();
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "delete");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("idList"));
		mc.setArgs(args);
		stmts.add(new ExpressionStmt(mc));
		if (hasFileColumn) {
			for (Column o : table.getColumns()) {
				if (o.isFile()) {
					List<Statement> bs = new LinkedList<Statement>();
					List<CatchClause> cs = new LinkedList<CatchClause>();
					ForeachStmt fs = new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(
							new ClassOrInterfaceType(table.getJavaClassName())), new VariableDeclarator(
							new VariableDeclaratorId("o"))), new NameExpr("ls"), new TryStmt(new BlockStmt(bs), cs,
							null));
					stmts.add(fs);
					bs.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("o"), StringHelper.toVarName("delete_"
							+ o.getName() + "_file"))));
					cs.add(new CatchClause(new Parameter(new ReferenceType(new ClassOrInterfaceType("Throwable")),
							new VariableDeclaratorId("e")), new BlockStmt(new LinkedList<Statement>())));
				}
			}
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(delete)", stmts);

	}
}
