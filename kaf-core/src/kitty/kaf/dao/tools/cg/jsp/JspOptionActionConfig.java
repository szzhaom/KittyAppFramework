package kitty.kaf.dao.tools.cg.jsp;

import org.w3c.dom.Element;

public class JspOptionActionConfig {
	String url, saveUrl, title, desp;
	TableJspConfig config;
	String actionName;

	public JspOptionActionConfig(TableJspConfig config, Element el) {
		this.config = config;
		this.url = el.getAttribute("url");
		this.saveUrl = el.hasAttribute("save_url") ? el
				.getAttribute("save_url") : "";
		this.title = el.getAttribute("title");
		this.desp = el.getAttribute("desp");
		this.actionName = el.hasAttribute("action") ? el.getAttribute("action")
				: "default";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSaveUrl() {
		return saveUrl;
	}

	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public TableJspConfig getConfig() {
		return config;
	}

	public void setConfig(TableJspConfig config) {
		this.config = config;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
}
