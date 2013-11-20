package kitty.kaf.dao.tools.cg.jsp;

import org.w3c.dom.Element;

public class JspTemplateConfig {
	/**
	 * 模板名称
	 */
	String templateName;
	TableJspConfig config;

	public JspTemplateConfig(TableJspConfig config, Element el) {
		templateName = el.getAttribute("template");
		this.config = config;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
