package kitty.kaf.pools.jndi;

import javax.naming.Context;
import javax.naming.NamingException;

import kitty.kaf.GafUtil;

/**
 * Jndi查找器，用于不同的应用服务器间转换
 * 
 * @author zhaom
 * 
 */
public class Lookuper {
	/**
	 * 查找EJB
	 */
	static final public int JNDI_TYPE_EJB = 0;
	/**
	 * 查找DataSource
	 */
	static final public int JNDI_TYPE_DATASOURCE = 1;
	private volatile int appServerType = GafUtil.getAppServerType();

	/**
	 * 查询session bean对象
	 * 
	 * @param context
	 *            Jndi命名上下文
	 * @param jndiType
	 *            jndi类型，本类实现包含：JNDI_TYPE_EJB,JNDI_TYPE_DATASOURCE，为了方便以后的升级，
	 *            派生类的类型请从1000开始
	 * @param name
	 *            jndi名称
	 * @param clazz
	 *            返回的类
	 * @return 查找到的对象
	 * @throws NamingException
	 */
	public <E> E lookup(Context context, int jndiType, String name, Class<E> clazz) throws NamingException {
		switch (jndiType) {
		case JNDI_TYPE_EJB:
			return ejbLookup(context, name, clazz);
		case JNDI_TYPE_DATASOURCE:
			return dataSourceLookup(context, name, clazz);
		default:
			throw new NamingException("不被支持的jndi类型[" + jndiType + "]");
		}
	}

	@SuppressWarnings("unchecked")
	public <E> E ejbLookup(Context context, String name, Class<E> clazz) throws NamingException {
		switch (appServerType) {
		case GafUtil.APP_SERVER_JBOSS:
			return (E) context.lookup("java:/app/" + name);
		case GafUtil.APP_SERVER_WEBLOGIC:
			return (E) context.lookup(name + "#" + clazz.getName());
		case GafUtil.APP_SERVER_WEBSPHERE:
			return (E) context.lookup(clazz.getName());
		default:
			return (E) context.lookup(name);
		}
	}

	@SuppressWarnings("unchecked")
	public <E> E dataSourceLookup(Context context, String name, Class<E> clazz) throws NamingException {
		switch (GafUtil.getAppServerType()) {
		case GafUtil.APP_SERVER_JBOSS:
			return (E) context.lookup("java:/" + name);
		case GafUtil.APP_SERVER_WEBLOGIC:
			return (E) context.lookup(name);
		default:
			return (E) context.lookup(name);
		}
	}

	public int getAppServerType() {
		return appServerType;
	}

	public void setAppServerType(int appServerType) {
		this.appServerType = appServerType;
	}
}
