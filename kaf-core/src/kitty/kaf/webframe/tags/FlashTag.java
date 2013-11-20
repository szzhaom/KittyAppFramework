package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

/**
 * flash标签
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class FlashTag extends HtmlTag {
	private static final long serialVersionUID = 1L;
	private String onFSCommand;
	private String height;
	private String width;
	private String src;
	private String quality;
	private String bgcolor;
	private String wmode = "transparent";
	private String allowFullScreen;
	private String allowscriptaccess;
	private String menu;
	private String play;
	private String loop;
	private String scale;

	@Override
	public void release() {
		onFSCommand = null;
		height = null;
		width = null;
		styleClass = null;
		src = null;
		quality = null;
		bgcolor = null;
		wmode = null;
		allowFullScreen = null;
		allowscriptaccess = null;
		menu = null;
		play = null;
		loop = null;
		scale = null;
		super.release();
	}

	public String getOnFSCommand() {
		return onFSCommand;
	}

	public void setOnFSCommand(String onFSCommand) {
		this.onFSCommand = onFSCommand;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public String getWmode() {
		return wmode;
	}

	public void setWmode(String wmode) {
		this.wmode = wmode;
	}

	public String getAllowFullScreen() {
		return allowFullScreen;
	}

	public void setAllowFullScreen(String allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}

	public String getAllowscriptaccess() {
		return allowscriptaccess;
	}

	public void setAllowscriptaccess(String allowscriptaccess) {
		this.allowscriptaccess = allowscriptaccess;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getPlay() {
		return play;
	}

	public void setPlay(String play) {
		this.play = play;
	}

	public String getLoop() {
		return loop;
	}

	public void setLoop(String loop) {
		this.loop = loop;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	@Override
	protected void writeValue(JspWriter writer) throws IOException {
	}

	@Override
	protected void writeAttributes(JspWriter writer) throws IOException {
		super.writeAttributes(writer);
		writeAttribute(writer, "codebase",
				"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=10");
		writeAttribute(writer, "classid", "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000");
		writeAttribute(writer, "id", getId());
		writeAttribute(writer, "type", "application/x-shockwave-flash");
		writeAttribute(writer, "width", getWidth());
		writeAttribute(writer, "height", getHeight());
		writeAttribute(writer, "onFSCommand", getOnFSCommand());
		writeAttribute(writer, "styleClass", getStyleClass());
		writer.write("><param value='");
		writeUrl(writer, getSrc());
		writer.write("' name='movie'></param>");
		if (getQuality() != null)
			writer.write("<param value='" + getQuality() + "' name='quality'></param>");
		if (getBgcolor() != null)
			writer.write("<param value='" + getBgcolor() + "' name='bgcolor'></param>");
		if (getWmode() != null)
			writer.write("<param value='" + getWmode() + "' name='wmode'></param>");
		if (getScale() != null)
			writer.write("<param value='" + getScale() + "' name='scale'></param>");
		if (getLoop() != null)
			writer.write("<param value='" + getLoop() + "' name='loop'></param>");
		if (getPlay() != null)
			writer.write("<param value='" + getPlay() + "' name='play'></param>");
		if (getMenu() != null)
			writer.write("<param value='" + getMenu() + "' name='menu'></param>");
		if (getAllowscriptaccess() != null)
			writer.write("<param value='" + getAllowscriptaccess() + "' name='allowscriptaccess'></param>");
		if (getAllowFullScreen() != null)
			writer.write("<param value='" + getAllowFullScreen() + "' name='allowFullScreen'></param>");
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		writeText(writer, "<embed");
		writeAttribute(writer, "id", getId());
		writeAttribute(writer, "type", "application/x-shockwave-flash");
		writeAttribute(writer, "name", getId());
		writeAttribute(writer, "onFSCommand", getOnFSCommand());
		writeAttribute(writer, "scale", getScale());
		writeAttribute(writer, "loop", getLoop());
		writeAttribute(writer, "play", getPlay());
		writeAttribute(writer, "menu", getMenu());
		writeAttribute(writer, "allowscriptaccess", getAllowscriptaccess());
		writeAttribute(writer, "allowFullScreen", getAllowFullScreen());
		writeAttribute(writer, "wmode", getWmode());
		writeAttribute(writer, "bgcolor", getBgcolor());
		writeAttribute(writer, "quality", getQuality());
		writeUrlAttribute(writer, "src", getSrc());
		writeAttribute(writer, "styleClass", getStyleClass());
		writeAttribute(writer, "width", getWidth());
		writeAttribute(writer, "height", getHeight());
		writeText(writer, "</embed>");
		super.doEndTag(writer);
	}

	@Override
	String getTagName() {
		return "object";
	}

}
