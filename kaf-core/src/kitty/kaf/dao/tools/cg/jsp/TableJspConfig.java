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
	QueryJspTemplateConfig queryConfig;
	EditJspTemplateConfig editConfig;
	Table table;

	public TableJspConfig(Table table, Element el) {
		this.table = table;
		NodeList ls = el.getElementsByTagName("jsp-config");
		if (ls.getLength() > 0) {
			el = (Element) ls.item(0);
			ls = el.getElementsByTagName("query");
			if (ls.getLength() > 0) {
				queryConfig = new QueryJspTemplateConfig(this, (Element) ls.item(0));
			}
			ls = el.getElementsByTagName("edit");
			if (ls.getLength() > 0) {
				editConfig = new EditJspTemplateConfig(this, (Element) ls.item(0));
			}
		}
	}

	public EditJspTemplateConfig getEditConfig() {
		return editConfig;
	}

	public void setEditConfig(EditJspTemplateConfig editConfig) {
		this.editConfig = editConfig;
	}

	public QueryJspTemplateConfig getQueryConfig() {
		return queryConfig;
	}

	public void setQueryConfig(QueryJspTemplateConfig queryConfig) {
		this.queryConfig = queryConfig;
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
