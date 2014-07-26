package kitty.kaf.cache.clients;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import kitty.kaf.GafUtil;
import kitty.kaf.cache.ICacheClient;
import kitty.kaf.pools.ConnectionPool;
import kitty.kaf.pools.HashedConnectionPoolList;

/**
 * 缓存连接池工厂。本类不能直接使用，需要继承并重载newConnectionPool才能使用。<br/>
 * 本类是一个基于统一哈希算法实现的一个基于客户端分布式的缓存连接池工厂，标准化了配置文件的读取方式，所有基于本类的继承类，均采用同一种配置方式，
 * 只是配置结点不同而已
 * 
 * @author 赵明
 * @version 1.0
 * @param <C>
 */
public class CacheConnectionPoolFactory {
	/**
	 * 连接池列表类
	 * 
	 * @author 赵明
	 * 
	 */
	public static class CacheConnectionPoolList extends
			HashedConnectionPoolList<ICacheClientConnection, ConnectionPool<ICacheClientConnection>> {
		public CacheConnectionPoolList(String name) {
			super(name);
		}

		public ConcurrentHashMap<String, String> configMap = new ConcurrentHashMap<String, String>();
		public volatile String executorClass;
	}

	static ConcurrentHashMap<String, ConnectionPool<ICacheClientConnection>> pools = new ConcurrentHashMap<String, ConnectionPool<ICacheClientConnection>>();
	static ConcurrentHashMap<String, CacheConnectionPoolList> services = new ConcurrentHashMap<String, CacheConnectionPoolList>();

	/**
	 * 装入配置文件
	 * 
	 */
	static public void loadConfig() {
		try {
			NodeList list = GafUtil.getBasicConfigRoot().getElementsByTagName("cacheservices");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				NodeList list1 = node.getElementsByTagName("pools");
				for (int j = 0; j < list1.getLength(); j++) {
					Element node1 = (Element) list1.item(j);
					NodeList list2 = node1.getElementsByTagName("pool");
					for (int k = 0; k < list2.getLength(); k++) {
						Element node2 = (Element) list2.item(k);
						Class<?> clazz = Class.forName(node2.getAttribute("class"));
						for (Constructor<?> c : clazz.getConstructors()) {
							if (c.getParameterCount() == 1 && c.getParameters()[0].getType().equals(Element.class)) {
								@SuppressWarnings("unchecked")
								ConnectionPool<ICacheClientConnection> pool = (ConnectionPool<ICacheClientConnection>) c
										.newInstance(node2);
								pools.put(pool.getName(), pool);
							}
						}
					}
				}
			}
			for (int i = 0; i < list.getLength(); i++) {
				Element el = (Element) list.item(i);
				NodeList list1 = el.getElementsByTagName("service");
				for (int j = 0; j < list1.getLength(); j++) {
					Element node = (Element) list1.item(j);
					String name = node.getAttribute("name");
					NodeList ls = node.getElementsByTagName("poolref");
					if (name != null && !name.trim().isEmpty() && ls.getLength() > 0) {
						CacheConnectionPoolList cps = new CacheConnectionPoolList(name);
						services.put(name, cps);
						boolean useConsistentHash = false;
						if (node.hasAttribute("useconsistenthash"))
							useConsistentHash = Boolean.valueOf(node.getAttribute("useconsistenthash"));
						cps.setUseConsistentHash(useConsistentHash);
						NodeList ls1 = node.getElementsByTagName("param");
						for (int k = 0; k < ls1.getLength(); k++) {
							node = (Element) ls1.item(k);
							cps.configMap.put(node.getAttribute("name"), node.getAttribute("value"));
						}
						for (int k = 0; k < ls.getLength(); k++) {
							Element el1 = (Element) ls.item(k);
							int weights = 1;
							if (el1.hasAttribute("weights"))
								weights = Integer.valueOf(el1.getAttribute("weights"));
							ConnectionPool<ICacheClientConnection> pool = pools.get(el1.getAttribute("poolname"));
							cps.add(pool, weights);
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	static {
		loadConfig();
	}

	public static ICacheClient getClientInstance(Object caller, String name) throws IOException {
		CacheConnectionPoolList pools = services.get(name);
		if (pools == null)
			return null;
		return new DistributedCacheClient(pools, caller);
	}

}
