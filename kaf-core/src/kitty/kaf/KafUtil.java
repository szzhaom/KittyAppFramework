package kitty.kaf;

import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kitty.kaf.logging.KafLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class KafUtil {
	static KafLogger logger = KafLogger.getLogger(KafUtil.class);
	static public final int APP_SERVER_WEBLOGIC = 0;
	static public final int APP_SERVER_JBOSS = 1;
	static public final int APP_SERVER_WEBSPHERE = 2;
	static public final int APP_SERVER_UNKNOWN = 9999;
	static private int appServerType = -1;

	public static void main(String[] args) {
		System.out.println(new Date());
	}

	synchronized static public int getAppServerType() {
		if (appServerType == -1) {
			if (System.getProperty("weblogic.home") != null
					|| System.getProperty("weblogic.Name") != null)
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
	static String RSHome = null;

	synchronized static public String getRSHome() {
		if (RSHome != null)
			return RSHome;
		String path = System.getenv("KAF_HOME");
		if (path != null && !path.isEmpty()) {
			RSHome = path;
			logger.info("Environment variable (KAF_HOME) is " + path);
		} else {
			System.err.println("【KAF_HOME】未配置");
		}
		return RSHome;
	}

	synchronized static public String getConfigPath() {
		if (configPath != null)
			return configPath;
		configPath = getRSHome() + "/config/";
		configPath = configPath.replace("\\", "/");
		configPath = configPath.replace("//", "/");
		logger.info("config path:[" + configPath + "]");
		return configPath;
	}

	static Element root;

	synchronized static public Element getBasicConfigRoot()
			throws ParserConfigurationException, SAXException, IOException {
		if (root == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(getConfigPath() + "basic-config.xml");
			root = doc.getDocumentElement();
		}
		return root;
	}
}
