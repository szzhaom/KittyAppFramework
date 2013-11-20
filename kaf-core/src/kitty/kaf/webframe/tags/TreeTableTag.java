package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import kitty.kaf.json.NoQuoteString;

public class TreeTableTag extends TreeTag {
	private static final long serialVersionUID = 1L;
	protected String headLineClass;
	protected String desp;
	protected String lineClass;
	protected String prefix;
	protected String url, deleteUrl;
	protected String columns;

	@Override
	protected void outputTreeScript(JspWriter writer) throws IOException {
		try {
			json.put("head_line_class", headLineClass);
			json.put("line_class", lineClass);
			json.put("desp", desp);
			json.put("prefix", prefix);
			json.put("url", url);
			json.put("delete_url", deleteUrl);
			json.put("columns", new NoQuoteString(columns));
		} catch (Throwable e) {
			throw new IOException(e);
		}
		writer.write("<script>" + getScriptScope() + getId() + "=new TreeTable(" + json.toString() + ");</script>");
		json = null;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getHeadLineClass() {
		return headLineClass;
	}

	public void setHeadLineClass(String headLineClass) {
		this.headLineClass = headLineClass;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getLineClass() {
		return lineClass;
	}

	public void setLineClass(String lineClass) {
		this.lineClass = lineClass;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDeleteUrl() {
		return deleteUrl;
	}

	public void setDeleteUrl(String deleteUrl) {
		this.deleteUrl = deleteUrl;
	}

}
