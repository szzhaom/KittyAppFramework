package kitty.kaf.language;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import kitty.kaf.GafUtil;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.logging.Logger;

/**
 * 语言助手类，用于国际化支持
 * <p>
 * 通过配置文件读取不同国家的语言
 * </p>
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class LanguageHelper {
	static final Logger logger = Logger.getLogger(LanguageHelper.class);
	static ConcurrentHashMap<String, Language> languageMap = new ConcurrentHashMap<String, Language>();
	static CopyOnWriteArrayList<Language> languageList = new CopyOnWriteArrayList<Language>();
	static String defLanguageConfig;
	static {
		loadConfig();
	}

	static void loadConfig() {
		try {
			NodeList ls = GafUtil.getBasicConfigRoot().getElementsByTagName("langconfig");
			defLanguageConfig = null;
			for (int i = 0; i < ls.getLength(); i++) {
				Element el = (Element) ls.item(i);
				if (defLanguageConfig == null)
					defLanguageConfig = el.getAttribute("default");
				NodeList ls1 = el.getElementsByTagName("language");
				for (int j = 0; j < ls1.getLength(); j++) {
					el = (Element) ls1.item(j);
					Language language = new Language();
					languageMap.put(el.getAttribute("name"), language);
					languageList.add(language);
					language.setDesp(el.getAttribute("desp"));
					language.setFielName(el.getAttribute("file"));
					language.setName(el.getAttribute("name"));
					// Properties prop = new Properties();
					FileInputStream stream = new FileInputStream(new File(GafUtil.getHome() + "/language/"
							+ language.getFielName()));
					DataReadStream rs = new DataReadStream(stream, 3000);
					while (stream.available() > 0) {
						String ln = new String(rs.readln("\n".getBytes(), true), "utf-8").trim();
						int index = ln.indexOf("=");
						if (index > 0) {
							String name = ln.substring(0, index).trim();
							String value = ln.substring(index + 1).trim();
							if (!name.isEmpty()) {
								language.stringsMap.put(name, value);
							}
						}
					}
					stream.close();
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	static public Language getDefault() {
		return languageMap.get(defLanguageConfig);
	}

	static public Language get(String name) {
		return languageMap.get(name);
	}

	public static void main(String[] args) {
		Enumeration<String> keys = languageMap.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			logger.debug("--------------------[" + key + "]-------------------");
			Language l = languageMap.get(key);
			Enumeration<String> keys1 = l.stringsMap.keys();
			while (keys1.hasMoreElements()) {
				key = keys1.nextElement();
				logger.debug(key + "=" + l.stringsMap.get(key));
			}
		}
	}

	public static List<Language> getLanguageList() {
		return languageList;
	}
}
