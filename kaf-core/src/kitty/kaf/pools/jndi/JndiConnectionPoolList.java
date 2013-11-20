package kitty.kaf.pools.jndi;

import kitty.kaf.pools.LoopedConnectionPoolList;

/**
 * JNDI连接池列表
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class JndiConnectionPoolList extends
		LoopedConnectionPoolList<JndiConnection, JndiConnectionPool> {

	public JndiConnectionPoolList(String name) {
		super(name);
	}
}
