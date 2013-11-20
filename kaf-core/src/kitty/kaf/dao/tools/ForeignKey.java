package kitty.kaf.dao.tools;

import kitty.kaf.dao.source.DaoSource;

import org.w3c.dom.Element;

public class ForeignKey extends BaseConfigDef {
	private String tableRef, column;
	private String delOption, prompt;
	private Table table;

	public ForeignKey() {
	}

	public ForeignKey(Element el, Table table, DaoSource daoSource) {
		this.table = table;
		this.daoSource = daoSource;
		tableRef = el.getAttribute("table_ref");
		column = el.getAttribute("column");
		delOption = el.getAttribute("del_option");
		prompt = el.getAttribute("prompt");
	}

	public String getTableRef() {
		return tableRef;
	}

	public void setTableRef(String tableRef) {
		this.tableRef = tableRef;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Table getTable() {
		return table;
	}

	public String getDelOption() {
		return delOption;
	}

	public void setDelOption(String delOption) {
		this.delOption = delOption;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

}
