package kitty.kaf.dao.tools.cg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kitty.kaf.dao.tools.Table;

import org.w3c.dom.Element;

public class TradeExecutorConfig {
	String className;
	String name;
	Map<String, List<Table>> tables = new HashMap<String, List<Table>>();
	String packageName, projectName;

	public TradeExecutorConfig(Element el) {
		name = el.getAttribute("name");
		className = el.getAttribute("classname");
		packageName = el.getAttribute("package-name");
		projectName = el.getAttribute("project-name");
	}

	public TradeExecutorConfig() {
		super();
	}

	public TradeExecutorConfig(String name, String className) {
		super();
		this.name = name;
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, List<Table>> getTables() {
		return tables;
	}

	public void setTables(Map<String, List<Table>> tables) {
		this.tables = tables;
	}

	public String getGroupPackageName() {
		return packageName + ".executors";
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
