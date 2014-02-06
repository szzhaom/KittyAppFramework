package kitty.kaf.helper;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import kitty.kaf.KafUtil;
import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.pools.jndi.JndiConnectionFactory;
import kitty.kaf.pools.jndi.JndiException;
import kitty.kaf.pools.jndi.Lookuper;
import kitty.kaf.serial.SerialFactoryRemote;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 序列号工厂
 * 
 * @author 赵明
 * 
 */
public class SerialFactoryHelper {
	static String serialJndiPool;
	static String serialEjbName;
	static {
		try {
			init();
		} catch (Throwable e) {
		}
	}

	static void init() throws ParserConfigurationException, SAXException, IOException {
		Element root = KafUtil.getBasicConfigRoot();
		NodeList ls = root.getElementsByTagName("serial-config");
		if (ls.getLength() > 0) {
			Element el = (Element) ls.item(0);
			serialJndiPool = el.getAttribute("jndiPool");
			serialEjbName = el.getAttribute("serialBeanName");
		}
	}

	public SerialFactoryHelper() {
	}

	static public String getNextSerial(String key) throws ConnectException, JndiException, InterruptedException {
		SerialFactoryRemote bean = JndiConnectionFactory.lookup(serialJndiPool, null, Lookuper.JNDI_TYPE_EJB,
				serialEjbName, SerialFactoryRemote.class);
		return bean.getNextSerial(key);
	}
}
