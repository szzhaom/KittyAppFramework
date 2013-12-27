package kitty.kaf.dao.tools;

import kitty.kaf.dao.source.DaoSource;

import org.w3c.dom.Element;

public class ForeignKey extends BaseConfigDef {
	private String tableRef, column, refColumn;
	private String delOption, prompt, idListVarName, objListVarName, genCodeTableName, varBindColumn, objVarName;
	private Table table;
	private String varTable, varColumn;

	public ForeignKey() {
	}

	public ForeignKey(Element el, Table table, DaoSource daoSource) {
		this.table = table;
		this.daoSource = daoSource;
		tableRef = el.getAttribute("table_ref");
		column = el.getAttribute("column");
		refColumn = el.hasAttribute("ref_column") ? el.getAttribute("ref_column") : column;
		varColumn = el.hasAttribute("var_column") ? el.getAttribute("var_column") : column;
		delOption = el.getAttribute("del_option");
		prompt = el.getAttribute("prompt");
		idListVarName = el.hasAttribute("id_list_var_name") ? el.getAttribute("id_list_var_name") : null;
		varBindColumn = el.hasAttribute("var_bind_column") ? el.getAttribute("var_bind_column") : null;
		objListVarName = el.hasAttribute("obj_list_var_name") ? el.getAttribute("obj_list_var_name") : null;
		objVarName = el.hasAttribute("obj_var_name") ? el.getAttribute("obj_var_name") : null;
		genCodeTableName = el.hasAttribute("gen_code_table") ? el.getAttribute("gen_code_table") : null;
		varTable = el.hasAttribute("var_table") ? el.getAttribute("var_table") : tableRef;
	}

	public String getVarColumn() {
		return varColumn;
	}

	public void setVarColumn(String varColumn) {
		this.varColumn = varColumn;
	}

	public String getVarTable() {
		return varTable;
	}

	public void setVarTable(String varTable) {
		this.varTable = varTable;
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

	public String getIdListVarName() {
		return idListVarName;
	}

	public void setIdListVarName(String genCodeName) {
		this.idListVarName = genCodeName;
	}

	public String getGenCodeTableName() {
		return genCodeTableName;
	}

	public void setGenCodeTableName(String genCodeTableName) {
		this.genCodeTableName = genCodeTableName;
	}

	public String getObjListVarName() {
		return objListVarName;
	}

	public void setObjListVarName(String objListVarName) {
		this.objListVarName = objListVarName;
	}

	public String getVarBindColumn() {
		return varBindColumn;
	}

	public void setVarBindColumn(String varBindColumn) {
		this.varBindColumn = varBindColumn;
	}

	public String getObjVarName() {
		return objVarName;
	}

	public void setObjVarName(String objVarName) {
		this.objVarName = objVarName;
	}

	public String getRefColumn() {
		return refColumn;
	}

	public void setRefColumn(String refColumn) {
		this.refColumn = refColumn;
	}

}
