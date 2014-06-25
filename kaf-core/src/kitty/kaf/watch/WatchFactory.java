package kitty.kaf.watch;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.GafUtil;
import kitty.kaf.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 监视工厂
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class WatchFactory {
	static Logger logger = Logger.getLogger(WatchFactory.class);
	static ConcurrentHashMap<String, Watcher> watchers = new ConcurrentHashMap<String, Watcher>();
	static {
		try {
			loadConfig(GafUtil.getConfigPath() + "basic-config.xml");
		} catch (Throwable e) {
		}
	}

	static void loadConfig(String configFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(configFile);
			NodeList list = doc.getElementsByTagName("watcher");
			for (int i = 0; i < list.getLength(); i++) {
				Element el = (Element) list.item(i);
				String name = el.getAttribute("name");
				int interval = Integer.valueOf(el.getAttribute("interval"));
				Watcher c = new Watcher(name, interval);
				watchers.put(name, c);
				new Thread(c, "watchthread[" + name + "]").start();
			}
		} catch (Throwable e) {
			logger.error("init watchers failure:", e);
		}
	}

	/**
	 * 根据名字获取守护者
	 * 
	 * @param name
	 *            守护者名称
	 * @return 守护者对象
	 */
	public static Watcher getWatcher(String name) {
		return watchers.get(name);
	}
}
