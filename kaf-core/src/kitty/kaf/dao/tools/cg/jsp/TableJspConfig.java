package kitty.kaf.dao.tools.cg.jsp;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.Table;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * table的jsp模板配置
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class TableJspConfig {
	QueryJspConfig queryConfig;
	EditJspConfig editConfig;
	Table table;
	String menuName;

	public TableJspConfig(Table table, Element el) {
		this.table = table;
		NodeList ls = el.getElementsByTagName("jsp-config");
		if (ls.getLength() > 0) {
			el = (Element) ls.item(0);
			menuName = el.getAttribute("menu");
			ls = el.getElementsByTagName("query");
			if (ls.getLength() > 0) {
				queryConfig = new QueryJspConfig(this, (Element) ls.item(0));
			}
			ls = el.getElementsByTagName("edit");
			if (ls.getLength() > 0) {
				editConfig = new EditJspConfig(this, (Element) ls.item(0));
			}
		}
	}

	public EditJspConfig getEditConfig() {
		return editConfig;
	}

	public void setEditConfig(EditJspConfig editConfig) {
		this.editConfig = editConfig;
	}

	public QueryJspConfig getQueryConfig() {
		return queryConfig;
	}

	public void setQueryConfig(QueryJspConfig queryConfig) {
		this.queryConfig = queryConfig;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getPlaceHolder() {
		StringBuffer sb = new StringBuffer();
		for (Column c : table.getColumns()) {
			if (c.isUniqueKeyField() || c.isToStringField()) {
				if (sb.length() > 0)
					sb.append("、");
				sb.append(c.getDesp());
			}
		}
		return "输入" + sb.toString() + "搜索";
	}
}
