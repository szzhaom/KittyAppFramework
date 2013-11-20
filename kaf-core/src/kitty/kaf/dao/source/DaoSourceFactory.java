package kitty.kaf.dao.source;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.KafUtil;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.pools.db.DatabaseConnection;
import kitty.kaf.pools.db.DatabaseConnectionPool;
import kitty.kaf.watch.WatchFactory;
import kitty.kaf.watch.Watcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class MasterSlaveDaoPoolConfig {
	Object master, slave;
	String type;
}

/**
 * 数据库对象创建工厂
 * 
 * @author 赵明
 * 
 */
public class DaoSourceFactory {

	static final KafLogger logger = KafLogger
			.getLogger(DaoSourceFactory.class);
	static ConcurrentHashMap<String, MasterSlaveDaoPoolConfig> poolsMap = new ConcurrentHashMap<String, MasterSlaveDaoPoolConfig>();
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
			NodeList list = doc.getElementsByTagName("database");
			Element root = (Element) list.item(0);
			list = root.getElementsByTagName("pools");
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				NodeList list1 = node.getElementsByTagName("pool");
				for (int j = 0; j < list1.getLength(); j++) {
					Element el = (Element) list1.item(j);
					String name = el.getAttribute("name");
					if (el.hasAttribute("jndiname")
							&& !el.getAttribute("jndiname").isEmpty()) {
						map.put(name, el.getAttribute("jndiname"));
					} else if (el.hasAttribute("url")
							&& !el.getAttribute("url").isEmpty()) {
						DatabaseConnectionPool<DatabaseConnection> pool = new DatabaseConnectionPool<DatabaseConnection>(
								name,
								Integer.valueOf(el
										.getAttribute("minconnections")),
								Integer.valueOf(el
										.getAttribute("maxconnections")),
								Integer.valueOf(el.getAttribute("timeout")) * 1000);
						pool.setClassName(el.getAttribute("driver"));
						pool.setUser(el.getAttribute("username"));
						pool.setPasswd(el.getAttribute("password"));
						pool.setConnectionUrl(el.getAttribute("url"));
						pool.setAliveSql(el.getAttribute("alivesql"));
						if (el.hasAttribute("keep-alive-interval"))
							pool.setKeepAliveInterval(Integer.valueOf(el
									.getAttribute("keep-alive-interval")) * 1000);
						if (el.hasAttribute("idle-maximum-period"))
							pool.setIdleMaximumPeriod(Integer.valueOf(el
									.getAttribute("idle-maximum-period")) * 1000);
						if (el.hasAttribute("work-maximum-period"))
							pool.setWorkMaximumPeriod(Integer.valueOf(el
									.getAttribute("work-maximum-period")) * 1000);
						if (el.hasAttribute("watcher")) {
							Watcher watcher = WatchFactory.getWatcher(el
									.getAttribute("watcher"));
							if (watcher != null)
								pool.setWatcher(watcher);
						}
						map.put(name, pool);
					}
				}
			}
			list = root.getElementsByTagName("daosources");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				NodeList list1 = node.getElementsByTagName("dao");
				for (int j = 0; j < list1.getLength(); j++) {
					Element el = (Element) list1.item(j);
					String pname = el.getAttribute("master_poolname");
					Object master = map.get(pname);
					if (master != null) {
						String sname = el.hasAttribute("slave_poolname") ? pname
								: el.getAttribute("slave_poolname");
						MasterSlaveDaoPoolConfig mpc = new MasterSlaveDaoPoolConfig();
						mpc.type = el.getAttribute("type");
						mpc.master = master;
						if (!pname.equals(sname)) {
							mpc.slave = map.get(sname);
						}
						poolsMap.put(el.getAttribute("name"), mpc);
					}
				}
			}
			map.clear();
			map = null;
		} catch (Throwable e) {
			logger.error("init dao failure:", e);
		}
	}

	/**
	 * 获取数据源
	 * 
	 * @param caller
	 *            调用者
	 * @param name
	 *            Dao数据源名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static DaoSource getDaoSource(Object caller, String name)
			throws SQLException {
		MasterSlaveDaoPoolConfig config = poolsMap.get(name);
		if (config == null)
			throw new SQLException("dao source [" + name + "] not found.");
		if (config.master == null)
			throw new SQLException("dao source [" + name
					+ "] not config [master_poolname] or not find.");
		if (config.master instanceof String)
			return new JndiDaoSource(config.type, (String) config.master,
					(String) config.slave);
		else {
			DatabaseConnectionPool<DatabaseConnection> master = (DatabaseConnectionPool<DatabaseConnection>) config.master;
			DatabaseConnectionPool<DatabaseConnection> slave = (DatabaseConnectionPool<DatabaseConnection>) (config.slave != null ? config.slave
					: null);
			PoolDataSource<DatabaseConnection> m = new PoolDataSource<DatabaseConnection>(
					caller, master);
			PoolDataSource<DatabaseConnection> s = slave == null ? null
					: new PoolDataSource<DatabaseConnection>(caller, slave);
			return new DaoSource(config.type, m, s);
		}
	}

	public static void main(String[] args) {
		try {
			DaoSource source = DaoSourceFactory.getDaoSource(
					Thread.currentThread(), "default");
			while (true) {
				if (source == null) {
					try {
						source = DaoSourceFactory.getDaoSource(
								Thread.currentThread(), "default");
					} catch (Throwable e) {
						e.printStackTrace();
					}
				} else {
					try {
						Statement st = source.getMaster().createStatement();
						st.executeQuery("select version()").close();
						st.close();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					try {
						if (source.getMaster().isClosed()) {
							source.close();
							source = null;
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				Thread.sleep(1000);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
