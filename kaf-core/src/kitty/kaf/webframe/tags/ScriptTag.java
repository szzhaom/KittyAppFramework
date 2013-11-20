package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

/**
 * 输入出Script标签
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class ScriptTag extends BasicTag {
	private static final long serialVersionUID = 1L;
	String src;
	String scriptText;
	String scriptScope;
	boolean outputContextPath;

	public String getScriptScope() {
		return scriptScope;
	}

	public void setScriptScope(String scriptScope) {
		this.scriptScope = scriptScope;
		if (scriptScope != null)
			pageContext.setAttribute("JSSCOPE", scriptScope);
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getScriptText() {
		return scriptText;
	}

	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}

	public boolean isOutputContextPath() {
		return outputContextPath;
	}

	public void setOutputContextPath(boolean outputContextPath) {
		this.outputContextPath = outputContextPath;
	}

	@Override
	public void doStartTag(JspWriter writer) throws IOException {
		if (outputContextPath) {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			writeText(writer, "<script type='text/javascript'>var contextPath='" + request.getContextPath()
					+ "';</script>");
		}
		writeText(writer, "<script type='text/javascript' ");
		writeUrlAttribute(writer, "src", getSrc());
		writeText(writer, ">");
		if (getScriptScope() != null) {
			writeText(writer, "var " + getScriptScope() + "={};");
		}
		if (scriptText != null)
			writeText(writer, scriptText);
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		writeText(writer, "</script>");
		src = null;
	}

}
