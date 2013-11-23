package kitty.kaf.dao.tools.cg.jsp;

import java.util.ArrayList;
import java.util.List;

import kitty.kaf.dao.tools.Table;

import org.w3c.dom.Element;

public class MenuJspConfig {
	String path;
	String name, desp;
	String template;
	List<Table> tables = new ArrayList<Table>();

	public MenuJspConfig(Element el) {
		template = el.hasAttribute("template") ? el.getAttribute("template") : null;
		path = el.getAttribute("path");
		name = el.getAttribute("name");
		desp = el.getAttribute("desp");
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

}
