package kitty.kaf.dao.tools.cg;

import japa.parser.ASTHelper;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.Expression;
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
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.EnumDef;
import kitty.kaf.dao.tools.EnumItemDef;
import kitty.kaf.helper.JPHelper;

/**
 * Enum 类生成器
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class EnumClassGenerator extends ClassGenerator {
	EnumDef enumDef;
	CodeGenerator generator;

	public EnumClassGenerator(CodeGenerator generator, EnumDef enumDef) {
		super();
		this.generator = generator;
		this.enumDef = enumDef;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		PackageDef def = generator.packageDefs.get(enumDef.getPackageName());
		String path = generator.workspaceDir + def.getInfProjectName() + "/src/"
				+ def.getEnumPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + enumDef.getName() + ".java";
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
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(def.getEnumPackageName())));

		addImport("java.io.IOException");
		addImport("kitty.kaf.io.DataRead");
		addImport("kitty.kaf.io.DataWrite");
		addImport("kitty.kaf.io.Valuable");
		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		EnumDeclaration type = JPHelper.AddEnumDeclartion(cu, enumDef.getName(), ModifierSet.PUBLIC);
		type.setJavaDoc(new JavadocComment("\r\n * " + enumDef.getDesp() + "\r\n "));
		type.setImplements(new LinkedList<ClassOrInterfaceType>());
		ClassOrInterfaceType it = new ClassOrInterfaceType("Valuable");
		it.setTypeArgs(new LinkedList<Type>());
		it.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType("Integer")));
		type.getImplements().add(it);

		return type;
	}

	public void generateBody() throws IOException, ParseException {
		//
		EnumDeclaration ed = (EnumDeclaration) mainClass;
		ed.setEntries(new LinkedList<EnumConstantDeclaration>());
		ed.setMembers(new LinkedList<BodyDeclaration>());
		for (EnumItemDef o : enumDef.getEnumItems()) {
			EnumConstantDeclaration ecd = new EnumConstantDeclaration(o.getName());
			ecd.setClassBody(new LinkedList<BodyDeclaration>());
			MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(
					new ClassOrInterfaceType("String")), "toString");
			md.setBody(new BlockStmt(new LinkedList<Statement>()));
			md.getBody().getStmts().add(new ReturnStmt(new StringLiteralExpr(o.getDesp())));
			ecd.getClassBody().add(md);
			md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("Integer")),
					"getValue");
			md.setBody(new BlockStmt(new LinkedList<Statement>()));
			md.getBody().getStmts().add(new ReturnStmt(new IntegerLiteralExpr(o.getValue())));
			ecd.getClassBody().add(md);
			ed.getEntries().add(ecd);
		}
		// setValue
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "setValue",
				new LinkedList<Parameter>());
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Integer")), new VariableDeclaratorId("v")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		ThrowStmt ts = new ThrowStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType(
				"UnsupportedOperationException")));
		md.getBody().getStmts().add(ts);
		ed.getMembers().add(md);
		// getText
		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("String")), "getText");
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		ReturnStmt rs = new ReturnStmt(new MethodCallExpr(null, "toString"));
		md.getBody().getStmts().add(rs);
		ed.getMembers().add(md);
		// valueOf
		md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new ReferenceType(new ClassOrInterfaceType(
				enumDef.getName())), "valueOf", new LinkedList<Parameter>());
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("int")), new VariableDeclaratorId("value")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		VariableDeclarationExpr vde = new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
				enumDef.getName()), 1), new LinkedList<VariableDeclarator>());
		Expression init = new MethodCallExpr(new NameExpr(enumDef.getName()), "values");
		vde.getVars().add(new VariableDeclarator(new VariableDeclaratorId("values"), init));
		md.getBody().getStmts().add(new ExpressionStmt(vde));
		List<VariableDeclarator> vars = new LinkedList<VariableDeclarator>();
		vars.add(new VariableDeclarator(new VariableDeclaratorId("o")));
		IfStmt ifStmt = new IfStmt(new BinaryExpr(new MethodCallExpr(new NameExpr("o"), "getValue"), new NameExpr(
				"value"), Operator.equals), new ReturnStmt(new NameExpr("o")), null);
		ForeachStmt fs = new ForeachStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
				enumDef.getName())), vars), new NameExpr("values"), ifStmt);
		md.getBody().getStmts().add(fs);
		md.getBody().getStmts()
				.add(new ReturnStmt(new ArrayAccessExpr(new NameExpr("values"), new IntegerLiteralExpr("0"))));
		ed.getMembers().add(md);
		// valueOfObject
		md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new ReferenceType(new ClassOrInterfaceType(
				enumDef.getName())), "valueOfObject", new LinkedList<Parameter>());
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("Object")), new VariableDeclaratorId("str")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));

		ifStmt = new IfStmt(new BinaryExpr(new NameExpr("str"), new NullLiteralExpr(), Operator.equals),
				new ReturnStmt(new ArrayAccessExpr(new MethodCallExpr(new NameExpr(enumDef.getName()), "values"),
						new IntegerLiteralExpr("0"))), null);
		md.getBody().getStmts().add(ifStmt);
		List<Expression> args1 = new LinkedList<Expression>();
		args1.add(new MethodCallExpr(new NameExpr("str"), "toString"));
		List<Expression> args = new LinkedList<Expression>();
		args.add(new MethodCallExpr(new NameExpr("Integer"), "valueOf", args1));
		MethodCallExpr mce = new MethodCallExpr(null, "valueOf", args);
		md.getBody().getStmts().add(new ReturnStmt(mce));
		ed.getMembers().add(md);
		// readFromStream
		md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new ReferenceType(new ClassOrInterfaceType(
				enumDef.getName())), "readFromStream", new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("IOException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("DataRead")), new VariableDeclaratorId(
						"stream")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		mce = new MethodCallExpr(null, "valueOf");
		enumDef.generateReadFromStreamCode(mce);
		md.getBody().getStmts().add(new ReturnStmt(mce));
		ed.getMembers().add(md);
		// writeToStream
		md = new MethodDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC, new VoidType(), "writeToStream",
				new LinkedList<Parameter>(), null, new LinkedList<NameExpr>());
		md.getThrows().add(new NameExpr("IOException"));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType(enumDef.getName())), new VariableDeclaratorId(
						"v")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("DataWrite")), new VariableDeclaratorId(
						"stream")));
		md.setBody(new BlockStmt(new LinkedList<Statement>()));
		mce = new MethodCallExpr(new NameExpr("v"), "getValue");
		mce = enumDef.generateWriteToStreamCode(mce);
		md.getBody().getStmts().add(new ExpressionStmt(mce));
		ed.getMembers().add(md);
	}
}
