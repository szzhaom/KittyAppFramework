package kitty.kaf.dao.tools.cg.jsp;

import org.w3c.dom.Element;

public class JspConfig {
	/**
	 * 模板名称
	 */
	String templateName;
	TableJspConfig config;

	public JspConfig(TableJspConfig config, Element el) {
		templateName = el.hasAttribute("template") ? el.getAttribute("template") : null;
		this.config = config;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
