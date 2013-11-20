package kitty.kaf.pools.jndi;

import java.util.concurrent.ConcurrentHashMap;

/**
 * JNDI连接池列表分组
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class JndiConnecitonPoolListGroup {
	Lookuper lookuper;
	JndiConnectionPool localPool;
	ConcurrentHashMap<String, JndiConnectionPoolList> poolsMap = new ConcurrentHashMap<String, JndiConnectionPoolList>();
	ConcurrentHashMap<Object, Object> properties = new ConcurrentHashMap<Object, Object>();

	public JndiConnecitonPoolListGroup() {
		super();
	}

	public Lookuper getLookuper() {
		return lookuper;
	}

	public void setLookuper(Lookuper lookuper) {
		this.lookuper = lookuper;
	}

	public JndiConnectionPool getLocalPool() {
		return localPool;
	}

	public void setLocalPool(JndiConnectionPool localPool) {
		this.localPool = localPool;
	}

	public ConcurrentHashMap<String, JndiConnectionPoolList> getPoolsMap() {
		return poolsMap;
	}

	public void setPoolsGroups(
			ConcurrentHashMap<String, JndiConnectionPoolList> poolsMap) {
		this.poolsMap = poolsMap;
	}

	public ConcurrentHashMap<Object, Object> getProperties() {
		return properties;
	}

	public void setProperties(ConcurrentHashMap<Object, Object> properties) {
		this.properties = properties;
	}

}
