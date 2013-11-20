package kitty.kaf.pools.jndi;

import java.util.Map;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.pools.ConnectionPool;

public class JndiConnectionPool extends ConnectionPool<JndiConnection> {
	JndiConnection connection;
	String url;
	Map<Object, Object> properties;
	Lookuper lookuper;

	public JndiConnectionPool(String name, String url, Lookuper lookuper, Map<Object, Object> properties,
			int maxConnectionSize) {
		super(name, 0, maxConnectionSize, 0);
		this.url = url;
		this.lookuper = lookuper;
		this.properties = properties;
	}

	@Override
	protected synchronized JndiConnection createConnection() throws ConnectException {
		if (connection == null) {
			connection = new JndiConnection(this, url, properties, lookuper);
		} else if (connection.isClosed())
			connection.open();
		return connection;
	}

	@Override
	protected synchronized void disposeConnection(JndiConnection c) {
		c.forceClose();
	}

	@Override
	public Object getConnectionUrl() {
		return url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<Object, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<Object, Object> properties) {
		this.properties = properties;
	}

	public Lookuper getLookuper() {
		return lookuper;
	}

	public void setLookuper(Lookuper lookuper) {
		this.lookuper = lookuper;
	}

}
