package kitty.kaf.dao.tools;

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;

public class JavaParserTest {
	public static void main(String[] args) {
		CompilationUnit cu = new CompilationUnit();
		cu.setPackage(new PackageDeclaration(ASTHelper
				.createNameExpr("kitty.kaf.dao.table")));
		ClassOrInterfaceDeclaration type = new ClassOrInterfaceDeclaration(
				ModifierSet.PUBLIC, false, "TableObject");
		ASTHelper.addTypeDeclaration(cu, type);
		type.setJavaDoc(new JavadocComment("sadfsadf\r\n * asdfasdf\r\n "));
		MethodDeclaration method = new MethodDeclaration(ModifierSet.PUBLIC,
				ASTHelper.VOID_TYPE, "main");
		method.setModifiers(ModifierSet.addModifier(method.getModifiers(),
				ModifierSet.STATIC));
		ASTHelper.addMember(type, method);
		method.setJavaDoc(new JavadocComment("\r\n     * 主函数\r\n     "));
		Parameter param = ASTHelper.createParameter(
				ASTHelper.createReferenceType("String", 0), "args");
		param.setVarArgs(true);
		ASTHelper.addParameter(method, param);
		BlockStmt block = new BlockStmt();
		method.setBody(block);
		NameExpr clazz = new NameExpr("System");
		FieldAccessExpr field = new FieldAccessExpr(clazz, "out");
		MethodCallExpr call = new MethodCallExpr(field, "println");
		ASTHelper.addArgument(call, new StringLiteralExpr("Hello World!"));
		ASTHelper.addStmt(block, call);

		System.out.println(cu);
	}
}
