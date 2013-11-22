package kitty.kaf.dao.tools;

import kitty.kaf.dao.source.DaoSource;

import org.w3c.dom.Element;

public class ForeignKey extends BaseConfigDef {
	private String tableRef, column;
	private String delOption, prompt, idListVarName, objListVarName, genCodeTableName, varBindColumn;
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
		idListVarName = el.hasAttribute("id_list_var_name") ? el.getAttribute("id_list_var_name") : null;
		varBindColumn = el.hasAttribute("var_bind_column") ? el.getAttribute("var_bind_column") : null;
		objListVarName = el.hasAttribute("obj_list_var_name") ? el.getAttribute("obj_list_var_name") : null;
		genCodeTableName = el.hasAttribute("gen_code_table") ? el.getAttribute("gen_code_table") : null;
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

}
