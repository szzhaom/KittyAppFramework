package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class OutputTag extends BasicTag {
	private static final long serialVersionUID = 1L;
	String tag;
	String attributes;
	String body;

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		writer.write("<" + tag);
		if (attributes != null) {
			writer.write(" " + attributes.trim() + ">");
		} else
			writer.write(">");
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		writer.write(body);
		writer.write("</" + tag + ">");
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
