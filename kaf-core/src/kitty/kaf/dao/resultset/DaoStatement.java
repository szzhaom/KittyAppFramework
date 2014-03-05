package kitty.kaf.dao.resultset;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import kitty.kaf.io.Valuable;
import kitty.kaf.logging.Logger;
import kitty.kaf.util.DateTime;

/**
 * 基于数据库访问的Statement
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class DaoStatement {
	PreparedStatement statement;
	Object params;
	String sql;
	private static final Logger logger = Logger.getLogger(DaoStatement.class);

	/**
	 * 构造DaoStatement
	 * 
	 * @param connection
	 *            JDBC 数据库连接
	 * @param isCall
	 *            是否是创建存储过程调用Statement
	 * @param sql
	 *            创建的SQL
	 * @param params
	 *            参数列表
	 * @throws SQLException
	 *             当数据库发生访问错误时
	 */
	public DaoStatement(Connection connection, boolean isCall, String sql, Object params) throws SQLException {
		this.params = params;
		this.sql = sql;
		this.createStatement(connection, isCall, sql, 0, params);
	}

	/**
	 * 构造DaoStatement
	 * 
	 * @param connection
	 *            JDBC 数据库连接
	 * @param sql
	 *            创建的SQL
	 * @param autoGenKeys
	 *            是否自动生成ID
	 * @param params
	 *            参数列表
	 * @throws SQLException
	 *             当数据库发生访问错误时
	 */
	public DaoStatement(Connection connection, String sql, boolean autoGenKeys, Object params) throws SQLException {
		this.params = params;
		this.createStatement(connection, false, sql, Statement.RETURN_GENERATED_KEYS, params);
	}

	/**
	 * 创建一个Statement
	 * 
	 * @param connection
	 *            JDBC 数据库连接
	 * @param isCall
	 *            是否是创建存储过程调用Statement
	 * @param sql
	 *            创建的SQL
	 * @param params
	 *            参数列表
	 * @throws SQLException
	 *             当数据库发生访问错误时
	 */
	protected void createStatement(Connection connection, boolean isCall, String sql, int autoGenKeys, Object params)
			throws SQLException {
		statement = !isCall ? connection.prepareStatement(sql, autoGenKeys) : connection.prepareCall(sql);
		if (params != null) {
			if (params instanceof Object[]) {
				Object[] s = (Object[]) params;
				for (int i = 0; i < s.length; i++) {
					setObject(i, s[i]);
				}
			} else if (params instanceof Collection<?>) {
				Collection<?> s = (Collection<?>) params;
				int i = 0;
				for (Object o : s) {
					setObject(i++, o);
				}
			}
		}
	}

	/**
	 * 设置参数
	 * 
	 * @param index
	 *            参数索引
	 * @param value
	 *            参数值
	 * @throws SQLException
	 *             当数据库发生访问错误时
	 */
	public void setObject(int index, Object value) throws SQLException {
		if (value == null) {
			statement.setString(index + 1, "");
		} else if (value instanceof String) {
			statement.setString(index + 1, (String) value);
		} else if (value instanceof Integer) {
			statement.setInt(index + 1, (Integer) value);
		} else if (value instanceof Byte) {
			statement.setByte(index + 1, (Byte) value);
		} else if (value instanceof Short) {
			statement.setShort(index + 1, (Short) value);
		} else if (value instanceof Integer) {
			statement.setInt(index + 1, (Integer) value);
		} else if (value instanceof Long) {
			statement.setLong(index + 1, (Long) value);
		} else if (value instanceof Float) {
			statement.setFloat(index + 1, (Float) value);
		} else if (value instanceof Double) {
			statement.setDouble(index + 1, (Double) value);
		} else if (value instanceof Boolean) {
			statement.setBoolean(index + 1, (Boolean) value);
		} else if (value instanceof Calendar) {
			statement.setTimestamp(index + 1, new Timestamp(((Calendar) value).getTimeInMillis()));
		} else if (value instanceof Date) {
			statement.setTimestamp(index + 1, new Timestamp(((Date) value).getTime()));
		} else if (value instanceof DateTime) {
			statement.setTimestamp(index + 1, new Timestamp(((DateTime) value).getTimeInMillis()));
		} else if (value instanceof StringBuffer) {
			statement.setString(index + 1, ((StringBuffer) value).toString());
		} else if (value instanceof byte[]) {
			statement.setBytes(index + 1, (byte[]) value);
		} else if (value instanceof BigDecimal) {
			statement.setBigDecimal(index + 1, (BigDecimal) value);
		} else if (value instanceof Blob) {
			statement.setBlob(index + 1, (Blob) value);
		} else if (value instanceof Clob) {
			statement.setClob(index + 1, (Clob) value);
		} else if (value instanceof Array) {
			statement.setArray(index + 1, (Array) value);
		} else if (value instanceof Valuable<?>) {
			statement.setObject(index + 1, ((Valuable<?>) value).getValue());
		} else if (value instanceof DaoParameter) {
			DaoParameter p = (DaoParameter) value;
			statement.setObject(index + 1, p.getValue(), p.getDataType());
		} else if (value instanceof DaoOutParameter) {
			DaoOutParameter v = (DaoOutParameter) value;
			if (v.getTypeName() != null)
				((CallableStatement) statement).registerOutParameter(index + 1, v.getDataType(), v.getTypeName());
			else if (v.getScale() != null)
				((CallableStatement) statement).registerOutParameter(index + 1, v.getDataType(), v.getScale());
			else
				((CallableStatement) statement).registerOutParameter(index + 1, v.getDataType());
		} else
			statement.setString(index + 1, value + "");
	}

	/**
	 * 获取输出参数值
	 * 
	 * @param p
	 *            输出参数
	 * @param st
	 *            存储过程对象
	 * @param index
	 *            参数索引
	 * @return 参数值
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	protected Object getParamOutValue(DaoOutParameter p, int index) throws SQLException {
		int type = p.getDataType();
		if (type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP)
			return ((CallableStatement) statement).getTimestamp(index);
		else {
			Object r = ((CallableStatement) statement).getObject(index);
			if (r instanceof ResultSet) {
				try {
					DaoResultSet rset = new DaoResultSet((ResultSet) r, 0);
					return rset;
				} finally {
					((ResultSet) r).close();
				}
			} else
				return r;
		}
	}

	/**
	 * 执行语句
	 * 
	 * @return 返回更新的行数
	 * @throws SQLException
	 *             当数据库发生访问错误时
	 */
	public int execute() throws SQLException {
		Date now = new Date();
		try {
			statement.execute();
			if (params != null && statement instanceof CallableStatement) {
				int i = 0;
				if (params instanceof Object[])
					for (Object o : (Object[]) params) {
						i++;
						if (o instanceof DaoOutParameter) {
							DaoOutParameter v = (DaoOutParameter) o;
							v.setValue(getParamOutValue(v, i));
						}
					}
				else if (params instanceof Collection<?>) {
					for (Object o : (Collection<?>) params) {
						i++;
						if (o instanceof DaoOutParameter) {
							DaoOutParameter v = (DaoOutParameter) o;
							v.setValue(getParamOutValue(v, i));
						}
					}
				} else
					throw new SQLException("Wrong parameter[params]");
			}
			return statement.getUpdateCount();
		} finally {
			logger.debug("execute(duration=" + DateTime.milliSecondsBetween(now, new Date()) + "):" + sql);
		}
	}

	/**
	 * 执行并返回自增的结果
	 * 
	 * @return 自增的结果
	 * @throws SQLException
	 *             当数据库发生访问错误时
	 */
	public String executeAutoGenKeys() throws SQLException {
		Date now = new Date();
		ResultSet rset = null;
		try {
			statement.executeUpdate();
			try {
				rset = statement.getGeneratedKeys();
				if (rset.next()) {
					return rset.getString(1);
				} else
					return null;
			} catch (Throwable e) {
				return null;
			}
		} finally {
			logger.debug("executeAutoGenKeys(duration=" + DateTime.milliSecondsBetween(now, new Date()) + "):" + sql);
			if (rset != null) {
				try {
					rset.close();
				} catch (Throwable e) {
				}
			}
		}
	}

	/**
	 * 在此 PreparedStatement 对象中执行 SQL 查询，并返回该查询生成的 DaoResultSet 对象。
	 * 
	 * @return 包含该查询生成的数据的 ResultSet 对象；从不返回 null
	 * @throws SQLException
	 *             如果发生数据库访问错误或者 SQL 语句没有返回一个 ResultSet 对象
	 */
	public DaoResultSet executeQuery(int maxResults) throws SQLException {
		Date now = new Date();
		ResultSet rset = statement.executeQuery();
		try {
			DaoResultSet r = new DaoResultSet(rset, maxResults);
			if (params != null && statement instanceof CallableStatement) {
				int i = 0;
				if (params instanceof Object[])
					for (Object o : (Object[]) params) {
						i++;
						if (o instanceof DaoOutParameter) {
							DaoOutParameter v = (DaoOutParameter) o;
							v.setValue(getParamOutValue(v, i));
						}
					}
				else if (params instanceof Collection<?>) {
					for (Object o : (Collection<?>) params) {
						i++;
						if (o instanceof DaoOutParameter) {
							DaoOutParameter v = (DaoOutParameter) o;
							v.setValue(getParamOutValue(v, i));
						}
					}
				} else
					throw new SQLException("Wrong parameter[params]");
			}
			return r;
		} finally {
			logger.debug("executeQuery(duration=" + DateTime.milliSecondsBetween(now, new Date()) + "):" + sql);
			rset.close();
		}
	}

	/**
	 * 关闭statement
	 * 
	 * @throws SQLException
	 *             如果数据库访问发生错误
	 */
	public void close() throws SQLException {
		if (statement != null) {
			statement.close();
			statement = null;
		}
	}
}
