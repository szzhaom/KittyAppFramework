package kitty.kaf.dao.tools;

import org.w3c.dom.Element;

public class TradeConfig {
	String executorName;
	String group;
	String queryDefaultCmd;

	public TradeConfig(Element el) {
		group = el.getAttribute("group");
		executorName = el.getAttribute("executor-name");
		queryDefaultCmd = el.hasAttribute("query_default_cmd") ? el.getAttribute("query_default_cmd") : "default";
	}

	public TradeConfig() {
		super();
	}

	public TradeConfig(String group, String executorName) {
		super();
		this.group = group;
		this.executorName = executorName;
	}

	public String getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getQueryDefaultCmd() {
		return queryDefaultCmd;
	}

	public void setQueryDefaultCmd(String queryDefaultCmd) {
		this.queryDefaultCmd = queryDefaultCmd;
	}

}
