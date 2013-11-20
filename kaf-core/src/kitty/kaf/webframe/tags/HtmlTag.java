package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

abstract public class HtmlTag extends BasicTag {
	private static final long serialVersionUID = 1L;
	String style, styleClass;
	Object value;

	abstract String getTagName();

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

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		writeText(writer, "<" + getTagName());
		writeAttributes(writer);
		writeText(writer, ">");
		writeValue(writer);
	}

	protected void writeValue(JspWriter writer) throws IOException {
		writeText(writer, getValue());
	}

	protected void writeAttributes(JspWriter writer) throws IOException {
		writeId(writer);
		writeName(writer);
		writeStyle(writer);
		writeStyleClass(writer);
	}

	protected void writeStyle(JspWriter writer) throws IOException {
		writeAttribute(writer, "style", getStyle());
	}

	protected void writeStyleClass(JspWriter writer) throws IOException {
		writeAttribute(writer, "class", getStyleClass());
	}

	protected void writeId(JspWriter writer) throws IOException {
		writeAttribute(writer, "id", getId());
	}

	protected void writeName(JspWriter writer) throws IOException {
		writeAttribute(writer, "name", getId());
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		writeText(writer, "</" + getTagName() + ">");
	}
}
