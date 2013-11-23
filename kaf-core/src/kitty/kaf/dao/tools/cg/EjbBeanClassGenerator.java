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
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
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

/**
 * Bean 类生成器
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class EjbBeanClassGenerator extends ClassGenerator {
	Table table;
	String pkClass;
	List<Column> pkColumns = null;
	CodeGenerator generator;
	Column pkColumn;

	public EjbBeanClassGenerator(CodeGenerator generator, Table table) {
		super();
		this.generator = generator;
		this.table = table;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		String path = generator.workspaceDir + def.getEjbProjectName()  + "/src/"
				+ def.getEjbPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + table.getJavaClassName() + "Bean.java";
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
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(def
				.getEjbPackageName())));

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
		addImport("java.util.List");
		addImport("javax.ejb.Stateless");
		addImport("kitty.kaf.io.KeyValue");
		addImport("kitty.kaf.cache.CacheValueList");
		addImport("java.util.Date");
		addImport("kitty.kaf.dao.DaoBean");
		addImport(table.getFullDaoHelperClassName());
		addImport(table.getFullBeanClassName());
		addImport(table.getFullBeanRemoteClassName());
		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu,
				table.getJavaClassName() + "Bean", false, ModifierSet.PUBLIC);
		type.setAnnotations(new LinkedList<AnnotationExpr>());
		NormalAnnotationExpr expr = new NormalAnnotationExpr(new NameExpr(
				"Stateless"), new LinkedList<MemberValuePair>());
		expr.getPairs().add(
				new MemberValuePair("name", new StringLiteralExpr(table
						.getEjbName() + "Bean")));
		expr.getPairs().add(
				new MemberValuePair("mappedName", new StringLiteralExpr(table
						.getEjbName() + "Bean")));
		type.getAnnotations().add(expr);
		type.setExtends(new LinkedList<ClassOrInterfaceType>());
		type.getExtends().add(new ClassOrInterfaceType("DaoBean"));
		type.setImplements(new LinkedList<ClassOrInterfaceType>());
		type.getImplements().add(
				new ClassOrInterfaceType(table.getJavaClassName()
						+ "BeanRemote"));
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
		generateQueryLatestCode();
		generateExecuteCode();
	}

	private void generateQueryPageCode() {
		ClassOrInterfaceType type = new ClassOrInterfaceType("KeyValue");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(
				new ReferenceType(new ClassOrInterfaceType("Integer")));
		ClassOrInterfaceType type1 = new ClassOrInterfaceType("List");
		type1.setTypeArgs(new LinkedList<Type>());
		type1.getTypeArgs().add(
				new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())));
		type.getTypeArgs().add(type1);
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, type,
				"queryPage", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(
						"String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(
				new Parameter(new PrimitiveType(Primitive.Long),
						new VariableDeclaratorId("firstIndex")));
		md.getParameters().add(
				new Parameter(new PrimitiveType(Primitive.Int),
						new VariableDeclaratorId("maxResults")));
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(
				new Parameter(new ReferenceType(type),
						new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		type = new ClassOrInterfaceType("KeyValue");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(
				new ReferenceType(new ClassOrInterfaceType("Integer")));
		type1 = new ClassOrInterfaceType("List");
		type1.setTypeArgs(new LinkedList<Type>());
		type1.getTypeArgs().add(
				new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())));
		type.getTypeArgs().add(type1);
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(type), new LinkedList<VariableDeclarator>());
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "queryPage",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("cmd"));
		mce.getArgs().add(new NameExpr("firstIndex"));
		mce.getArgs().add(new NameExpr("maxResults"));
		mce.getArgs().add(new NameExpr("params"));
		VariableDeclarator vd = new VariableDeclarator(
				new VariableDeclaratorId("ret"), mce);
		vde.getVars().add(vd);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(queryPage)",
				stmts);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(queryPage)",
				stmts);
	}

	private void generateExecuteCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC,
				new ReferenceType(new ClassOrInterfaceType("Object")),
				"execute", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(
						"String")), new VariableDeclaratorId("cmd")));
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(
				new Parameter(new ReferenceType(type),
						new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(new ClassOrInterfaceType("Object")),
				new LinkedList<VariableDeclarator>());
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "execute",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("cmd"));
		mce.getArgs().add(new NameExpr("params"));
		VariableDeclarator vd = new VariableDeclarator(
				new VariableDeclaratorId("ret"), mce);
		vde.getVars().add(vd);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(execute)",
				stmts);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(execute)",
				stmts);
	}

	private void generateQueryLatestCode() {
		ClassOrInterfaceType type = new ClassOrInterfaceType("CacheValueList");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		type.getTypeArgs().add(new WildcardType());
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, type,
				"queryLatest", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(
						"String")), new VariableDeclaratorId("cmd")));
		md.getParameters().add(
				new Parameter(new PrimitiveType(Primitive.Long),
						new VariableDeclaratorId("firstIndex")));
		md.getParameters().add(
				new Parameter(new PrimitiveType(Primitive.Int),
						new VariableDeclaratorId("maxResults")));
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Date")),
						new VariableDeclaratorId("lastModified")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		type = new ClassOrInterfaceType("CacheValueList");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		type.getTypeArgs().add(new WildcardType());
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(type), new LinkedList<VariableDeclarator>());
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "queryLatest",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("cmd"));
		mce.getArgs().add(new NameExpr("firstIndex"));
		mce.getArgs().add(new NameExpr("maxResults"));
		mce.getArgs().add(new NameExpr("lastModified"));
		VariableDeclarator vd = new VariableDeclarator(
				new VariableDeclaratorId("ret"), mce);
		vde.getVars().add(vd);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(queryLatest)",
				stmts);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(),
				"autogenerated:return(queryLatest)", stmts);
	}

	private void generateQueryCode() {
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(
				new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())));
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, type,
				"query", new LinkedList<Parameter>(), null,
				new LinkedList<NameExpr>());
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(
						"String")), new VariableDeclaratorId("cmd")));
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(
				new Parameter(new PrimitiveType(Primitive.Int),
						new VariableDeclaratorId("maxResults")));
		md.getParameters().add(
				new Parameter(new ReferenceType(type),
						new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(
				new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(type), new LinkedList<VariableDeclarator>());
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "query",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("cmd"));
		mce.getArgs().add(new NameExpr("maxResults"));
		mce.getArgs().add(new NameExpr("params"));
		VariableDeclarator vd = new VariableDeclarator(
				new VariableDeclaratorId("ret"), mce);
		vde.getVars().add(vd);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(query)", stmts);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(query)",
				stmts);
	}

	private void generateFindByUniqueKeyCode() {
		Column uk = table.getUniqueKeyColumn();
		if (uk == null)
			return;
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC,
				new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())), "findByUniqueKey",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(uk
						.getDataType().getJavaClassName())),
						new VariableDeclaratorId("keyCode")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(new ClassOrInterfaceType(
						table.getJavaClassName())),
				new LinkedList<VariableDeclarator>());
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "findByUniqueKey",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("keyCode"));
		VariableDeclarator vd = new VariableDeclarator(
				new VariableDeclaratorId("ret"), mce);
		vde.getVars().add(vd);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(),
				"autogenerated:body(findByUniqueKey)", stmts);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(),
				"autogenerated:return(findByUniqueKey)", stmts);
	}

	private void generateEditCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC,
				new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())), "edit",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())), new VariableDeclaratorId("o")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(new ClassOrInterfaceType(
						table.getJavaClassName())),
				new LinkedList<VariableDeclarator>());
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "edit",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("o"));
		VariableDeclarator vd = new VariableDeclarator(
				new VariableDeclaratorId("ret"), mce);
		vde.getVars().add(vd);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(edit)", stmts);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(edit)",
				stmts);
	}

	private void generateInsertCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC,
				new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())), "insert",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())), new VariableDeclaratorId("o")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(new ClassOrInterfaceType(
						table.getJavaClassName())),
				new LinkedList<VariableDeclarator>());
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "insert",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("o"));
		VariableDeclarator vd = new VariableDeclarator(
				new VariableDeclaratorId("ret"), mce);
		vde.getVars().add(vd);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(insert)",
				stmts);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(insert)",
				stmts);
	}

	private void generateFindByIdCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC,
				new ReferenceType(new ClassOrInterfaceType(table
						.getJavaClassName())), "findById",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(
						pkColumn.getDataType().getJavaClassName())),
						new VariableDeclaratorId("id")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		VariableDeclarationExpr vde = new VariableDeclarationExpr(
				new ReferenceType(new ClassOrInterfaceType(
						table.getJavaClassName())),
				new LinkedList<VariableDeclarator>());
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "findById",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("id"));
		VariableDeclarator vd = new VariableDeclarator(
				new VariableDeclaratorId("ret"), mce);
		vde.getVars().add(vd);
		stmts.add(new ExpressionStmt(vde));
		autoGenerateStatements(md.getBody(), "autogenerated:body(findById)",
				stmts);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(findById)",
				stmts);
	}

	private void generateDeleteCode() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC,
				new VoidType(), "delete", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(
						new ClassOrInterfaceType("Long")),
						new VariableDeclaratorId("loginUserId")));
		ClassOrInterfaceType type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(
				new ReferenceType(new ClassOrInterfaceType(pkClass)));
		md.getParameters().add(
				new Parameter(type, new VariableDeclaratorId("idList")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		MethodCallExpr mce = new MethodCallExpr(new NameExpr(
				table.getJavaClassName() + "DaoHelper"), "delete",
				new LinkedList<Expression>());
		mce.getArgs().add(new MethodCallExpr(null, "getDao"));
		mce.getArgs().add(new NameExpr("loginUserId"));
		mce.getArgs().add(new NameExpr("idList"));
		stmts.add(new ExpressionStmt(mce));
		autoGenerateStatements(md.getBody(), "autogenerated:body(delete)",
				stmts);
	}
}
