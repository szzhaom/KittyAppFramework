package kitty.kaf.dao.tools.cg.jsp;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class QueryJspConfig extends JspConfig {
	List<JspTableColumn> tableColumns = new ArrayList<JspTableColumn>();
	String path, prevIdName;
	String jsFiles;
	String cssFiles;
	List<JspOptionActionConfig> actions = new ArrayList<JspOptionActionConfig>();
	String createButtonDesp, deleteButtonDesp, editButtonDesp;

	public QueryJspConfig(TableJspConfig config, Element el) {
		super(config, el);
		path = el.getAttribute("path");
		NodeList ls = el.getElementsByTagName("table_col");
		for (int i = 0; i < ls.getLength(); i++) {
			tableColumns.add(new JspTableColumn(config, (Element) ls.item(i)));
		}
		ls = el.getElementsByTagName("action");
		for (int i = 0; i < ls.getLength(); i++) {
			actions.add(new JspOptionActionConfig(config, (Element) ls.item(i)));
		}
		if (el.hasAttribute("jsfiles"))
			jsFiles = el.getAttribute("jsfiles");
		else
			jsFiles = "";
		if (el.hasAttribute("cssfiles"))
			cssFiles = el.getAttribute("cssfiles");
		else
			cssFiles = "";
		if (el.hasAttribute("create_button_desp"))
			createButtonDesp = el.getAttribute("create_button_desp");
		if (el.hasAttribute("delete_button_desp"))
			deleteButtonDesp = el.getAttribute("delete_button_desp");
		if (el.hasAttribute("edit_button_desp"))
			editButtonDesp = el.getAttribute("edit_button_desp");
		if (el.hasAttribute("prev_id_name"))
			prevIdName = el.getAttribute("prev_id_name");
	}

	public List<JspTableColumn> getTableColumns() {
		return tableColumns;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public String getCreateButtonDesp() {
		if (createButtonDesp == null)
			return "创建新" + config.getTable().getDesp();
		return createButtonDesp;
	}

	public void setCreateButtonDesp(String createButtonDesp) {
		this.createButtonDesp = createButtonDesp;
	}

	public String getDeleteButtonDesp() {
		if (deleteButtonDesp == null)
			return "删除" + config.getTable().getDesp();
		return deleteButtonDesp;
	}

	public void setDeleteButtonDesp(String deleteButtonDesp) {
		this.deleteButtonDesp = deleteButtonDesp;
	}

	public String getEditButtonDesp() {
		if (editButtonDesp == null)
			return "编辑" + config.getTable().getDesp();
		return editButtonDesp;
	}

	public void setEditButtonDesp(String editButtonDesp) {
		this.editButtonDesp = editButtonDesp;
	}

	public String getPrevIdName() {
		if (prevIdName == null)
			return "prev_id";
		return prevIdName;
	}

}
