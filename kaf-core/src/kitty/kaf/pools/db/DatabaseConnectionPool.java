package kitty.kaf.pools.db;

import java.sql.DriverManager;
import java.sql.SQLException;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.pools.ConnectionPool;

/**
 * 数据库连接池
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * @param <C>
 */
public class DatabaseConnectionPool<C extends DatabaseConnection> extends
		ConnectionPool<C> {
	/**
	 * 构建一个连接池
	 * 
	 * @param name
	 *            连接池名称
	 * @param minConnectionSize
	 *            最小连接数
	 * @param maxConnectionSize
	 *            最大的连接数
	 * @param connectionTimeout
	 *            连接超时时间，以毫秒为单位
	 */
	public DatabaseConnectionPool(String name, int minConnectionSize,
			int maxConnectionSize, int connectionTimeout) {
		super(name, minConnectionSize, maxConnectionSize, connectionTimeout);
	}

	private String className, connectionUrl, user, passwd, aliveSql;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}

	public void setConnectionUrl(String url) {
		this.connectionUrl = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	/**
	 * 根据已经创建的JDBC连接，创建一个DatabaseConnection<br>
	 * <span
	 * style='color:red'>注意：本方法只创建DatabaseConnection，如果是DatabaseConnection的继承类，
	 * 则需要继承DatabaseConnectionPool写一个类来具体实现 </span>
	 * 
	 * @param con
	 *            已经创建的JDBC数据
	 * @return DatabaseConnection
	 */
	@SuppressWarnings("unchecked")
	protected C createConnection(java.sql.Connection con) {
		return (C) new DatabaseConnection(this, con);
	}

	/**
	 * 新建一个JDBC连接
	 * 
	 * @return 新建的JDBC连接
	 * @throws ConnectException
	 *             新建连接失败时抛出
	 */
	java.sql.Connection newJdbcConnection() throws ConnectException {
		try {
			Class.forName(className);
			java.sql.Connection connection = DriverManager.getConnection(
					connectionUrl, user, passwd);
			return connection;
		} catch (ClassNotFoundException e) {
			throw new ConnectException(e);
		} catch (SQLException e) {
			throw new ConnectException(e);
		}
	}

	@Override
	protected C createConnection() throws ConnectException {
		try {
			Class.forName(className);
			java.sql.Connection connection = DriverManager.getConnection(
					connectionUrl, user, passwd);
			return createConnection(connection);
		} catch (ClassNotFoundException e) {
			throw new ConnectException(e);
		} catch (SQLException e) {
			throw new ConnectException(e);
		}
	}

	@Override
	protected void disposeConnection(C c) {
		c.forceClose();
	}

	public synchronized String getAliveSql() {
		return aliveSql;
	}

	public synchronized void setAliveSql(String aliveSql) {
		this.aliveSql = aliveSql;
	}

	@Override
	public String toString() {
		return getName();
	}

}
