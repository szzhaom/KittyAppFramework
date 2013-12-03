package kitty.kaf.dao.tools.cg.jsp;

import java.util.ArrayList;
import java.util.List;

import kitty.kaf.dao.tools.Table;

import org.w3c.dom.Element;

public class MenuJspConfig {
	String path, right;
	String name, desp;
	String template;
	String jsFiles;
	String cssFiles;
	List<Table> tables = new ArrayList<Table>();

	public MenuJspConfig(Element el) {
		template = el.hasAttribute("template") ? el.getAttribute("template") : null;
		path = el.getAttribute("path");
		name = el.getAttribute("name");
		desp = el.getAttribute("desp");
		right = el.getAttribute("right");
		if (el.hasAttribute("jsfiles"))
			jsFiles = el.getAttribute("jsfiles");
		else
			jsFiles = "";
		if (el.hasAttribute("cssfiles"))
			cssFiles = el.getAttribute("cssfiles");
		else
			cssFiles = "";
	}

	public String getName() {
		return name;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public String getJsFiles() {
		return jsFiles;
	}

	public void setJsFiles(String jsFiles) {
		this.jsFiles = jsFiles;
	}

	public String getCssFiles() {
		return cssFiles;
	}

	public void setCssFiles(String cssFiles) {
		this.cssFiles = cssFiles;
	}

}
