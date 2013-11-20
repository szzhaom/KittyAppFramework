package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

/**
 * 输出简单的A标签
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class ATag extends HtmlTag {
	private static final long serialVersionUID = 1L;
	private String href;
	private String target;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@Override
	protected void writeAttributes(JspWriter writer) throws IOException {
		super.writeAttributes(writer);
		writeUrlAttribute(writer, "href", getHref());
		writeAttribute(writer, "target", getTarget());
	}

	@Override
	String getTagName() {
		return "a";
	}

}
