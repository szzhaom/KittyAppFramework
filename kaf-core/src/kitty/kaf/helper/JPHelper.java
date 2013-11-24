package kitty.kaf.helper;

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;

import java.util.LinkedList;

/**
 * JavaParser 助手程序
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class JPHelper {

	/**
	 * 查找类定义
	 * 
	 * @param cu
	 *            Java文件单元对象
	 * @param name
	 *            类名
	 * @return 如果找到，则返回类定义，否则，返回null
	 */
	public static TypeDeclaration findTypeDeclartion(CompilationUnit cu, String name) {
		for (TypeDeclaration t : cu.getTypes()) {
			if (t.getName().equals(name))
				return t;
		}
		return null;
	}

	/**
	 * 查找字段定义
	 * 
	 * @param c
	 *            类或接口
	 * @param name
	 *            字段名
	 * @return 如果找到，则返回字段定义，否则，返回null
	 */
	public static FieldDeclaration findFieldDeclartion(TypeDeclaration c, String name) {
		for (BodyDeclaration t : c.getMembers()) {
			if (t instanceof FieldDeclaration) {
				FieldDeclaration cc = (FieldDeclaration) t;
				if (cc.getVariables().get(0).getId().getName().equals(name))
					return cc;
			}
		}
		return null;
	}

	/**
	 * 查找方法定义
	 * 
	 * @param c
	 *            类或接口
	 * @param name
	 *            方法名称
	 * @return 如果找到，则返回方法定义，否则，返回null
	 */
	public static BodyDeclaration findBodyDeclartion(TypeDeclaration c, String name) {
		for (BodyDeclaration t : c.getMembers()) {
			if (t instanceof MethodDeclaration) {
				MethodDeclaration m = (MethodDeclaration) t;
				if (m.getName().equals(name))
					return m;
			}
			if (t instanceof ConstructorDeclaration) {
				ConstructorDeclaration m = (ConstructorDeclaration) t;
				if (m.getName().equals(name))
					return m;
			}
		}
		return null;
	}

	/**
	 * 如果类存在，直接返回已经存在的类定义，否则，则新建一类
	 * 
	 * @param cu
	 *            Java文件单元对象
	 * @param name
	 *            类名
	 * @param isInterface
	 *            是否是接口定义
	 * @param modifiers
	 *            修饰符，如：public,static等
	 * @return 新建或存在的名为name的类定义
	 */
	public static ClassOrInterfaceDeclaration AddClassDeclartion(CompilationUnit cu, String name, boolean isInterface,
			int modifiers) {
		ClassOrInterfaceDeclaration type = (ClassOrInterfaceDeclaration) JPHelper.findTypeDeclartion(cu, name);
		if (type == null) {
			type = new ClassOrInterfaceDeclaration(modifiers, isInterface, name);
			ASTHelper.addTypeDeclaration(cu, type);
		}
		return type;
	}

	/**
	 * 如果类存在，直接返回已经存在的类定义，否则，则新建一类
	 * 
	 * @param cu
	 *            Java文件单元对象
	 * @param name
	 *            类名
	 * @param isInterface
	 *            是否是接口定义
	 * @param modifiers
	 *            修饰符，如：public,static等
	 * @return 新建或存在的名为name的类定义
	 */
	public static EnumDeclaration AddEnumDeclartion(CompilationUnit cu, String name, int modifiers) {
		EnumDeclaration type = (EnumDeclaration) JPHelper.findTypeDeclartion(cu, name);
		if (type == null) {
			type = new EnumDeclaration(modifiers, name);
			ASTHelper.addTypeDeclaration(cu, type);
		}
		return type;
	}

	/**
	 * 添加或更新一个类的extends
	 * 
	 * @param c
	 *            类定义
	 * @param extendClassName
	 *            extends的类名
	 * @param types
	 *            extends的类型数组
	 */
	public static void updateExtendsToClass(ClassOrInterfaceDeclaration c, String extendClassName, Type... types) {
		if (c.getExtends() == null)
			c.setExtends(new LinkedList<ClassOrInterfaceType>());
		ClassOrInterfaceType ct = null;
		for (ClassOrInterfaceType o : c.getExtends()) {
			if (o.getName().equals(extendClassName)) {
				ct = o;
				break;
			}
		}
		if (ct == null)
			ct = new ClassOrInterfaceType(extendClassName);
		if (ct.getTypeArgs() == null)
			ct.setTypeArgs(new LinkedList<Type>());
		for (Type t : types) {
			if (!ct.getTypeArgs().contains(t))
				ct.getTypeArgs().add(t);
		}
		if (!c.getExtends().contains(ct))
			c.getExtends().add(ct);
	}

	/**
	 * 添加或更新一个类的字段
	 * 
	 * @param c
	 *            类定义
	 * @param field
	 *            字段定义
	 */
	public static void addOrUpdateFieldsToClass(TypeDeclaration c, FieldDeclaration field) {
		VariableDeclaratorId id = field.getVariables().get(0).getId();
		for (BodyDeclaration o : c.getMembers()) {
			if (o instanceof FieldDeclaration) {
				FieldDeclaration d = (FieldDeclaration) o;
				if (id.equals(d.getVariables().get(0).getId())) {
					d.setModifiers(field.getModifiers());
					d.setType(field.getType());
					d.setVariables(field.getVariables());
					d.setAnnotations(field.getAnnotations());
					d.setData(field.getData());
					return;
				}
			}
		}
		c.getMembers().add(field);
	}

	public static MethodDeclaration addOrUpdateMethod(BodyDeclaration cl, MethodDeclaration md, boolean checkBody) {
		ClassOrInterfaceDeclaration c = (ClassOrInterfaceDeclaration) cl;
		MethodDeclaration omd = (MethodDeclaration) findBodyDeclartion(c, md.getName());
		if (omd != null) {
			omd.setAnnotations(md.getAnnotations());
			omd.setJavaDoc(md.getJavaDoc());
			omd.setModifiers(md.getModifiers());
			omd.setParameters(md.getParameters());
			omd.setThrows(md.getThrows());
			omd.setType(md.getType());
			omd.setTypeParameters(md.getTypeParameters());
			md = omd;
		} else {
			c.getMembers().add(md);
		}
		if (checkBody) {
			if (md.getBody() == null)
				md.setBody(new BlockStmt());
			if (md.getBody().getStmts() == null)
				md.getBody().setStmts(new LinkedList<Statement>());
		}
		return md;
	}

	public static ConstructorDeclaration addOrUpdateonstructor(BodyDeclaration cl, ConstructorDeclaration md,
			boolean checkBody) {
		ClassOrInterfaceDeclaration c = (ClassOrInterfaceDeclaration) cl;
		ConstructorDeclaration omd = (ConstructorDeclaration) findBodyDeclartion(c, md.getName());
		if (omd != null) {
			omd.setAnnotations(md.getAnnotations());
			omd.setJavaDoc(md.getJavaDoc());
			omd.setModifiers(md.getModifiers());
			omd.setParameters(md.getParameters());
			omd.setThrows(md.getThrows());
			omd.setTypeParameters(md.getTypeParameters());
			md = omd;
		} else {
			c.getMembers().add(md);
		}
		if (checkBody) {
			if (md.getBlock() == null)
				md.setBlock(new BlockStmt());
			if (md.getBlock().getStmts() == null)
				md.getBlock().setStmts(new LinkedList<Statement>());
		}
		return md;
	}

	/**
	 * 通过条件表达式，找到If语句中对应条件的语句
	 * 
	 * @param ifStmt
	 *            If语句
	 * @param condition
	 *            条件
	 * @return 找到的语句
	 */
	public static IfStmt findIfStmtByCondition(IfStmt ifStmt, Expression condition) {
		if (ifStmt.getCondition().equals(condition))
			return ifStmt;
		else if (ifStmt.getElseStmt() != null && ifStmt.getElseStmt() instanceof IfStmt)
			return findIfStmtByCondition((IfStmt) ifStmt.getElseStmt(), condition);
		else
			return null;
	}
}
