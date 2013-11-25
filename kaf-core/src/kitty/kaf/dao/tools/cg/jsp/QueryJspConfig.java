package kitty.kaf.dao.tools.cg.jsp;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class QueryJspConfig extends JspConfig {
	List<JspTableColumn> tableColumns = new ArrayList<JspTableColumn>();
	String path;

	public QueryJspConfig(TableJspConfig config, Element el) {
		super(config, el);
		path = el.getAttribute("path");
		NodeList ls = el.getElementsByTagName("table_col");
		for (int i = 0; i < ls.getLength(); i++) {
			tableColumns.add(new JspTableColumn(config, (Element) ls.item(i)));
		}
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

}
