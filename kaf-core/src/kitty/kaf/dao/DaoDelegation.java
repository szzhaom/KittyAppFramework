package kitty.kaf.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.dao.resultset.DaoStatement;
import kitty.kaf.dao.table.TableColumnDef;
import kitty.kaf.util.DateTime;

/**
 * 数据访问委托代理
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
abstract public class DaoDelegation {
	protected Connection connection;

	/**
	 * 获取DateTime的表示字串
	 * 
	 * @param o
	 *            时间
	 * @return 获取的时间字串
	 */
	abstract public String getToDateTimeFormat(DateTime o);

	/**
	 * 获取下一个序列值，适合Oracle等，不支持则返回null
	 * 
	 * @param
	 * @param sequence
	 * @return 下一个序列值，不支持则返回null
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	abstract public String getSequenceNextValue(Object sequence) throws SQLException;

	/**
	 * 获取当前自增值
	 * 
	 * @param
	 * @return 自增值，不支持则返回null
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	abstract public String getAutoIncrementValue() throws SQLException;

	/**
	 * 处理包含自定义特殊变量的SQL字串，转换成数据库认识的sql字串
	 * 
	 * @param sql
	 *            要转换的sql
	 * @return 转换后的sql
	 */
	abstract public String processSqlVar(String sql);

	/**
	 * 构造分页SQL
	 * 
	 * @param fields
	 *            查询的字段
	 * @param fromWhereCause
	 *            sql字串中from和where字串
	 * @param orderGroupByCause
	 *            sql字串中order by , gropu by等字串
	 * @param firstIndex
	 *            起始记录索引，如果为-1，则同时查询总记录数，便于外部计算总页数
	 * @param maxResults
	 *            最大返回的记录数
	 * @param params
	 *            查询参数
	 * @return 构造好的分页SQL
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	abstract protected String buildPageSql(Object fields, String fromWhereCause, String orderGroupByCause,
			long firstIndex, int maxResults, Object params) throws SQLException;

	/**
	 * 构造SQL
	 * 
	 * @param fields
	 *            查询的字段
	 * @param fromWhereCause
	 *            sql字串中from和where字串
	 * @param orderGroupByCause
	 *            sql字串中order by , gropu by等字串
	 * @param maxResults
	 *            最大返回的记录数
	 * @param params
	 *            查询参数
	 * @return 构造好的分页SQL
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	protected String buildSql(Object fields, String fromWhereCause, String orderGroupByCause, int maxResults,
			Object params) throws SQLException {
		StringBuffer sb = new StringBuffer("select ");
		if (fields instanceof Object[])
			sb.append(encodeSqlByFields((Object[]) fields));
		else if (fields instanceof Collection<?>)
			sb.append(encodeSqlByFields((Collection<?>) fields));
		else
			sb.append(fields.toString());
		sb.append(" " + fromWhereCause.trim() + " " + orderGroupByCause);
		return sb.toString();

	}

	/**
	 * 构造DaoDelegation
	 * 
	 * @param connection
	 *            JDBC连接
	 */
	public DaoDelegation() {
		super();
	}

	/**
	 * 获取当前使用的Connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * 设置当前使用的Connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * 提交
	 * 
	 * @throws SQLException
	 *             当数据库访问发生错误时
	 */
	public void commit() throws SQLException {
		connection.commit();
	}

	/**
	 * 回滚
	 * 
	 * @throws SQLException
	 *             当数据库访问发生错误时
	 */
	public void rollback() throws SQLException {
		connection.rollback();
	}

	/**
	 * 设置是否自动提交
	 * 
	 * @param autoCommit
	 *            是否自动提交
	 * @throws SQLException
	 *             当数据库访问发生错误时
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		connection.setAutoCommit(autoCommit);
	}

	/**
	 * 将fields组合成SQL字串
	 * 
	 * @param fields
	 *            要组合的字段数组
	 * @return 组合后的字串
	 */
	protected String encodeSqlByFields(Object[] fields) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (Object o : fields) {
			if (i > 0)
				sb.append(",");
			if (o instanceof TableColumnDef)
				sb.append("a." + ((TableColumnDef) o).getColumnName());
			else
				sb.append(o.toString());
			i++;
		}
		return sb.toString();
	}

	/**
	 * 将fields组合成SQL字串
	 * 
	 * @param fields
	 *            要组合的字段数组
	 * @return 组合后的字串
	 */
	protected String encodeSqlByFields(Collection<?> fields) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (Object o : fields) {
			if (i > 0)
				sb.append(",");
			if (o instanceof TableColumnDef)
				sb.append("a." + ((TableColumnDef) o).getColumnName());
			else
				sb.append(o.toString());
			i++;
		}
		return sb.toString();
	}

	/**
	 * 查询SQL，并根据maxResults取回指定的记录数，如果maxResults为0，取回全部记录数
	 * 
	 * @param sql
	 *            SQL字串
	 * @param maxResults
	 *            最大取回的记录数，当此值为0时，取回全部记录数
	 * @param params
	 *            参数列表
	 * @return 查询后返回的数据集
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public DaoResultSet query(String sql, int maxResults, Object params) throws SQLException {
		sql = processSqlVar(sql);
		DaoStatement st = new DaoStatement(connection, false, sql, params);
		try {
			return st.executeQuery(maxResults);
		} finally {
			st.close();
		}
	}

	/**
	 * 查询SQL，无参数，并根据maxResults取回指定的记录数，如果maxResults为0，取回全部记录数
	 * 
	 * @param sql
	 *            SQL字串
	 * @param maxResults
	 *            最大取回的记录数，当此值为0时，取回全部记录数
	 * @return 查询后返回的数据集
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public DaoResultSet query(String sql, int maxResults) throws SQLException {
		sql = processSqlVar(sql);
		DaoStatement st = new DaoStatement(connection, false, sql, null);
		try {
			return st.executeQuery(maxResults);
		} finally {
			st.close();
		}
	}

	/**
	 * 查询SQL，无参数，取回全部记录数
	 * 
	 * @param sql
	 *            SQL字串
	 * @param maxResults
	 *            最大取回的记录数，当此值为0时，取回全部记录数
	 * @return 查询后返回的数据集
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public DaoResultSet query(String sql) throws SQLException {
		return query(sql, 0);
	}

	/**
	 * 执行sql
	 * 
	 * @param sql
	 *            SQL字串
	 * @param params
	 *            参数列表
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public int execute(String sql, Object params) throws SQLException {
		sql = processSqlVar(sql);
		DaoStatement st = new DaoStatement(connection, false, sql, params);
		try {
			return st.execute();
		} finally {
			st.close();
		}
	}

	/**
	 * 执行sql，无参数
	 * 
	 * @param sql
	 *            SQL字串
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public int execute(String sql) throws SQLException {
		sql = processSqlVar(sql);
		DaoStatement st = new DaoStatement(connection, false, sql, null);
		try {
			return st.execute();
		} finally {
			st.close();
		}
	}

	/**
	 * 查询存储过程SQL，并根据maxResults取回指定的记录数，如果maxResults为0，取回全部记录数
	 * 
	 * @param sql
	 *            存储过程SQL字串
	 * @param maxResults
	 *            最大取回的记录数，当此值为0时，取回全部记录数
	 * @param params
	 *            参数列表
	 * @return 查询后返回的数据集
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public DaoResultSet queryCall(String sql, int maxResults, Object params) throws SQLException {
		sql = processSqlVar(sql);
		DaoStatement st = new DaoStatement(connection, true, sql, params);
		try {
			return st.executeQuery(maxResults);
		} finally {
			st.close();
		}
	}

	/**
	 * 查询存储过程SQL，无参数，并根据maxResults取回指定的记录数，如果maxResults为0，取回全部记录数
	 * 
	 * @param sql
	 *            存储过程SQL字串
	 * @param maxResults
	 *            最大取回的记录数，当此值为0时，取回全部记录数
	 * @return 查询后返回的数据集
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public DaoResultSet queryCall(String sql, int maxResults) throws SQLException {
		sql = processSqlVar(sql);
		DaoStatement st = new DaoStatement(connection, true, sql, null);
		try {
			return st.executeQuery(maxResults);
		} finally {
			st.close();
		}
	}

	/**
	 * 查询存储过程SQL，无参数，取回全部记录数
	 * 
	 * @param sql
	 *            存储过程SQL字串
	 * @param maxResults
	 *            最大取回的记录数，当此值为0时，取回全部记录数
	 * @return 查询后返回的数据集
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public DaoResultSet queryCall(String sql) throws SQLException {
		return queryCall(sql, 0);
	}

	/**
	 * 执行存储过程sql
	 * 
	 * @param sql
	 *            存储过程SQL字串
	 * @param params
	 *            参数列表
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public int executeCall(String sql, Object params) throws SQLException {
		sql = processSqlVar(sql);
		DaoStatement st = new DaoStatement(connection, true, sql, params);
		try {
			return st.execute();
		} finally {
			st.close();
		}
	}

	/**
	 * 执行存储过程sql，无参数
	 * 
	 * @param sql
	 *            存储过程SQL字串
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public int executeCall(String sql) throws SQLException {
		sql = processSqlVar(sql);
		DaoStatement st = new DaoStatement(connection, true, sql, null);
		try {
			return st.execute();
		} finally {
			st.close();
		}
	}

	/**
	 * 分页查询
	 * 
	 * @param fields
	 *            查询的字段
	 * @param fromWhereCause
	 *            sql字串中from和where字串
	 * @param orderGroupByCause
	 *            sql字串中order by , gropu by等字串
	 * @param firstIndex
	 *            起始记录索引，如果为-1，则同时查询总记录数，便于外部计算总页数
	 * @param maxResults
	 *            最大返回的记录数
	 * @param params
	 *            查询参数
	 * @return 查询到的结果集
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public DaoResultSet queryPage(Object fields, String fromWhereCause, String orderGroupByCause, long firstIndex,
			int maxResults, Object params) throws SQLException {
		int c = 0;
		if (firstIndex < 0) {
			DaoResultSet st = query(processSqlVar("select count(*) " + fromWhereCause), 1, params);
			if (st.first()) {
				c = st.getInt(1);
			} else
				return new DaoResultSet();
		}
		DaoResultSet r = query(
				processSqlVar(buildPageSql(fields, fromWhereCause, orderGroupByCause, firstIndex, maxResults, params)),
				maxResults, params);
		r.setAllRecordCount(c);
		return r;
	}

	/**
	 * 分页查询，无参数
	 * 
	 * @param fields
	 *            查询的字段
	 * @param fromWhereCause
	 *            sql字串中from和where字串
	 * @param orderGroupByCause
	 *            sql字串中order by , gropu by等字串
	 * @param firstIndex
	 *            起始记录索引，如果为-1，则同时查询总记录数，便于外部计算总页数
	 * @param maxResults
	 *            最大返回的记录数
	 * @param params
	 *            查询参数
	 * @return 查询到的结果集
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public DaoResultSet queryPage(Object fields, String fromWhereCause, String orderGroupByCause, long firstIndex,
			int maxResults) throws SQLException {
		int c = 0;
		if (firstIndex < 0) {
			DaoResultSet st = query(processSqlVar("select count(*) " + fromWhereCause), 1);
			if (st.first()) {
				c = st.getInt(1);
			} else
				return new DaoResultSet();
		}
		DaoResultSet r = query(
				processSqlVar(buildPageSql(fields, fromWhereCause, orderGroupByCause, firstIndex, maxResults, null)),
				maxResults);
		r.setAllRecordCount(c);
		return r;
	}

	/**
	 * 执行SQL，并返回自增字段值
	 * 
	 * @param sql
	 *            SQL
	 * @param params
	 *            参数
	 * @return 自增值
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public String executeAutoGenKeys(String sql, Object params) throws SQLException {
		DaoStatement st = new DaoStatement(connection, sql, true, params);
		try {
			return st.executeAutoGenKeys();
		} finally {
			st.close();
		}
	}
}
