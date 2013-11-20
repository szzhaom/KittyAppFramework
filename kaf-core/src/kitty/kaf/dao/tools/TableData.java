package kitty.kaf.dao.tools;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 表的数据
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class TableData {
	Table table;
	boolean createRunOnly;
	String rightClass;
	List<String> rows = new ArrayList<String>();

	public TableData(Table table, Element el) {
		this.table = table;
		createRunOnly = el.hasAttribute("create_run_only") ? Boolean.valueOf(el
				.getAttribute("create_run_only")) : false;
		rightClass = el.hasAttribute("rightClass") ? el
				.getAttribute("rightClass") : null;
		NodeList ls = el.getElementsByTagName("row");
		for (int i = 0; i < ls.getLength(); i++) {
			rows.add(((Element) ls.item(i)).getAttribute("values"));
		}
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public boolean isCreateRunOnly() {
		return createRunOnly;
	}

	public void setCreateRunOnly(boolean createRunOnly) {
		this.createRunOnly = createRunOnly;
	}

	public String getRightClass() {
		return rightClass;
	}

	public void setRightClass(String rightClass) {
		this.rightClass = rightClass;
	}

	public List<String> getRows() {
		return rows;
	}

	public void setRows(List<String> rows) {
		this.rows = rows;
	}
}
