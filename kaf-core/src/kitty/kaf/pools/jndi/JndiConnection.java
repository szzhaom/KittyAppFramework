package kitty.kaf.pools.jndi;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.exceptions.DataException;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.pools.Connection;
import kitty.kaf.pools.ConnectionPool;

public class JndiConnection extends Connection {
	String url;
	Map<Object, Object> properties;
	Lookuper lookuper;
	InitialContext context;
	final static KafLogger logger = KafLogger.getLogger(JndiConnection.class);
	String checkAliveBeanName;
	AliveTestBeanRemote aliveBean;
	/**
	 * 缓存查找到的bean
	 */
	ConcurrentHashMap<String, Object> lookupedBeanMap = new ConcurrentHashMap<String, Object>();

	JndiConnection() {
		super();
	}

	public JndiConnection(ConnectionPool<?> pool, String url, Map<Object, Object> properties, Lookuper lookuper) {
		super(pool);
		this.url = url;
		this.properties = properties;
		this.lookuper = lookuper;
	}

	/**
	 * 查找一个jndi对象
	 * 
	 * @param jndiType
	 *            jndi资源类型
	 * @param name
	 *            会话Bean的Jndi名称
	 * @return 对象
	 * @throws NamingException
	 */
	public <E> E lookup(int jndiType, String name, Class<E> clazz) throws ConnectException {
		String key = name + "_" + jndiType;
		@SuppressWarnings("unchecked")
		E r = (E) lookupedBeanMap.get(key);
		if (r != null) {
			logger.info("lookup(" + key + "): return from cache ==> " + r);
			return r;
		}
		if (isClosed())
			open();
		try {
			r = lookuper.lookup(context, jndiType, name, clazz);
			if (r != null) {
				lookupedBeanMap.put(name + "_" + jndiType, r);
			}
			logger.info("lookup(" + key + "): return from lookuper.lookup()==>" + r);
			return r;
		} catch (NamingException e) {
			forceClose();
			throw new ConnectException(e);
		}
	}

	@Override
	public void open() throws ConnectException {
		try {
			if (context == null) {
				if (url == null) { // 创建本地Jndi调用
					context = new InitialContext();
				} else {
					Properties props = new Properties();
					props.putAll(properties);
					props.setProperty("java.naming.provider.url", url);
					context = new InitialContext(props);
				}
				aliveBean = null;
			}
			if (aliveBean == null) {
				if (checkAliveBeanName != null && !checkAliveBeanName.isEmpty()) {
					aliveBean = lookup(Lookuper.JNDI_TYPE_EJB, checkAliveBeanName, AliveTestBeanRemote.class);
					if (!aliveBean.isOk(""))
						throw new ConnectException("连接检查失败.");
				}
			}
		} catch (NamingException e) {
			throw new ConnectException(e);
		}
	}

	@Override
	public boolean isClosed() {
		return context == null;
	}

	@Override
	public void keepAlive() throws DataException {
		if (aliveBean != null) {
			try {
				if (!aliveBean.isOk("")) {
					forceClose();
				}
			} catch (Throwable e) {
				try {
					forceClose();
				} catch (Throwable ex) {
				}
			}
		}
	}

	@Override
	protected void forceClose() {
		lookupedBeanMap.clear();
		if (context != null) {
			try {
				context.close();
			} catch (Throwable e) {
			}
			context = null;
		}
	}

	public InitialContext getContext() {
		return context;
	}

}
