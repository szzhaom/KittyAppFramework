package kitty.kaf.pools.jndi;

import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import kitty.kaf.KafUtil;
import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.logging.KafLogger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * JNDI连接工厂
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public class JndiConnectionFactory {
	static KafLogger logger = KafLogger.getLogger(JndiConnectionFactory.class);
	static ConcurrentHashMap<String, JndiConnecitonPoolListGroup> groups = new ConcurrentHashMap<String, JndiConnecitonPoolListGroup>();
	static {
		try {
			loadConfig();
		} catch (Throwable e) {
		}
	}

	public static void loadConfig() {
		try {
			NodeList list = KafUtil.getBasicConfigRoot().getElementsByTagName("jndi-group");
			for (int i = 0; i < list.getLength(); i++) {
				Element el = (Element) list.item(i);
				JndiConnecitonPoolListGroup cps = new JndiConnecitonPoolListGroup();
				groups.put(el.getAttribute("name"), cps);
				NodeList ls = el.getElementsByTagName("lookuper");
				if (ls.getLength() > 0)
					cps.lookuper = (Lookuper) Class.forName(((Element) ls.item(0)).getAttribute("class")).newInstance();
				else
					cps.lookuper = new Lookuper();
				int size = 30;
				if (el.hasAttribute("local-jndi-maxconnectionsize"))
					size = Integer.valueOf(el.getAttribute("local-jndi-maxconnectionsize"));
				cps.localPool = new JndiConnectionPool("local", null, cps.lookuper, cps.properties, size);
				ls = el.getElementsByTagName("properties");
				for (int j = 0; j < ls.getLength(); j++) {
					NodeList ls1 = ((Element) ls.item(j)).getElementsByTagName("property");
					for (int k = 0; k < ls1.getLength(); k++) {
						Element el1 = (Element) ls1.item(k);
						cps.properties.put(el1.getAttribute("name"), el1.getAttribute("value"));
					}
				}
				ls = el.getElementsByTagName("pools");
				for (int j = 0; j < ls.getLength(); j++) {
					Element el1 = (Element) ls.item(j);
					JndiConnectionPoolList pc = new JndiConnectionPoolList(el1.getAttribute("name"));
					cps.poolsMap.put(el1.getAttribute("name"), pc);
					NodeList ls1 = el1.getElementsByTagName("pool");
					for (int k = 0; k < ls1.getLength(); k++) {
						Element el2 = (Element) ls.item(k);
						size = 30;
						if (el2.hasAttribute("maxconnectionsize"))
							size = Integer.valueOf(el2.getAttribute("maxconnectionsize"));
						JndiConnectionPool cc = new JndiConnectionPool(el2.getAttribute("name"), el2.getNodeValue(),
								cps.lookuper, cps.properties, size);
						cc.url = el2.getNodeValue();
						pc.add(cc, 1);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static JndiConnection getConnection(String group, String name, Object caller) throws JndiException,
			ConnectException, InterruptedException {
		JndiConnecitonPoolListGroup g = groups.get(group);
		if (g != null) {
			JndiConnectionPoolList ls = g.poolsMap.get(name);
			if (ls == null)
				throw new JndiException();
			else
				return ls.getConnection(caller);
		} else
			throw new JndiException();
	}

	public static JndiConnection getConnection(String name, Object caller) throws JndiException, ConnectException,
			InterruptedException {
		JndiConnecitonPoolListGroup g = groups.get("default");
		if (g != null) {
			JndiConnectionPoolList ls = g.poolsMap.get(name);
			if (ls == null)
				throw new JndiException("JNDI[" + name + "]不存在。");
			else
				return ls.getConnection(caller);
		} else
			throw new JndiException();
	}

	public static JndiConnection getLocalConnection(String group, Object caller) throws JndiException,
			ConnectException, InterruptedException {
		JndiConnecitonPoolListGroup g = groups.get(group);
		if (g != null) {
			return g.localPool.getConnection(caller);
		} else
			throw new JndiException();
	}

	public static JndiConnection getLocalConnection(Object caller) throws JndiException, ConnectException,
			InterruptedException {
		JndiConnecitonPoolListGroup g = groups.get("default");
		if (g != null) {
			return g.localPool.getConnection(caller);
		} else
			throw new JndiException();
	}

	/**
	 * 查找一个jndi对象
	 * 
	 * @param jndiType
	 *            jndi资源类型
	 * @param name
	 *            会话Bean的Jndi名称
	 * @return 对象
	 * @throws InterruptedException
	 * @throws JndiException
	 * @throws NamingException
	 */
	public static <E> E lookup(String poolName, Object caller, int jndiType, String name, Class<E> clazz)
			throws ConnectException, JndiException, InterruptedException {
		JndiConnection con = getConnection(poolName, caller);
		try {
			return con.lookup(jndiType, name, clazz);
		} finally {
			con.close();
		}
	}

	/**
	 * 查找一个jndi对象
	 * 
	 * @param jndiType
	 *            jndi资源类型
	 * @param name
	 *            会话Bean的Jndi名称
	 * @return 对象
	 * @throws InterruptedException
	 * @throws JndiException
	 * @throws NamingException
	 */
	public static <E> E lookup(String groupName, String poolName, Object caller, int jndiType, String name,
			Class<E> clazz) throws ConnectException, JndiException, InterruptedException {
		JndiConnection con = getConnection(groupName, poolName, caller);
		try {
			return con.lookup(jndiType, name, clazz);
		} finally {
			con.close();
		}
	}
}
