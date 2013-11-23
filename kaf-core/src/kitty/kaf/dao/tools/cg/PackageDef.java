package kitty.kaf.dao.tools.cg;

import org.w3c.dom.Element;

/**
 * 代码生成的包工程及路径定义
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public class PackageDef {

	/**
	 * ejb包名
	 */
	String ejbPackageName;
	/**
	 * inf包名
	 */
	String infPackageName;
	/**
	 * enum包名
	 */
	String enumPackageName;
	CodeGenerator generator;

	public PackageDef(CodeGenerator generator, Element el) {
		super();
		this.generator = generator;
		ejbPackageName = el.getAttribute("ejb-package");
		infPackageName = el.getAttribute("inf-package");
		enumPackageName = el.getAttribute("enum-package");
	}

	public String getWebProjectName() {
		return generator.getWebProjectName();
	}

	public String getEnumPackageName() {
		return enumPackageName;
	}

	public void setEnumPackageName(String enumPackageName) {
		this.enumPackageName = enumPackageName;
	}

	public String getEjbProjectName() {
		return generator.getEjbProjectName();
	}

	public String getInfProjectName() {
		return generator.getInfProjectName();
	}

	public String getEjbPackageName() {
		return ejbPackageName;
	}

	public void setEjbPackageName(String ejbPackageName) {
		this.ejbPackageName = ejbPackageName;
	}

	public String getInfPackageName() {
		return infPackageName;
	}

	public String getBeanPackageName() {
		return infPackageName + ".beans";
	}

	public String getDaoPackageName() {
		return ejbPackageName + ".dao";
	}

	public void setInfPackageName(String infPackageName) {
		this.infPackageName = infPackageName;
	}
}
