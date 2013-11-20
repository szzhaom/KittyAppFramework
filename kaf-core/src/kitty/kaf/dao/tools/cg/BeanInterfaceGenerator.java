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
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
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
public class BeanInterfaceGenerator extends ClassGenerator {
	Table table;
	String pkClass;
	List<Column> pkColumns = null;
	CodeGenerator generator;
	Column pkColumn;

	public BeanInterfaceGenerator(CodeGenerator generator, Table table) {
		super();
		this.generator = generator;
		this.table = table;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		String path = generator.workspaceDir + def.infProjectName + "/src/"
				+ def.getInfPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + table.getJavaClassName()
				+ "BeanRemote.java";
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
				.getInfPackageName())));

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
		addImport("kitty.kaf.cache.CacheValueList");
		addImport("java.sql.SQLException");
		addImport("java.util.List");
		addImport("javax.ejb.Remote");
		addImport("kitty.kaf.io.KeyValue");
		addImport("java.util.Date");
		addImport(table.getFullBeanClassName());
		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu,
				table.getJavaClassName() + "BeanRemote", true,
				ModifierSet.PUBLIC);
		type.setAnnotations(new LinkedList<AnnotationExpr>());
		type.getAnnotations().add(
				new MarkerAnnotationExpr(new NameExpr("Remote")));
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
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC,
				new ReferenceType(type), "queryLatest",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("SQLException"));
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

		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
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

		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
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
		type = new ClassOrInterfaceType("List");
		type.setTypeArgs(new LinkedList<Type>());
		type.getTypeArgs().add(new WildcardType());
		md.getParameters().add(
				new Parameter(new PrimitiveType(Primitive.Long),
						new VariableDeclaratorId("firstIndex")));
		md.getParameters().add(
				new Parameter(new PrimitiveType(Primitive.Int),
						new VariableDeclaratorId("maxResults")));
		md.getParameters().add(
				new Parameter(new ReferenceType(type),
						new VariableDeclaratorId("params")));
		md.getThrows().add(new NameExpr("SQLException"));

		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
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

		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
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
		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
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
		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
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
		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
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
		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
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
		md = JPHelper.addOrUpdateMethod(mainClass, md, false);
	}
}
