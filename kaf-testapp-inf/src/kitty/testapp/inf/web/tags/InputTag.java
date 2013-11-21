package kitty.testapp.inf.web.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspWriter;

import kitty.kaf.dao.table.IdTableObject;
import kitty.kaf.exceptions.CoreException;
import kitty.kaf.helper.StringHelper;
import kitty.kaf.io.ListItemable;
import kitty.kaf.io.TreeNode;
import kitty.kaf.io.Valuable;
import kitty.kaf.io.ValueTextable;
import kitty.kaf.json.JSONArray;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.json.NoQuoteString;
import kitty.kaf.webframe.tags.BasicTag;

public class InputTag extends BasicTag {
	private static final long serialVersionUID = 1L;
	private String type = "text";
	private String placeHolder;
	private String style, styleClass;
	private Object value;
	private Boolean multiSelect, disabled, readOnly, checkboxes;
	private Object params;
	private int depths;
	private String url;
	private String textFieldName;
	private String regExp;
	private String errorPrompt, normalPrompt, prompt;
	private Object autoComplete;
	private Integer maxLength, minLength;
	private Object maxValue, minValue;
	private String buttonClick;

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		writer.write("<div id='p_" + getId() + "'></div>");
		writer.write("<script type='text/javascript'>");
		JSONObject json = new JSONObject();
		try {
			json.put("id", getId());
			json.put("panel", "p_" + getId());
			if (placeHolder != null)
				json.put("placeholder", placeHolder);
			if (value != null)
				json.put("value", value);
			if (styleClass != null)
				json.put("class", styleClass);
			if (multiSelect != null)
				json.put("multiselect", multiSelect);
			if (readOnly != null)
				json.put("readonly", readOnly);
			if (maxLength != null)
				json.put("maxlength", maxLength);
			if (minLength != null)
				json.put("minlength", minLength);
			if (maxValue != null)
				json.put("maxvalue", maxValue);
			if (minValue != null)
				json.put("minvalue", minValue);
			if (regExp != null)
				json.put("regexp", regExp);
			if (errorPrompt != null)
				json.put("errorprompt", errorPrompt);
			if (normalPrompt != null)
				json.put("normalprompt", normalPrompt);
			if (prompt != null)
				json.put("prompt", prompt);
			if (checkboxes != null)
				json.put("checkboxes", checkboxes);
			if (autoComplete != null)
				json.put("autocomplete", autoComplete);
			if (type.equals("searchlist")) {
				type = "list";
				json.put("searchInputParams", new JSONObject());
				json.put("nextButtonParams", new JSONObject());
			} else if (type.equals("searchcombobox")) {
				type = "combobox";
				json.put("searchInputParams", new JSONObject());
				json.put("nextButtonParams", new JSONObject());
			} else if (type.equals("searchchosenbox")) {
				type = "chosenbox";
				json.put("searchInputParams", new JSONObject());
				json.put("nextButtonParams", new JSONObject());
			}
			if (params != null) {
				if (params instanceof TreeNode<?, ?>) {
					TreeNode<?, ?> node = (TreeNode<?, ?>) params;
					node = (TreeNode<?, ?>) node.copy();
					List<Object> ls = new ArrayList<Object>();
					if (value != null) {
						if (value instanceof Object[]) {
							for (Object o : (Object[]) value)
								ls.add(o);
						} else if (value instanceof Collection<?>)
							ls.addAll((Collection<?>) value);
						else
							ls.add(value);
					}
					node.enableDepths(ls, depths > 0 ? depths : 1000000);
					node.toJson(json, 1000000);
				} else {
					JSONArray a = new JSONArray();
					json.put("items", a);
					Object[] array = null;
					if (params instanceof Object[]) {
						array = (Object[]) params;
					} else if (params instanceof Collection<?>) {
						array = ((Collection<?>) params).toArray();
					} else if (params instanceof Map<?, ?>) {
						array = ((Map<?, ?>) params).values().toArray();
					}
					if (array != null) {
						List<String> ls = new ArrayList<String>();
						StringHelper.split(value + "", ",", ls);
						StringBuffer sb = new StringBuffer();
						String tn = "text";// iscolor ? "color" : "text";
						for (Object o : array) {
							Object id = null;
							String text = null;
							if (o instanceof ListItemable<?>) {
								ListItemable<?> d = (ListItemable<?>) o;
								id = d.getId();
								text = d.getText();
							} else if (o instanceof ValueTextable<?>) {
								ValueTextable<?> d = (ValueTextable<?>) o;
								id = d.getValue();
								text = d.getText();
							} else if (o instanceof Valuable<?>) {
								Valuable<?> d = (Valuable<?>) o;
								id = d.getValue();
								text = d.toString();
							} else if (o instanceof IdTableObject<?>) {
								IdTableObject<?> d = (IdTableObject<?>) o;
								id = d.getId();
								text = d.toString();
							} else
								throw new CoreException("参数类型不正确");
							if (ls.contains(id.toString())) {
								if (sb.length() > 0)
									sb.append(",");
								sb.append(text);
							}
							JSONObject j = new JSONObject();
							j.put("id", id);
							j.put(tn, text);
							a.put(j);
						}
						// valueDesp = sb.toString();
					}
				}
			}
			if (url != null && !url.isEmpty()) {
				JSONObject r = new JSONObject();
				r.put("url", url);
				r.put("textfield", textFieldName);
				json.put("requestParams", r);
			}
			json.put("type", type);
			if (buttonClick != null) {
				json.put("buttonClick", new NoQuoteString("function(){" + buttonClick + "}"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		writer.write(this.getScriptScope() + getId() + "=$input(" + json.toString() + ");");
		writer.write("</script>");
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
	}

	public String getNormalPrompt() {
		return normalPrompt;
	}

	public void setNormalPrompt(String normalPrompt) {
		this.normalPrompt = normalPrompt;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlaceHolder() {
		return placeHolder;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Boolean getMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(Boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public int getDepths() {
		return depths;
	}

	public void setDepths(int depths) {
		this.depths = depths;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTextFieldName() {
		return textFieldName;
	}

	public void setTextFieldName(String textFieldName) {
		this.textFieldName = textFieldName;
	}

	public Object getAutoComplete() {
		return autoComplete;
	}

	public void setAutoComplete(Object autoComplete) {
		this.autoComplete = autoComplete;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Object getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Object maxValue) {
		this.maxValue = maxValue;
	}

	public Object getMinValue() {
		return minValue;
	}

	public void setMinValue(Object minValue) {
		this.minValue = minValue;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getCheckboxes() {
		return checkboxes;
	}

	public void setCheckboxes(Boolean checkboxes) {
		this.checkboxes = checkboxes;
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public String getErrorPrompt() {
		return errorPrompt;
	}

	public void setErrorPrompt(String errorPrompt) {
		this.errorPrompt = errorPrompt;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getButtonClick() {
		return buttonClick;
	}

	public void setButtonClick(String buttonClick) {
		this.buttonClick = buttonClick;
	}

}
