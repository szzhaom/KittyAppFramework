package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class TextAreaTag extends HtmlTag {
	private static final long serialVersionUID = 1L;
	boolean readonly;
	boolean disabled;
	String type;
	Integer rows, cols;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getCols() {
		return cols;
	}

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	@Override
	protected void writeAttributes(JspWriter writer) throws IOException {
		super.writeAttributes(writer);
		if (disabled)
			writeAttribute(writer, "disabled", "disabled");
		if (readonly)
			writeAttribute(writer, "readonly", "readonly");
		writeAttribute(writer, "rows", rows);
		writeAttribute(writer, "cols", cols);
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
	}

	@Override
	String getTagName() {
		return "textarea";
	}
}
