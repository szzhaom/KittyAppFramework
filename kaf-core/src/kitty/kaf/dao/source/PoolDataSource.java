package kitty.kaf.dao.source;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.pools.db.DatabaseConnection;
import kitty.kaf.pools.db.DatabaseConnectionPool;

public class PoolDataSource<E extends DatabaseConnection> implements DataSource {
	PrintWriter logWriter;
	int loginTimeout;
	DatabaseConnectionPool<E> pool;
	Object caller;

	public PoolDataSource(Object caller, DatabaseConnectionPool<E> pool) {
		this.pool = pool;
		this.caller = caller;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		loginTimeout = seconds;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			return pool.getConnection(caller);
		} catch (InterruptedException e) {
			throw new SQLException(e);
		} catch (ConnectException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		throw new SQLException("不支持该接口");
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
