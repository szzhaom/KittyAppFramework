package kitty.testapp.inf.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import kitty.kaf.json.JSONObject;
import kitty.kaf.json.NoQuoteString;
import kitty.kaf.webframe.tags.BasicTag;

public class ButtonTag extends BasicTag {
	private static final long serialVersionUID = 1L;
	private String style, styleClass;
	private String onClick;
	private boolean disabled;
	private boolean checked;
	private int group;
	private String buttonType;
	private String scriptScope;
	private String value;

	@Override
	protected String getScriptScope() {
		if (scriptScope != null)
			return scriptScope + ".";
		return super.getScriptScope();
	}

	public void setScriptScope(String scriptScope) {
		this.scriptScope = scriptScope;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public String getButtonType() {
		return buttonType;
	}

	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		writeText(writer, "<div ");
		writeAttribute(writer, "href", "javascript:void(0);");
		writeAttribute(writer, "id", getId());
		writeAttribute(writer, "style", getStyle());
		writeText(writer, ">");
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		writeText(writer, "</div>");
		try {
			JSONObject json = new JSONObject();
			json.put("panel", getId());
			JSONObject json1 = new JSONObject();
			json1.put("disabled", disabled);
			json1.put("check", checked);
			json.put("status", json1);
			json.put("type", buttonType == null ? "button" : buttonType);
			json1 = new JSONObject();
			json1.put("text", value);
			json.put("labelParams", json1);
			String c = styleClass == null ? "" : styleClass.trim();
			int index = c.lastIndexOf(" ");
			if (index > 0)
				c = c.substring(index);
			json.put("classPrefix", c.trim());
			json1 = new JSONObject();
			json1.put("class", styleClass == null ? "" : styleClass.trim());
			json.put("createParams", json1);
			JSONObject events = new JSONObject();
			json.put("events", events);
			if (onClick != null) {
				events.put("click", new NoQuoteString("function(s){" + "" + onClick + "}"));
			}
			writer.write("<script>" + getScriptScope() + getId() + "=new UIButton(" + json.toString());
			writer.write(")</script>");
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

}
