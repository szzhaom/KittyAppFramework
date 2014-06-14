package kitty.kaf.pools.db;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.exceptions.DataException;
import kitty.kaf.logging.Logger;
import kitty.kaf.pools.Connection;
import kitty.kaf.pools.ConnectionPool;

/**
 * 数据库连接
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class DatabaseConnection extends Connection implements
		java.sql.Connection {
	private static Logger logger = Logger.getLogger(DatabaseConnection.class);
	/**
	 * JDBC连接对象
	 */
	private java.sql.Connection connection;

	public DatabaseConnection(ConnectionPool<?> pool,
			java.sql.Connection connection) {
		super(pool);
		this.connection = connection;
	}

	@Override
	public void setCaller(Object caller) {
		super.setCaller(caller);
		if (caller != null) { // 新指定调用时，打开连接
			try {
				open();
			} catch (ConnectException e) {
				logger.debug("Failed to establish connection:", e);
			}
		}
	}

	public void updateLastAliveTime() {
		setLastAliveTime(System.currentTimeMillis());
	}

	@Override
	public void open() throws ConnectException {
		if (isClosed()) {
			if (getPool() == null)
				throw new ConnectException("Can not reconnect, pool is null.");
			connection = ((DatabaseConnectionPool<?>) getPool())
					.newJdbcConnection();
		}
	}

	@Override
	public String toString() {
		return getPool() == null ? (connection == null ? super.toString()
				: connection.toString()) : getPool().getName();
	}

	@Override
	protected void forceClose() {
		if (connection != null) {
			logger.debug(this + ": Be forced to close.");
			try {
				connection.close();
			} catch (Throwable e) {
			}
			connection = null;
		}
	}

	@Override
	public boolean isClosed() {
		try {
			return connection == null || connection.isClosed();
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		Statement st = connection.createStatement();
		updateLastAliveTime();
		return st;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		PreparedStatement st = connection.prepareStatement(sql);
		updateLastAliveTime();
		return st;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		CallableStatement st = connection.prepareCall(sql);
		updateLastAliveTime();
		return st;
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		String r = connection.nativeSQL(sql);
		updateLastAliveTime();
		return r;
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.setAutoCommit(autoCommit);
		updateLastAliveTime();
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.commit();
		updateLastAliveTime();
	}

	@Override
	public void rollback() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.rollback();
		updateLastAliveTime();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.setReadOnly(readOnly);
		updateLastAliveTime();
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.setCatalog(catalog);
		updateLastAliveTime();
	}

	@Override
	public String getCatalog() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.setTransactionIsolation(level);
		updateLastAliveTime();
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		Statement r = connection.createStatement(resultSetType,
				resultSetConcurrency);
		updateLastAliveTime();
		return r;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		PreparedStatement r = connection.prepareStatement(sql, resultSetType,
				resultSetConcurrency);
		updateLastAliveTime();
		return r;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		CallableStatement r = connection.prepareCall(sql, resultSetType,
				resultSetConcurrency);
		updateLastAliveTime();
		return r;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.setTypeMap(map);
		updateLastAliveTime();
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.setHoldability(holdability);
		updateLastAliveTime();
	}

	@Override
	public int getHoldability() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		Savepoint r = connection.setSavepoint(name);
		updateLastAliveTime();
		return r;
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.rollback(savepoint);
		updateLastAliveTime();
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.releaseSavepoint(savepoint);
		updateLastAliveTime();
	}

	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		Statement r = connection.createStatement(resultSetType,
				resultSetConcurrency, resultSetHoldability);
		updateLastAliveTime();
		return r;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		PreparedStatement r = connection.prepareStatement(sql, resultSetType,
				resultSetConcurrency, resultSetHoldability);
		updateLastAliveTime();
		return r;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		CallableStatement r = connection.prepareCall(sql, resultSetType,
				resultSetConcurrency, resultSetHoldability);
		updateLastAliveTime();
		return r;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		PreparedStatement r = connection.prepareStatement(sql,
				autoGeneratedKeys);
		updateLastAliveTime();
		return r;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		PreparedStatement r = connection.prepareStatement(sql, columnIndexes);
		updateLastAliveTime();
		return r;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		PreparedStatement r = connection.prepareStatement(sql, columnNames);
		updateLastAliveTime();
		return r;
	}

	@Override
	public Clob createClob() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		Clob c = connection.createClob();
		updateLastAliveTime();
		return c;
	}

	@Override
	public Blob createBlob() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		Blob c = connection.createBlob();
		updateLastAliveTime();
		return c;
	}

	@Override
	public NClob createNClob() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		NClob c = connection.createNClob();
		updateLastAliveTime();
		return c;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		SQLXML c = connection.createSQLXML();
		updateLastAliveTime();
		return c;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		if (connection != null) {
			connection.setClientInfo(name, value);
			updateLastAliveTime();
		}
	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		if (connection != null) {
			connection.setClientInfo(properties);
			updateLastAliveTime();
		}
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		return connection.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		Array c = connection.createArrayOf(typeName, elements);
		updateLastAliveTime();
		return c;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		Struct c = connection.createStruct(typeName, attributes);
		updateLastAliveTime();
		return c;
	}

	@Override
	public void keepAlive() throws DataException {
		try {
			Statement st = createStatement();
			ResultSet rset = null;
			try {
				rset = st.executeQuery(((DatabaseConnectionPool<?>) getPool())
						.getAliveSql());
				rset.close();
				rset = null;
			} finally {
				if (rset != null) {
					try {
						rset.close();
					} catch (Throwable e) {
					}
				}
				st.close();
			}
		} catch (SQLException e) {
			if (isClosed()) {
				logger.debug("Connection has been disconnected, removed");
				getPool().removeConnection(this);
			}
			throw new DataException(e);
		}
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.setSchema(schema);
		updateLastAliveTime();
	}

	@Override
	public String getSchema() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		String r = connection.getSchema();
		updateLastAliveTime();
		return r;
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.abort(executor);
		updateLastAliveTime();
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		connection.setNetworkTimeout(executor, milliseconds);
		updateLastAliveTime();
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		if (connection == null)
			throw new SQLException("Connection is not established");
		int r = connection.getNetworkTimeout();
		updateLastAliveTime();
		return r;
	}

}
