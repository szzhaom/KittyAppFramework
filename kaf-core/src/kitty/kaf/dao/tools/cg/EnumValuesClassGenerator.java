package kitty.kaf.dao.tools.cg;

import japa.parser.ASTHelper;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.EnumDef;
import kitty.kaf.helper.JPHelper;

/**
 * Enum 类生成器
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class EnumValuesClassGenerator extends ClassGenerator {
	CodeGenerator generator;

	public EnumValuesClassGenerator(CodeGenerator generator) {
		super();
		this.generator = generator;
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		String path = generator.workspaceDir + generator.infProjectName + "/src/"
				+ generator.enumPackageName.replace(".", "/").replace("//", "/");
		String fileName = path + "/EnumValues.java";
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
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(generator.enumPackageName)));

		return cu;
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu, "EnumValues", false, ModifierSet.PUBLIC);
		type.setJavaDoc(new JavadocComment("\r\n * 自动生成的枚举类值\r\n "));
		return type;
	}

	public void generateBody() throws IOException, ParseException {
		for (EnumDef e : generator.database.getEnumDefs().values()) {
			MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(
					new ClassOrInterfaceType(e.getName()), 1), "get" + e.getName() + "List");
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			List<Statement> stmts = new LinkedList<Statement>();
			stmts.add(new ReturnStmt(new MethodCallExpr(new NameExpr(e.getName()), "values")));
			autoGenerateStatements(md.getBody(), "autogenerated:body(get" + e.getName() + "List)", stmts);
		}
	}
}