package kitty.kaf.dao.tools.cg;

import japa.parser.ASTHelper;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.ReferenceType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.Table;
import kitty.kaf.helper.JPHelper;
import kitty.kaf.helper.StringHelper;

/**
 * 权限类生成器
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class RightClassGenerator extends ClassGenerator {
	Table table;
	CodeGenerator generator;

	public RightClassGenerator(CodeGenerator generator, Table table) {
		super();
		this.generator = generator;
		this.table = table;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		if (def == null)
			throw new IOException(table.getName() + " not package def");
		String path = generator.workspaceDir + def.getInfProjectName()  + "/src/"
				+ def.getBeanPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + table.getTableData().getRightClass()
				+ ".java";
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
				.getBeanPackageName())));
		addImport("java.io.Serializable");
		addImport("kitty.kaf.session.SessionUser");
		return cu;
	}

	public void generateBody() throws IOException, ParseException {
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId(
				"serialVersionUID"), new LongLiteralExpr("1L")));
		FieldDeclaration fd = new FieldDeclaration(ModifierSet.PRIVATE
				| ModifierSet.STATIC | ModifierSet.FINAL, new PrimitiveType(
				Primitive.Long), vars);
		JPHelper.addOrUpdateFieldsToClass(mainClass, fd);

		vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("user")));
		fd = new FieldDeclaration(ModifierSet.PRIVATE, new ReferenceType(
				new ClassOrInterfaceType("SessionUser")), vars);
		JPHelper.addOrUpdateFieldsToClass(mainClass, fd);

		for (String str : table.getTableData().getRows()) {
			String[] s = StringHelper.splitToStringArray(str, ",");
			String n = s[1].trim();
			n = n.substring(1, n.length() - 1);
			vars = new LinkedList<VariableDeclarator>();
			vars.add(new VariableDeclarator(new VariableDeclaratorId(n
					.toUpperCase()), new LongLiteralExpr(s[0].trim() + "L")));
			fd = new FieldDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC
					| ModifierSet.FINAL, new PrimitiveType(Primitive.Long),
					vars);
			JPHelper.addOrUpdateFieldsToClass(mainClass, fd);
			vars = new LinkedList<VariableDeclarator>();
			vars.add(new VariableDeclarator(new VariableDeclaratorId(
					StringHelper.toVarName(n + "_Enabled"))));
			fd = new FieldDeclaration(ModifierSet.PRIVATE, new ReferenceType(
					new ClassOrInterfaceType("Boolean")), vars);
			JPHelper.addOrUpdateFieldsToClass(mainClass, fd);

			MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC,
					new PrimitiveType(Primitive.Boolean),
					StringHelper.toVarName("IS_" + n + "_Enabled"));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			List<Expression> args = new LinkedList<Expression>();
			args.add(new NameExpr(n.toUpperCase()));
			MethodCallExpr mce = new MethodCallExpr(new NameExpr("user"),
					"hasRight", args);
			List<Statement> stmts = new ArrayList<Statement>();
			IfStmt ifStmt = new IfStmt(new BinaryExpr(new NameExpr(
					StringHelper.toVarName(n + "_Enabled")),
					new NullLiteralExpr(), Operator.equals),
					new ExpressionStmt(new AssignExpr(new NameExpr(StringHelper
							.toVarName(n + "_Enabled")), mce,
							japa.parser.ast.expr.AssignExpr.Operator.assign)),
					null);
			stmts.add(ifStmt);
			autoGenerateStatements(md.getBody(), "autogenerated:body("
					+ StringHelper.toVarName("IS_" + n + "_ENABLED") + ")",
					stmts);
			stmts = new LinkedList<Statement>();
			stmts.add(new ReturnStmt(new NameExpr(StringHelper.toVarName(n
					+ "_Enabled"))));
			autoGenerateStatements(md.getBody(), "autogenerated:return("
					+ StringHelper.toVarName("IS_" + n + "_ENABLED") + ")",
					stmts);
		}
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper
				.AddClassDeclartion(cu, table.getTableData().getRightClass(),
						false, ModifierSet.PUBLIC);
		type.setJavaDoc(new JavadocComment("\r\n * " + table.getDesp()
				+ "\r\n "));
		if (type.getImplements() == null)
			type.setImplements(new LinkedList<ClassOrInterfaceType>());
		ClassOrInterfaceType t = new ClassOrInterfaceType("Serializable");
		if (!type.getImplements().contains(t))
			type.getImplements().add(t);
		return type;
	}

}
