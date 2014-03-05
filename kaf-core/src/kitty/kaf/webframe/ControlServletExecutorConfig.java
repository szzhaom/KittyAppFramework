package kitty.kaf.webframe;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.KafUtil;
import kitty.kaf.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ControlServletExecutorConfig {

	public class ToRule {

		boolean redirect;
		String url;

		public ToRule(boolean redirect, String url) {
			super();
			this.redirect = redirect;
			this.url = url;
		}
	}

	public class NavigateRule {
		public boolean saveQueryStringToAttribute;
		public String from;
		public String action;
		public String baseDir;
		public String firstBaseDir;
		public String actionClass;
		public boolean createActionAlways;
		public boolean needLogin;
		public ConcurrentHashMap<String, ToRule> toRules = new ConcurrentHashMap<String, ToRule>();
	}

	static Logger logger = Logger.getLogger(ControlServletExecutorConfig.class);
	ConcurrentHashMap<String, NavigateRule> rules = new ConcurrentHashMap<String, NavigateRule>();
	private String webRootPath;

	public ControlServletExecutorConfig(String webRootPath, String configFile) {
		this.webRootPath = webRootPath;
		load(configFile);
	}

	public void clear() {
		rules.clear();
	}

	public String getWebRootPath() {
		return webRootPath;
	}

	public boolean urlFileExists(String url) {
		File file = new File(webRootPath + url);
		return file.exists();
	}

	private void loadNavigateRules(Map<String, NavigateRule> rules, Document doc) {
		NodeList list = doc.getElementsByTagName("navigate-rule");
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			String from = e.hasAttribute("from") ? e.getAttribute("from")
					: null;
			String action = e.hasAttribute("action") ? e.getAttribute("action")
					: null;
			String actionClass = e.hasAttribute("actionClass") ? e
					.getAttribute("actionClass") : null;
			if (!(from == null || from.trim().isEmpty())) {
				NavigateRule rule = rules.get(from);
				if (rule == null) {
					rule = new NavigateRule();
					rule.from = from;
					rules.put(from, rule);
					if (e.hasAttribute("basedir")) {
						rule.baseDir = e.getAttribute("basedir");
					} else
						rule.baseDir = "";
					rule.firstBaseDir = rule.baseDir;
				} else {
					if (e.hasAttribute("basedir")) {
						rule.baseDir = e.getAttribute("basedir");
					}
				}
				if (action != null)
					rule.action = action;
				if (actionClass != null)
					rule.actionClass = actionClass;
				if (e.hasAttribute("createActionAlways"))
					rule.createActionAlways = "true".equals(e
							.getAttribute("createActionAlways"));
				if (e.hasAttribute("needLogin"))
					rule.needLogin = "true".equals(e.getAttribute("needLogin"));
				if (e.hasAttribute("saveQueryStringToAttribute"))
					rule.saveQueryStringToAttribute = "true".equals(e
							.getAttribute("saveQueryStringToAttribute"));
				NodeList ns = e.getElementsByTagName("to-rule");
				for (int j = 0; j < ns.getLength(); j++) {
					e = (Element) ns.item(j);
					String result = e.getAttribute("result");
					String to = e.getAttribute("to");
					if (!(result == null || result.trim().isEmpty()
							|| to == null || to.trim().isEmpty())) {
						String redirect = e.getAttribute("redirect");
						rule.toRules.put(result,
								new ToRule("true".equals(redirect), to));
					}
				}
			}
		}
	}

	public void load(String configFile) {
		try {
			Map<String, NavigateRule> tempRules = new HashMap<String, NavigateRule>();
			String path = KafUtil.getConfigPath();
			logger.debug("config file:[" + path + configFile + "]");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(path + configFile);

			loadNavigateRules(tempRules, doc);
			rules.clear();
			for (String key : tempRules.keySet()) {
				NavigateRule rule = tempRules.get(key);
				if (!rule.baseDir.isEmpty()) {
					Enumeration<ToRule> en = rule.toRules.elements();
					while (en.hasMoreElements()) {
						ToRule r = en.nextElement();
						r.url = rule.baseDir + r.url;
					}
				}
				if (rule.action != null && rule.action.trim().isEmpty())
					rule.action = null;
				if (rule.actionClass != null
						&& rule.actionClass.trim().isEmpty())
					rule.actionClass = null;
				rules.put(rule.baseDir + key, rule);
				rule.from = rule.baseDir + rule.from;
				if (!rule.baseDir.equals(rule.firstBaseDir))
					rules.put(rule.firstBaseDir + key, rule);
			}
			tempRules.clear();
			tempRules = null;
		} catch (Throwable e) {
			logger.error("Load config error:", e);
		}
	}

}
