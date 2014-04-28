package kitty.kaf;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kitty.kaf.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KafUtil {
	static Logger logger = Logger.getLogger(KafUtil.class);
	static public final int APP_SERVER_WEBLOGIC = 0;
	static public final int APP_SERVER_JBOSS = 1;
	static public final int APP_SERVER_WEBSPHERE = 2;
	static public final int APP_SERVER_UNKNOWN = 9999;
	static private int appServerType = -1;
	static ConcurrentHashMap<String, String> attributes = new ConcurrentHashMap<String, String>();
	static {
		try {
			Element root = getBasicConfigRoot();
			NodeList ls = root.getElementsByTagName("attributes");
			for (int i = 0; i < ls.getLength(); i++) {
				Element el = (Element) ls.item(i);
				NodeList ls1 = el.getElementsByTagName("attr");
				for (int j = 0; j < ls1.getLength(); j++) {
					el = (Element) ls1.item(j);
					attributes.put(el.getAttribute("name"), el.getAttribute("value"));
				}
			}
		} catch (Throwable e) {
			logger.error("load error:", e);
		}
	}

	public static void main(String[] args) {
		System.out
				.println(procAttribute("${basic_admin_root}+asdf${basic_admin_root}${basic_admin_root}_asdf${basic_admin_root}"));
	}

	public static String getAttribute(String name) {
		return attributes.get(name);
	}

	// 处理变量，把${xxx}替换成xxx代表的值
	public static String procAttribute(String text) {
		return procAttribute(text, attributes);
	}

	public static String procAttribute(String text, Map<String, String> varMap) {
		int from = text.indexOf("${");
		if (from < 0)
			return text;
		StringBuffer sb = new StringBuffer();
		int index = 0;
		while (from > -1) {
			int end = text.indexOf("}", from + 2);
			String var = text.substring(from + 2, end);
			String v = varMap.get(var.trim());
			sb.append(text.subSequence(index, from));
			if (v != null)
				sb.append(v);
			index = end + 1;
			from = text.indexOf("${", index);
		}
		sb.append(text.substring(index));
		return sb.toString();
	}

	public static String clearAttributeTag(String text) {
		int from = text.indexOf("${");
		if (from < 0)
			return text;
		StringBuffer sb = new StringBuffer();
		int index = 0;
		while (from > -1) {
			int end = text.indexOf("}", from + 2);
			sb.append(text.subSequence(index, from));
			index = end + 1;
			from = text.indexOf("${", index);
		}
		sb.append(text.substring(index));
		return sb.toString();
	}

	public static String clearFirstAttributeTag(String text) {
		int from = text.indexOf("${");
		if (from < 0)
			return text;
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int end = text.indexOf("}", from + 2);
		sb.append(text.subSequence(index, from));
		index = end + 1;
		sb.append(text.substring(index));
		return sb.toString();
	}

	public static ConcurrentHashMap<String, String> getAttributes() {
		return attributes;
	}

	synchronized static public int getAppServerType() {
		if (appServerType == -1) {
			if (System.getProperty("weblogic.home") != null || System.getProperty("weblogic.Name") != null)
				appServerType = APP_SERVER_WEBLOGIC;
			else if (System.getProperty("jboss.server.home.url") != null
					|| System.getProperty("jboss.server.base.dir") != null)
				appServerType = APP_SERVER_JBOSS;
			else if (System.getProperty("was.install.root") != null)
				appServerType = APP_SERVER_WEBSPHERE;
			else
				appServerType = APP_SERVER_UNKNOWN;
		}
		return appServerType;
	}

	static String configPath = null;
	static String home = null;

	synchronized static public String getHome() {
		if (home != null)
			return home;
		String path = System.getenv("APP_RUN_HOME");
		if (path != null && !path.isEmpty()) {
			home = path;
			logger.info("Environment variable (APP_RUN_HOME) is " + path);
		} else {
			System.err.println("【APP_RUN_HOME】未配置");
		}
		return home;
	}

	synchronized static public String getConfigPath() {
		if (configPath != null)
			return configPath;
		configPath = getHome() + "/config/";
		configPath = configPath.replace("\\", "/");
		configPath = configPath.replace("//", "/");
		logger.info("config path:[" + configPath + "]");
		return configPath;
	}

	static Element root;

	synchronized static public Element getBasicConfigRoot() throws ParserConfigurationException, SAXException,
			IOException {
		if (root == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(getConfigPath() + "basic-config.xml");
			root = doc.getDocumentElement();
		}
		return root;
	}
}
