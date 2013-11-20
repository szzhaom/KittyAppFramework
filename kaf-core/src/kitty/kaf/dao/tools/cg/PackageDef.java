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
	 * ejb工程名
	 */
	String ejbProjectName;
	/**
	 * inf工程名
	 */
	String infProjectName;
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
	String webProjectName;

	public PackageDef(Element el) {
		super();
		ejbPackageName = el.getAttribute("ejb-package");
		infPackageName = el.getAttribute("inf-package");
		ejbProjectName = el.getAttribute("ejb-project-name");
		infProjectName = el.getAttribute("inf-project-name");
		enumPackageName = el.getAttribute("enum-package");
		webProjectName = el.getAttribute("web-project-name");
	}

	public String getWebProjectName() {
		return webProjectName;
	}

	public void setWebProjectName(String webProjectName) {
		this.webProjectName = webProjectName;
	}

	public String getEnumPackageName() {
		return enumPackageName;
	}

	public void setEnumPackageName(String enumPackageName) {
		this.enumPackageName = enumPackageName;
	}

	public String getEjbProjectName() {
		return ejbProjectName;
	}

	public void setEjbProjectName(String ejbProjectName) {
		this.ejbProjectName = ejbProjectName;
	}

	public String getInfProjectName() {
		return infProjectName;
	}

	public void setInfProjectName(String infProjectName) {
		this.infProjectName = infProjectName;
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
