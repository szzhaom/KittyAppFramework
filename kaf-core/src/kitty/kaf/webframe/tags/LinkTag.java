package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class LinkTag extends BasicTag {
	private static final long serialVersionUID = 1L;
	String href;

	@Override
	public void release() {
		href = null;
		super.release();
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		writeText(writer, "<link type='text/css' rel='stylesheet'");
		writeUrlAttribute(writer, "href", getHref());
		writeText(writer, ">");
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		writeText(writer, "</link>");
	}
}
