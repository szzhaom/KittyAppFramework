package kitty.kaf.dao.tools.cg.jsp;

import kitty.kaf.dao.tools.Column;

import org.w3c.dom.Element;

/**
 * JSP模板的表格列定义
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class JspTableColumn {
	String columnName;
	Column column;
	String width;
	boolean checkboxes;
	String headClass, rowClass;
	TableJspConfig config;
	String caption;

	public JspTableColumn(TableJspConfig config, Element el) {
		this.config = config;
		columnName = el.getAttribute("column");
		column = config.table.findColumnByName(columnName);
		checkboxes = el.hasAttribute("checkbox") ? Boolean.valueOf(el.getAttribute("checkbox")) : false;
		width = el.getAttribute("width");
		headClass = el.getAttribute("head-class");
		rowClass = el.getAttribute("row-class");
		caption = el.hasAttribute("caption") ? el.getAttribute("caption") : null;
	}

	public Column getColumn() {
		return column;
	}

	public String getCaption() {
		if (caption == null)
			return column.getDesp();
		return caption;
	}

	public TableJspConfig getConfig() {
		return config;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public boolean isCheckboxes() {
		return checkboxes;
	}

	public void setCheckboxes(boolean checkboxes) {
		this.checkboxes = checkboxes;
	}

	public String getHeadClass() {
		return headClass;
	}

	public void setHeadClass(String headClass) {
		this.headClass = headClass;
	}

	public String getRowClass() {
		return rowClass;
	}

	public void setRowClass(String rowClass) {
		this.rowClass = rowClass;
	}

}
