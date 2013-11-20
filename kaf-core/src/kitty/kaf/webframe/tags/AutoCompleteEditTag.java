package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import kitty.kaf.helper.ObjectHelper;
import kitty.kaf.json.JSONArray;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.json.NoQuoteString;

public class AutoCompleteEditTag extends BasicTag {
	private static final long serialVersionUID = 1L;
	private String prompt;
	private String menuClass;
	private String text;
	private String url;
	protected JSONObject json;
	private String onSelect;
	private String allUrl;
	private String scriptScope;
	private Object value;

	@Override
	protected String getScriptScope() {
		if (scriptScope != null)
			return scriptScope + ".";
		return super.getScriptScope();
	}

	public void setScriptScope(String scriptScope) {
		this.scriptScope = scriptScope;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getAllUrl() {
		return allUrl;
	}

	public void setAllUrl(String allUrl) {
		this.allUrl = allUrl;
	}

	protected void outputHiddenInput(JspWriter writer) throws IOException {
		writer.write("<input type='hidden' id='" + this.getId() + "' name='" + this.getId() + "' value='"
				+ (getValue() == null ? "" : getValue()) + "'");
		writer.write("/>");
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getMenuClass() {
		return menuClass;
	}

	public void setMenuClass(String menuClass) {
		this.menuClass = menuClass;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(String onSelect) {
		this.onSelect = onSelect;
	}

	protected void outputEditScript(JspWriter writer) throws IOException {
		writer.write("<script>" + getScriptScope() + getId() + "=new AutoCompleteEdit(" + json.toString()
				+ ");</script>");
	}

	protected void outputText(JspWriter writer) throws IOException {
		writeAttribute(writer, "value", text);
	}

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		try {
			json = new JSONObject();
			json.put("prompt", ObjectHelper.checkNull(prompt, "输入简拼、全拼和中文查询"));
			json.put("class", ObjectHelper.checkNull(menuClass, "list popupwin"));
			json.put("input", getId() + "_text");
			json.put("value_input", getId());
			json.put("id", getId());
			json.put("url", url);
			json.put("allurl", allUrl);
			JSONObject events = new JSONObject();
			json.put("events", events);
			if (onSelect != null)
				events.put("select", new NoQuoteString("function(s,o){" + onSelect + "}"));
			JSONArray a = new JSONArray();
			json.put("items", a);
		} catch (JSONException e) {
			throw new IOException(e);
		}
		outputHiddenInput(writer);
		writeText(writer, "<input id='" + getId() + "_text' ");
		writeAttribute(writer, "validate_input", getId());
		outputText(writer);
		writeText(writer, "/>");
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		outputEditScript(writer);
		json = null;
		text = "";
	}

}
