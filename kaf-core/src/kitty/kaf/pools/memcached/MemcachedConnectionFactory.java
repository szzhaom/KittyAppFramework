package kitty.kaf.pools.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.KafUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Memcached连接工厂
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */

public class MemcachedConnectionFactory {
	/**
	 * 连接池映射
	 */
	static ConcurrentHashMap<String, MemcachedConnectionPool<MemcachedConnection>> pools = new ConcurrentHashMap<String, MemcachedConnectionPool<MemcachedConnection>>();
	static ConcurrentHashMap<String, MemcachedConnectionPoolList> groups = new ConcurrentHashMap<String, MemcachedConnectionPoolList>();
	static {
		try {
			loadConfig(KafUtil.getConfigPath() + "basic-config.xml");
		} catch (Throwable e) {
		}
	}

	public static void loadConfig(String configFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(configFile);
			NodeList list = doc.getElementsByTagName("memcached");
			Element root = (Element) list.item(0);
			list = root.getElementsByTagName("pool");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				int dataTimeout = 0;
				// int soTimeout = 10 * 1000;
				int connectTimeout = 10 * 1000;
				int maxConnections = 30;
				int minConnections = 0;
				if (node.hasAttribute("timeout")) {
					dataTimeout = Integer.valueOf(node.getAttribute("timeout")) * 1000;
				}
				// if (node.hasAttribute("sotimeout"))
				// soTimeout = Integer.valueOf(node.getAttribute("sotimeout")) *
				// 1000;
				if (node.hasAttribute("connecttimeout"))
					connectTimeout = Integer.valueOf(node
							.getAttribute("connecttimeout")) * 1000;
				if (node.hasAttribute("maxconnections"))
					maxConnections = Integer.valueOf(node
							.getAttribute("maxconnections"));
				if (node.hasAttribute("minconnections"))
					minConnections = Integer.valueOf(node
							.getAttribute("minconnections"));
				MemcachedConnectionPool<MemcachedConnection> pool = new MemcachedConnectionPool<MemcachedConnection>(
						node.getAttribute("name"), minConnections,
						maxConnections, connectTimeout);
				pool.setDataTimeout(dataTimeout);
				pool.setAddress(new InetSocketAddress(
						node.getAttribute("host"), Integer.valueOf(node
								.getAttribute("port"))));
				pools.put(pool.getName(), pool);
			}
			list = root.getElementsByTagName("group");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				String name = node.getAttribute("name");
				NodeList ls = node.getElementsByTagName("poolref");
				if (name != null && !name.trim().isEmpty()
						&& ls.getLength() > 0) {
					MemcachedConnectionPoolList cps = new MemcachedConnectionPoolList(
							name);
					groups.put(name, cps);
					boolean useConsistentHash = false;
					if (node.hasAttribute("useconsistenthash"))
						useConsistentHash = Boolean.valueOf(node
								.getAttribute("useconsistenthash"));
					cps.setUseConsistentHash(useConsistentHash);
					NodeList ls1 = node.getElementsByTagName("param");
					for (int j = 0; j < ls1.getLength(); j++) {
						node = (Element) ls1.item(j);
						cps.configMap.put(node.getAttribute("name"),
								node.getAttribute("value"));
					}
					for (int j = 0; j < ls.getLength(); j++) {
						Element el = (Element) ls.item(j);
						int weights = 1;
						if (el.hasAttribute("weights"))
							weights = Integer.valueOf(el
									.getAttribute("weights"));
						MemcachedConnectionPool<MemcachedConnection> pool = pools
								.get(el.getAttribute("poolname"));
						cps.add(pool, weights);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除指定配置的服务器上所有缓存数据
	 * 
	 * @param name
	 *            配置名称
	 * @throws InterruptedException
	 * @throws IOException
	 */
	static public void flushAll(Object caller, String name)
			throws InterruptedException, IOException {
		MemcachedConnectionPoolList pools = groups.get(name);
		if (pools != null)
			pools.flushAll(caller);
	}

	/**
	 * 删除所有的服务器上全部缓存数据
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	static public void flushAll(Object caller) throws InterruptedException {
		Enumeration<MemcachedConnectionPoolList> en = groups.elements();
		while (en.hasMoreElements()) {
			try {
				en.nextElement().flushAll(caller);
			} catch (IOException e) {
			}
		}
	}

	static public MemcachedClient getClientInstance(Object caller, String name)
			throws MemcachedException {
		MemcachedConnectionPoolList pools = groups.get(name);
		if (pools == null)
			return null;
		try {
			return new MemcachedClient(caller, pools);
		} catch (Exception e) {
			throw new MemcachedException(e);
		}
	}

}
