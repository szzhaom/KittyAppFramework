package kitty.kaf.webframe.tags;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import kitty.kaf.helper.StringHelper;

/**
 * 输出Form标签
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class FormTag extends HtmlTag {
	private static final long serialVersionUID = 1L;

	@Override
	public String getId() {
		String id = super.getId();
		if (id == null) {
			id = "form" + new Random(100000000).nextLong();
			setId(id);
		}
		return id;
	}

	private String method = "post";
	private String enctype = "application/x-www-form-urlencoded";
	private String target;
	private String onsubmit;
	private String onreset;
	private String name;
	private String action;
	private boolean ajaxform;

	@Override
	public void release() {
		method = null;
		enctype = null;
		target = null;
		onsubmit = null;
		onreset = null;
		name = null;
		action = null;
		super.release();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getEnctype() {
		return enctype;
	}

	public void setEnctype(String enctype) {
		this.enctype = enctype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getOnsubmit() {
		return onsubmit;
	}

	public void setOnsubmit(String onsubmit) {
		this.onsubmit = onsubmit;
	}

	public String getOnreset() {
		return onreset;
	}

	public void setOnreset(String onreset) {
		this.onreset = onreset;
	}

	public boolean isAjaxform() {
		return ajaxform;
	}

	public void setAjaxform(boolean ajaxForm) {
		this.ajaxform = ajaxForm;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	protected void writeAttributes(JspWriter writer) throws IOException {
		super.writeAttributes(writer);
		if (!isAjaxform()) {
			String url = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
			int index = url.indexOf(".jsp");
			if (index > -1)
				url = url.substring(0, index) + ".go";
			if (action == null || action.trim().isEmpty())
				writeAttribute(writer, "action", url);
			else
				writeUrlAttribute(writer, "action", action);
		}
		writeAttribute(writer, "method", getMethod());
		writeAttribute(writer, "target", getTarget());
		writeAttribute(writer, "enctype", getEnctype());
		writeAttribute(writer, "onsubmit", getOnsubmit());
		writeAttribute(writer, "onreset", getOnreset());
		writeText(writer, ">");
		if (!isAjaxform()) {
			String url = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
			writeText(writer,
					"<input type='hidden' name='RSfacesfromid' value='" + StringHelper.bytesToHex(url.getBytes())
							+ "'/>");
		}
	}

	@Override
	protected void writeValue(JspWriter writer) throws IOException {
	}

	@Override
	String getTagName() {
		return "form";
	}
}
