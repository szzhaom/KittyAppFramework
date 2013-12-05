package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

/**
 * 输入img标签
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class ImageTag extends HtmlTag {
	private static final long serialVersionUID = 1L;
	private String src;
	private String align;
	private String border;
	private String width;
	private String height;
	private String href;
	private String target;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

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
		writeUrlAttribute(writer, "src", getSrc());
		writeAttribute(writer, "align", getAlign());
		writeAttribute(writer, "width", getWidth());
		writeAttribute(writer, "height", getHeight());
		writeAttribute(writer, "border", getBorder());
	}

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		if (getHref() != null) {
			writeText(writer, "<a");
			writeUrlAttribute(writer, "href", getHref());
			writeAttribute(writer, "target", getTarget());
			writeText(writer, ">");
		}
		super.doStartTag(writer);
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		super.doEndTag(writer);
		if (getHref() != null)
			writeText(writer, "</a>");
	}

	@Override
	String getTagName() {
		return "img";
	}

}
