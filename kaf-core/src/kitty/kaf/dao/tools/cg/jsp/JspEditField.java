package kitty.kaf.dao.tools.cg.jsp;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.helper.StringHelper;

import org.w3c.dom.Element;

/**
 * JSP模板的编辑字段定义
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class JspEditField {
	Column column;
	String regExp;
	String normalPrompt, errorPrompt;
	TableJspConfig config;
	String type;
	String maxValue, minValue, minLength, maxLength, url, urlTextField;
	String readonly;
	String params;
	String field, desp, value;
	String checkboxes;
	String multiselect;
	String depths;

	public JspEditField(TableJspConfig config, Element el) {
		this.config = config;
		if (el.hasAttribute("column"))
			column = config.table.findColumnByName(el.getAttribute("column"));
		else {
			field = el.getAttribute("field");
			desp = el.getAttribute("desp");
		}
		multiselect = el.hasAttribute("multiselect") ? el.getAttribute("multiselect") : null;
		checkboxes = el.hasAttribute("checkboxes") ? el.getAttribute("checkboxes") : null;
		regExp = el.hasAttribute("reg_exp") ? el.getAttribute("reg_exp") : null;
		normalPrompt = el.hasAttribute("normal_prompt") ? el.getAttribute("normal_prompt") : null;
		errorPrompt = el.hasAttribute("error_prompt") ? el.getAttribute("error_prompt") : null;
		type = el.hasAttribute("type") ? el.getAttribute("type") : null;
		maxValue = el.hasAttribute("max_value") ? el.getAttribute("max_value") : null;
		minValue = el.hasAttribute("min_value") ? el.getAttribute("min_value") : null;
		params = el.hasAttribute("params") ? el.getAttribute("params") : null;
		desp = el.hasAttribute("desp") ? el.getAttribute("desp") : null;
		minLength = el.hasAttribute("min_length") ? el.getAttribute("min_length") : null;
		maxLength = el.hasAttribute("max_length") ? el.getAttribute("max_length") : null;
		readonly = el.hasAttribute("readonly") ? el.getAttribute("readonly") : null;
		url = el.hasAttribute("url") ? el.getAttribute("url") : null;
		urlTextField = el.hasAttribute("url_text_field") ? el.getAttribute("url_text_field") : null;
		value = el.hasAttribute("value") ? el.getAttribute("value") : null;
		depths = el.hasAttribute("depths") ? el.getAttribute("depths") : null;
	}

	public String getDepths() {
		if (depths == null || depths.trim().isEmpty())
			return "1";
		return depths.trim();
	}

	public void setDepths(String depths) {
		this.depths = depths;
	}

	public String getValue() {
		if (value == null) {
			return "${data." + this.getVarName() + "}";
		} else
			return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUrlTextField() {
		if (urlTextField == null)
			return "";
		return urlTextField;
	}

	public void setUrlTextField(String urlTextField) {
		this.urlTextField = urlTextField;
	}

	public String getMultiselect() {
		if (multiselect == null)
			return "false";
		return multiselect;
	}

	public void setMultiselect(String multiselect) {
		this.multiselect = multiselect;
	}

	public String getCheckboxes() {
		if (checkboxes == null)
			return "false";
		return checkboxes;
	}

	public void setCheckboxes(String checkboxes) {
		this.checkboxes = checkboxes;
	}

	public String getUrl() {
		if (url == null)
			return "";
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		if (type == null) {
			if (column != null) {
				if (column.isSecret())
					type = "password";
				else if (column.getDataType().getCustomJavaClassName() != null) {
					type = "combo";
				} else if (column.getDataType().getDataType().equalsIgnoreCase("date")) {
					type = "date";
				} else
					type = "text";
			}
		}
		return type;
	}

	public String getParams() {
		if (params == null) {
			if (column != null) {
				if (column.getDataType().getCustomJavaClassName() != null) {
					return "${mysession.globalData.enumValues."
							+ StringHelper.firstWordLower(column.getDataType().getCustomJavaClassName()) + "List}";
				}
			}
			return "";
		}
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReadonly() {
		if (readonly == null) {
			if (column == config.getTable().getPkColumn())
				return "${data.id!=null}";
			else
				return "false";
		}
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public Column getColumn() {
		return column;
	}

	public TableJspConfig getConfig() {
		return config;
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public String getNormalPrompt() {
		if (normalPrompt == null || normalPrompt.length() == 0) {
			normalPrompt = "&nbsp;";
		}
		return normalPrompt;
	}

	public void setNormalPrompt(String normalPrompt) {
		this.normalPrompt = normalPrompt;
	}

	public String getMinValue() {
		if (minValue != null)
			return minValue;
		else if (column != null) {
			if (column.getDataType().getCustomJavaClassName() != null)
				return "";
			else if (column.getDataType().getDataType().equalsIgnoreCase("byte"))
				return "1";
			else if (column.getDataType().getDataType().equalsIgnoreCase("short"))
				return "1";
			else if (column.getDataType().getDataType().equalsIgnoreCase("int"))
				return "1";
			else if (column.getDataType().getDataType().equalsIgnoreCase("short"))
				return "1";
			else
				return "";
		} else
			return "";
	}

	public String getMaxValue() {
		if (maxValue != null)
			return maxValue;
		else if (column != null) {
			if (column.getDataType().getCustomJavaClassName() != null)
				return "";
			else if (column.getDataType().getDataType().equalsIgnoreCase("byte"))
				return "255";
			else if (column.getDataType().getDataType().equalsIgnoreCase("short"))
				return "65535";
			else if (column.getDataType().getDataType().equalsIgnoreCase("int"))
				return "1000000000";
			else if (column.getDataType().getDataType().equalsIgnoreCase("short"))
				return "100000000000000000";
			else
				return "";
		} else
			return "";
	}

	public String getMinLength() {
		if (minLength != null)
			return minLength;
		else if (column != null) {
			if (column.isNullable())
				return "0";
			else if (!column.isVarLength())
				return Integer.toString(column.getLength());
			else
				return "1";
		} else
			return "0";
	}

	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}

	public String getMaxLength() {
		if (maxLength != null)
			return maxLength;
		else if (column != null) {
			if (column.getLength() > 0)
				return Integer.toString(column.getLength());
			else if (column.getDataType().getDataType().equalsIgnoreCase("byte"))
				return "3";
			else if (column.getDataType().getDataType().equalsIgnoreCase("short"))
				return "5";
			else if (column.getDataType().getDataType().equalsIgnoreCase("int"))
				return "10";
			else if (column.getDataType().getDataType().equalsIgnoreCase("short"))
				return "18";
			else if (column.getDataType().getDataType().equalsIgnoreCase("string"))
				return Integer.toString(column.getLength());
			else
				return "0";
		} else
			return "0";
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public void setConfig(TableJspConfig config) {
		this.config = config;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getErrorPrompt() {
		if (errorPrompt == null) {
			if (column != null) {
				String n = column.getDataType().getShortName();
				if (n.equalsIgnoreCase("Byte")) {
					errorPrompt = "请输入1到255之间的数字";
				} else if (n.equalsIgnoreCase("Short")) {
					errorPrompt = "请输入1到65535之间的数字";
				} else if (n.equalsIgnoreCase("Int")) {
					errorPrompt = "请输入1到1000000000之间的数字";
				} else if (n.equalsIgnoreCase("Long")) {
					errorPrompt = "请输入1到100000000000000000之间的数字";
				} else
					errorPrompt = "输入错误";
			} else
				errorPrompt = "输入错误";
		}
		return errorPrompt;
	}

	public void setErrorPrompt(String errorPrompt) {
		this.errorPrompt = errorPrompt;
	}

	public String getField() {
		if (column != null)
			return column.getName();
		else
			return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getDesp() {
		if (column != null)
			return column.getDesp();
		else
			return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getVarName() {
		if (column != null) {
			if (column.getDataType().getCustomJavaClassName() != null)
				return StringHelper.toVarName(getField()) + ".value";
		}
		return StringHelper.toVarName(getField());
	}
}
