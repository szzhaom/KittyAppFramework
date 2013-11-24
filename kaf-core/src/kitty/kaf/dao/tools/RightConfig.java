package kitty.kaf.dao.tools;

import org.w3c.dom.Element;

public class RightConfig {
	String insert, delete, edit, query;
	String manage;

	public RightConfig(String p) {
		insert = p.toLowerCase() + "_insert";
		delete = p.toLowerCase() + "_delete";
		edit = p.toLowerCase() + "_edit";
		query = p.toLowerCase() + "_query";
		manage = p.toLowerCase() + "_manage";
	}

	public RightConfig(String p, Element el) {
		insert = p.toLowerCase() + "_insert";
		delete = p.toLowerCase() + "_delete";
		edit = p.toLowerCase() + "_edit";
		query = p.toLowerCase() + "_query";
		manage = p.toLowerCase() + "_manage";
		if (el.hasAttribute("insert"))
			insert = el.getAttribute("insert");
		if (el.hasAttribute("delete"))
			delete = el.getAttribute("delete");
		if (el.hasAttribute("edit"))
			edit = el.getAttribute("edit");
		if (el.hasAttribute("query"))
			query = el.getAttribute("query");
		if (el.hasAttribute("manage"))
			manage = el.getAttribute("manage");
	}

	public String getInsert() {
		return insert;
	}

	public void setInsert(String insert) {
		this.insert = insert;
	}

	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getManage() {
		return manage;
	}

	public void setManage(String manage) {
		this.manage = manage;
	}

}
