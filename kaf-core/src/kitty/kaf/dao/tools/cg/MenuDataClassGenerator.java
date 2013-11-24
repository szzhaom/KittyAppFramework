package kitty.kaf.dao.tools.cg;

import japa.parser.ASTHelper;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.Table;
import kitty.kaf.dao.tools.cg.jsp.MenuJspConfig;
import kitty.kaf.helper.JPHelper;
import kitty.kaf.helper.StringHelper;

/**
 * 菜单类生成器
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class MenuDataClassGenerator extends ClassGenerator {
	CodeGenerator generator;
	MenuJspConfig menuConfig;

	public MenuDataClassGenerator(CodeGenerator generator, MenuJspConfig config) {
		super();
		this.generator = generator;
		menuConfig = config;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		String path = generator.workspaceDir + generator.infProjectName + "/src/"
				+ generator.webPackageName.replace(".", "/").replace("//", "/");
		String fileName = path + "/MenuData.java";
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
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(generator.webPackageName)));
		addImport("java.util.List");
		addImport("java.util.concurrent.CopyOnWriteArrayList");
		addImport("kitty.kaf.session.SessionUser");
		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu, "MenuData", false, ModifierSet.PUBLIC);
		type.setJavaDoc(new JavadocComment("\r\n * 自动生成的菜单数据值\r\n "));
		return type;
	}

	public void generateBody() throws IOException, ParseException {
		List<String> rightList = new ArrayList<String>();
		HashMap<String, Long> rightMap = new HashMap<String, Long>();
		for (String str : generator.rightTable.getTableData().getRows()) {
			String[] s = StringHelper.splitToStringArray(str, ",");
			String n = s[1].trim();
			n = n.substring(1, n.length() - 1);
			rightList.add(n);
			rightMap.put(n, Long.valueOf(s[0].trim()));
		}
		createMenuDateDefClass();
		List<Type> types = new LinkedList<Type>();
		types.add(new ClassOrInterfaceType("MenuDataDef"));
		List<Type> types1 = new LinkedList<Type>();
		types1.add(new ClassOrInterfaceType("MenuDataDef"));
		FieldDeclaration fd = new FieldDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new ReferenceType(
				new ClassOrInterfaceType("List", types)),
				new VariableDeclarator(new VariableDeclaratorId("mainMenuDefs"), new ObjectCreationExpr(null,
						new ClassOrInterfaceType("CopyOnWriteArrayList", types1))));
		List<BodyDeclaration> members = new LinkedList<BodyDeclaration>();
		members.add(fd);
		List<Statement> stmts = new LinkedList<Statement>();
		InitializerDeclaration id = new InitializerDeclaration(true, new BlockStmt(stmts));
		for (MenuJspConfig o : generator.menuJspList) {
			String var = StringHelper.toVarName(o.getName()) + "Menu";
			members.add(new FieldDeclaration(ModifierSet.STATIC, new ReferenceType(new ClassOrInterfaceType(
					"MenuDataDef")), new VariableDeclarator(new VariableDeclaratorId(var))));
			Expression expr;
			String rightstr = o.getRight();
			Long right = (rightstr == null || rightstr.trim().isEmpty()) ? 0L : rightMap.get(rightstr.trim());
			List<Expression> args = new LinkedList<Expression>();
			args.add(new IntegerLiteralExpr(right + "L"));
			args.add(new StringLiteralExpr(o.getName()));
			args.add(new StringLiteralExpr(rightstr));
			args.add(new StringLiteralExpr(o.getDesp()));
			args.add(new StringLiteralExpr(o.getPath() + ".go"));
			ObjectCreationExpr oce = new ObjectCreationExpr(null, new ClassOrInterfaceType("MenuDataDef"), args);
			expr = new AssignExpr(new NameExpr(var), oce, Operator.assign);
			stmts.add(new ExpressionStmt(expr));
			stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("mainMenuDefs"), "add", new NameExpr(var))));
			for (Table t : o.getTables()) {
				String tn = t.getName();
				if (tn.startsWith("t_"))
					tn = tn.substring(2);
				right = t.getRightConfig().getManage() != null ? rightMap.get(t.getRightConfig().getManage()) : null;
				if (right == null)
					right = 0L;
				stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr(var + ".subMenuDefs"), "add",
						new ObjectCreationExpr(null, new ClassOrInterfaceType("MenuDataDef"), new LongLiteralExpr(right
								+ "L"), new StringLiteralExpr(tn),
								new StringLiteralExpr(t.getRightConfig().getManage()), new StringLiteralExpr(t
										.getDesp()), new StringLiteralExpr(t.getJspConfig().getQueryConfig().getPath()
										+ ".go")))));
			}
		}
		members.add(id);
		autoGenerateMembers(mainClass, "autogenerated:static(initlist)", members);
		fd = new FieldDeclaration(0, new ReferenceType(new ClassOrInterfaceType("SessionUser")),
				new VariableDeclarator(new VariableDeclaratorId("user")));
		JPHelper.addOrUpdateFieldsToClass(mainClass, fd);
		ConstructorDeclaration cdd = new ConstructorDeclaration(ModifierSet.PUBLIC, "MenuData");
		cdd.setParameters(new LinkedList<Parameter>());
		cdd.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("SessionUser")), new VariableDeclaratorId(
						"user")));
		cdd.setBlock(new BlockStmt(new LinkedList<Statement>()));
		cdd = JPHelper.addOrUpdateonstructor(mainClass, cdd, true);
		List<Statement> ls = new LinkedList<Statement>();
		ls.add(new ExpressionStmt(new AssignExpr(new FieldAccessExpr(new NameExpr("this"), "user"),
				new NameExpr("user"), Operator.assign)));
		autoGenerateStatements(cdd.getBlock(), "autogenerated:body(MenuData)", ls);
		generateMenuList();
	}

	private void generateMenuList() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType(
				"List", new ClassOrInterfaceType("MenuDataDef"))), "getMainMenuList");
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		ls.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("List",
				new ReferenceType(new ClassOrInterfaceType("MenuDataDef")))), new VariableDeclarator(
				new VariableDeclaratorId("list"), new ObjectCreationExpr(null, new ClassOrInterfaceType("ArrayList",
						new ReferenceType(new ClassOrInterfaceType("MenuDataDef"))))))));
		List<Statement> stmts = new LinkedList<Statement>();
		stmts.add(new IfStmt(new BinaryExpr(new BinaryExpr(new FieldAccessExpr(new NameExpr("o"), "right"),
				new LongLiteralExpr("0L"), japa.parser.ast.expr.BinaryExpr.Operator.equals), new MethodCallExpr(
				new NameExpr("user"), "hasRight", new FieldAccessExpr(new NameExpr("o"), "right")),
				japa.parser.ast.expr.BinaryExpr.Operator.or), new ExpressionStmt(new MethodCallExpr(
				new NameExpr("list"), "add", new NameExpr("o"))), null));
		ls.add(new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("MenuDataDef")),
				new VariableDeclarator(new VariableDeclaratorId("o"))), new NameExpr("mainMenuDefs"), new BlockStmt(
				stmts)));
		ls.add(new ReturnStmt(new NameExpr("list")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(getMainMenuList)", ls);

		for (MenuJspConfig o : generator.menuJspList) {
			String var = StringHelper.toVarName(o.getName()) + "Menu";
			String mname = "get" + StringHelper.firstWordCap(var) + "MenuList";

			md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("List",
					new ClassOrInterfaceType("MenuDataDef"))), mname);
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			ls = new LinkedList<Statement>();
			ls.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("List",
					new ReferenceType(new ClassOrInterfaceType("MenuDataDef")))), new VariableDeclarator(
					new VariableDeclaratorId("list"), new ObjectCreationExpr(null, new ClassOrInterfaceType(
							"ArrayList", new ReferenceType(new ClassOrInterfaceType("MenuDataDef"))))))));
			stmts = new LinkedList<Statement>();
			stmts.add(new IfStmt(new BinaryExpr(new BinaryExpr(new FieldAccessExpr(new NameExpr("o"), "right"),
					new LongLiteralExpr("0L"), japa.parser.ast.expr.BinaryExpr.Operator.equals), new MethodCallExpr(
					new NameExpr("user"), "hasRight", new FieldAccessExpr(new NameExpr("o"), "right")),
					japa.parser.ast.expr.BinaryExpr.Operator.or), new ExpressionStmt(new MethodCallExpr(new NameExpr(
					"list"), "add", new NameExpr("o"))), null));
			ls.add(new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(
					new ClassOrInterfaceType("MenuDataDef")), new VariableDeclarator(new VariableDeclaratorId("o"))),
					new FieldAccessExpr(new NameExpr(var), "subMenuDefs"), new BlockStmt(stmts)));
			ls.add(new ReturnStmt(new NameExpr("list")));
			autoGenerateStatements(md.getBody(), "autogenerated:return(" + mname + ")", ls);
		}
	}

	private void createMenuDateDefClass() {
		for (BodyDeclaration d : mainClass.getMembers()) {
			if (d instanceof ClassOrInterfaceDeclaration) {
				if (((ClassOrInterfaceDeclaration) d).getName().equals("MenuDataDef")) {
					mainClass.getMembers().remove(d);
					break;
				}
			}
		}
		ClassOrInterfaceDeclaration cd = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC,
				false, "MenuDataDef");
		mainClass.getMembers().add(0, cd);
		cd.setMembers(new LinkedList<BodyDeclaration>());
		FieldDeclaration fd = new FieldDeclaration(0, new PrimitiveType(Primitive.Long), new VariableDeclarator(
				new VariableDeclaratorId("right")));
		cd.getMembers().add(fd);
		cd.getMembers().add(
				new FieldDeclaration(0, new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclarator(
						new VariableDeclaratorId("name"))));
		cd.getMembers().add(
				new FieldDeclaration(0, new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclarator(
						new VariableDeclaratorId("desp"))));
		cd.getMembers().add(
				new FieldDeclaration(0, new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclarator(
						new VariableDeclaratorId("url"))));
		cd.getMembers().add(
				new FieldDeclaration(0, new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclarator(
						new VariableDeclaratorId("rightName"))));
		List<Type> types = new LinkedList<Type>();
		types.add(new ClassOrInterfaceType("MenuDataDef"));
		List<Type> types1 = new LinkedList<Type>();
		types1.add(new ClassOrInterfaceType("MenuDataDef"));
		fd = new FieldDeclaration(0, new ReferenceType(new ClassOrInterfaceType("List", types)),
				new VariableDeclarator(new VariableDeclaratorId("subMenuDefs"), new ObjectCreationExpr(null,
						new ClassOrInterfaceType("CopyOnWriteArrayList", types))));
		cd.getMembers().add(fd);
		ConstructorDeclaration cdd = new ConstructorDeclaration(ModifierSet.PUBLIC, "MenuDataDef");
		cd.getMembers().add(cdd);
		List<Parameter> params = new LinkedList<Parameter>();
		params.add(new Parameter(new PrimitiveType(Primitive.Long), new VariableDeclaratorId("right")));
		params.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("String")),
				new VariableDeclaratorId("name")));
		params.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId(
				"rightName")));
		params.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("String")),
				new VariableDeclaratorId("desp")));
		params.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId("url")));
		cdd.setParameters(params);
		List<Statement> stmts = new LinkedList<Statement>();
		stmts.add(new ExplicitConstructorInvocationStmt());
		stmts.add(new ExpressionStmt(new AssignExpr(new FieldAccessExpr(new NameExpr("this"), "right"), new NameExpr(
				"right"), Operator.assign)));
		stmts.add(new ExpressionStmt(new AssignExpr(new FieldAccessExpr(new NameExpr("this"), "name"), new NameExpr(
				"name"), Operator.assign)));
		stmts.add(new ExpressionStmt(new AssignExpr(new FieldAccessExpr(new NameExpr("this"), "rightName"),
				new NameExpr("rightName"), Operator.assign)));
		stmts.add(new ExpressionStmt(new AssignExpr(new FieldAccessExpr(new NameExpr("this"), "desp"), new NameExpr(
				"desp"), Operator.assign)));
		stmts.add(new ExpressionStmt(new AssignExpr(new FieldAccessExpr(new NameExpr("this"), "url"), new NameExpr(
				"url"), Operator.assign)));
		cdd.setBlock(new BlockStmt(stmts));

		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(Primitive.Long), "getRight");
		cd.getMembers().add(md);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("right")));
		md.setBody(new BlockStmt(stmts));

		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("String")), "getUrl");
		cd.getMembers().add(md);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("url")));
		md.setBody(new BlockStmt(stmts));

		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("String")), "getDesp");
		cd.getMembers().add(md);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("desp")));
		md.setBody(new BlockStmt(stmts));

		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("String")),
				"getRightName");
		cd.getMembers().add(md);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("rightName")));
		md.setBody(new BlockStmt(stmts));

		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("String")), "getName");
		cd.getMembers().add(md);
		stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("name")));
		md.setBody(new BlockStmt(stmts));
	}
}
