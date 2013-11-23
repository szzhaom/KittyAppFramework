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
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ExpressionStmt;
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
		addImport("java.util.Date");
		addImport("kitty.kaf.io.KeyValue");
		addImport("kitty.kaf.pools.jndi.JndiConnectionFactory");
		addImport("kitty.kaf.pools.jndi.Lookuper");
		addImport(table.getFullBeanClassName());
		addImport("kitty.kaf.cache.CacheValueList");
		if (table.getMemcachedConfig() != null) {
			addImport("kitty.kaf.pools.memcached.MemcachedClient");
			addImport("kitty.kaf.cache.MemcachedCallback");
			if (table.getUniqueKeyColumn() != null) {
				addImport("kitty.kaf.pools.memcached.UniqueKeyMemcachedMap");
			} else
				addImport("kitty.kaf.pools.memcached.MemcachedMap");
		}
		if (table.getLocalCache() != null) {
			addImport("kitty.kaf.listeners.ItemChangedEventListener");
			addImport("kitty.kaf.pools.memcached.MemcachedClient");
			addImport("kitty.kaf.cache.LocalCacheCallback");
			addImport("kitty.kaf.cache.LocalCachedMap");
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
		args.add(new ObjectCreationExpr(null, new ClassOrInterfaceType(table.getJavaClassName()), null));
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
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "queryLatest");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("cmd"));
		args.add(new NameExpr("firstIndex"));
		args.add(new NameExpr("maxResults"));
		args.add(new NameExpr("lastModified"));
		mc.setArgs(args);
		stmts.add(new ReturnStmt(mc));
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

	private String[] generateMemcached(List<BodyDeclaration> members, String name1, String name2) {
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
			args.add(new NullLiteralExpr());
			args.add(new StringLiteralExpr(table.getMemcachedConfig()));
			vars.add(new VariableDeclarator(new VariableDeclaratorId("mc"), new MethodCallExpr(new NameExpr(
					"MemcachedClient"), "newInstance", args)));
			FieldDeclaration fd = new FieldDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC | ModifierSet.FINAL,
					new ReferenceType(new ClassOrInterfaceType("MemcachedClient")), vars);
			members.add(fd);
			ret[0] = "mc";
		}
		if (name2.trim().length() > 0) {
			if (!name2.equals(name1)) {
				List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
				List<Expression> args = new LinkedList<Expression>();
				args.add(new NullLiteralExpr());
				args.add(new StringLiteralExpr(name2));
				if (name1.length() == 0)
					ret[1] = "mc";
				else
					ret[1] = "mc1";
				vars.add(new VariableDeclarator(new VariableDeclaratorId(ret[1]), new MethodCallExpr(new NameExpr(
						"MemcachedClient"), "newInstance", args)));
				FieldDeclaration fd = new FieldDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC | ModifierSet.FINAL,
						new ReferenceType(new ClassOrInterfaceType("MemcachedClient")), vars);
				members.add(fd);
			} else
				ret[1] = "mc";
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
		String mc[] = generateMemcached(members, table.getMemcachedConfig(), localCacheMc);
		if (mc[0] != null) {
			generateMemcachedMap(mc[0], members);
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
		}
	}

	private void generateLocalCacheMap(String mc, List<BodyDeclaration> members, String localCacheInterval) {
		ObjectCreationExpr init = new ObjectCreationExpr(null, new ClassOrInterfaceType("ItemChangedEventListener"),
				null);
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

		LinkedList<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		VariableDeclarator vd = new VariableDeclarator(new VariableDeclaratorId("itemsChangedEventListener"), init);
		vars.add(vd);
		FieldDeclaration fd = new FieldDeclaration(ModifierSet.STATIC, new ReferenceType(new ClassOrInterfaceType(
				"ItemChangedEventListener")), vars);
		members.add(fd);

		init = new ObjectCreationExpr(null, new ClassOrInterfaceType("LocalCacheCallback"), null);
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
		type = new ClassOrInterfaceType("LocalCachedMap");
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
		init = new ObjectCreationExpr(null, type, args);
		vd = new VariableDeclarator(new VariableDeclaratorId("local" + table.getJavaClassName() + "Map"), init);
		vars.add(vd);

		type = new ClassOrInterfaceType("LocalCachedMap");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		fd = new FieldDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC, type, vars);
		members.add(fd);
		generator.database.getLocalCacheTables().add(table);
	}

	private void generateMemcachedMap(String mc, List<BodyDeclaration> members) {
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		Column uk = table.getUniqueKeyColumn();

		ClassOrInterfaceType type = new ClassOrInterfaceType(uk == null ? "MemcachedMap" : "UniqueKeyMemcachedMap");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
		if (uk != null)
			type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("String")));
		type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(table.getJavaClassName())));
		args = new LinkedList<Expression>();
		ObjectCreationExpr expr = new ObjectCreationExpr(null, new ClassOrInterfaceType("MemcachedCallback"), null);
		expr.setAnonymousClassBody(new LinkedList<BodyDeclaration>());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType(
				"Object")), "onGetCacheValue", new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(),
				new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("Throwable"));
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters()
				.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId(
						"source")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId("id")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("ret"), new NullLiteralExpr()));
		md.getBody()
				.getStmts()
				.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())), vars)));
		args = new LinkedList<Expression>();
		args.add(new NullLiteralExpr());
		args.add(new NullLiteralExpr());
		args.add(new CastExpr(new ReferenceType(new ClassOrInterfaceType(pkClass)), new NameExpr("id")));
		Statement thenStmt = null, elseStmt = null;
		thenStmt = new ExpressionStmt(new AssignExpr(new NameExpr("ret"), new MethodCallExpr(null, "findById", args),
				AssignExpr.Operator.assign));
		if (uk != null) {
			LinkedList<Expression> args1 = new LinkedList<Expression>();
			args1.add(new NullLiteralExpr());
			args1.add(new NullLiteralExpr());
			args1.add(new CastExpr(new ReferenceType(new ClassOrInterfaceType("String")), new NameExpr("id")));
			elseStmt = new ExpressionStmt(new AssignExpr(new NameExpr("ret"), new MethodCallExpr(null,
					"findByUniqueKey", args1), AssignExpr.Operator.assign));
		}
		IfStmt ifStmt = new IfStmt(new InstanceOfExpr(new NameExpr("id"), new ReferenceType(new ClassOrInterfaceType(
				pkClass))), thenStmt, elseStmt);
		md.getBody().getStmts().add(ifStmt);
		args = new LinkedList<Expression>();
		args.add(new NameExpr("ret"));
		md.getBody().getStmts().add(new ExpressionStmt(new MethodCallExpr(null, "getCacheValueCompete", args)));
		md.getBody().getStmts().add(new ReturnStmt(new NameExpr("ret")));
		expr.getAnonymousClassBody().add(md);
		md = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(Primitive.Boolean), "isNullId",
				new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
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
		expr.getAnonymousClassBody().add(md);

		type = new ClassOrInterfaceType(uk == null ? "MemcachedMap" : "UniqueKeyMemcachedMap");
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
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "execute");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("cmd"));
		args.add(new NameExpr("params"));
		mc.setArgs(args);
		stmts.add(new ReturnStmt(mc));
		autoGenerateStatements(md.getBody(), "autogenerated:return(execute)", stmts);
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
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "queryPage");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("cmd"));
		args.add(new NameExpr("firstIndex"));
		args.add(new NameExpr("maxResults"));
		args.add(new NameExpr("params"));
		mc.setArgs(args);
		stmts.add(new ReturnStmt(mc));
		autoGenerateStatements(md.getBody(), "autogenerated:return(queryPage)", stmts);
	}

	private void generateQueryCode() {
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
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
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
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "query");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("cmd"));
		args.add(new NameExpr("maxResults"));
		args.add(new NameExpr("params"));
		mc.setArgs(args);
		stmts.add(new ReturnStmt(mc));
		autoGenerateStatements(md.getBody(), "autogenerated:return(query)", stmts);
	}

	private void generateFindByUniqueKeyCode() {
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
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "findByUniqueKey");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("keyCode"));
		mc.setArgs(args);
		stmts.add(new ReturnStmt(mc));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findByUniqueKey)", stmts);
	}

	private void generateEditCode() {
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
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "edit");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("o"));
		mc.setArgs(args);
		stmts.add(new ReturnStmt(mc));
		autoGenerateStatements(md.getBody(), "autogenerated:return(edit)", stmts);
	}

	private void generateInsertCode() {
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
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "insert");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("o"));
		mc.setArgs(args);
		stmts.add(new ReturnStmt(mc));
		autoGenerateStatements(md.getBody(), "autogenerated:return(insert)", stmts);
	}

	private void generateFindByIdCode() {
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
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "findById");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("id"));
		mc.setArgs(args);
		stmts.add(new ReturnStmt(mc));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findById)", stmts);
	}

	private void generateDeleteCode() {
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
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		List<Expression> args = new LinkedList<Expression>();
		List<Statement> stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new StringLiteralExpr("db"));
		args.add(new NameExpr("caller"));
		args.add(new FieldAccessExpr(new NameExpr("Lookuper"), "JNDI_TYPE_EJB"));
		args.add(new StringLiteralExpr(table.getEjbName() + "Bean"));
		args.add(new ClassExpr(new ReferenceType(new ClassOrInterfaceType(beanRemote))));
		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("bean"), new MethodCallExpr(new NameExpr(
				"JndiConnectionFactory"), "lookup", args)));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(
				new ClassOrInterfaceType(beanRemote)), vars);
		stmts.add(new ExpressionStmt(vde));
		MethodCallExpr mc = new MethodCallExpr(new NameExpr("bean"), "delete");
		args = new LinkedList<Expression>();
		args.add(new NameExpr("loginUserId"));
		args.add(new NameExpr("idList"));
		mc.setArgs(args);
		stmts.add(new ExpressionStmt(mc));
		autoGenerateStatements(md.getBody(), "autogenerated:return(delete)", stmts);

	}
}
