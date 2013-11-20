package kitty.kaf.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.Index;
import kitty.kaf.dao.tools.Partition;
import kitty.kaf.dao.tools.PartitionItem;
import kitty.kaf.dao.tools.datatypes.DbColumnDataType;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.tokenizers.SQLFileTokenizer;

/**
 * SQL文件执行助手程序
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class SQLHelper {
	static KafLogger logger = KafLogger.getLogger(SQLHelper.class);

	/**
	 * 构造一个基于IDLIST或运算的SQL
	 * 
	 * @param table
	 * @param columnName
	 * @param params
	 * @return
	 */
	public static String buildSelectOrListSql(String table, String columnName, List<?> params) {
		StringBuffer sb = new StringBuffer("select " + columnName + " from " + table + " where ");
		for (int i = 0; i < params.size(); i++) {
			if (i > 0)
				sb.append(" or ");
			sb.append(columnName + "=?");
		}
		return sb.toString();
	}

	/**
	 * 构造一个基于IDLIST或运算的删除SQL
	 * 
	 * @param table
	 * @param columnName
	 * @param params
	 * @return
	 */
	public static String buildDeleteOrListSql(String table, String columnName, List<?> params) {
		StringBuffer sb = new StringBuffer("delete from " + table + " where ");
		for (int i = 0; i < params.size(); i++) {
			if (i > 0)
				sb.append(" or ");
			sb.append(columnName + "=?");
		}
		return sb.toString();
	}

	public static String processVarSql(String type, String sql) {
		if (type.equalsIgnoreCase("mysql")) {
			sql = sql.replace("${now}", "now()");
		} else if (type.equalsIgnoreCase("oracle"))
			sql = sql.replace("${now}", "sysdate");
		return sql;
	}

	/**
	 * 检查表是否存在
	 * 
	 * @param con
	 *            数据库连接
	 * @param tableName
	 *            表名
	 * @return true - 表存在；false - 表不存在
	 * @throws SQLException
	 *             如果发生数据库访问错误
	 */
	public static boolean tableExists(Connection con, String tableName) throws SQLException {
		ResultSet rset = con.getMetaData().getTables(null, "%", tableName, new String[] { "TABLE" });
		try {
			while (rset.next()) {
				if (rset.getString("TABLE_NAME").equalsIgnoreCase(tableName))
					return true;
			}
			return false;
		} finally {
			rset.close();
		}
	}

	/**
	 * 获取分区信息，没有分区的返回null
	 * 
	 * @param dbType
	 *            数据库类别
	 * @param con
	 *            数据库连接
	 * @param tableName
	 *            表名
	 * @return 分区信息
	 * @throws SQLException
	 *             如果发生数据库访问错误
	 */
	static public Partition getPartitionInfo(String dbType, Connection con, String tableName) throws SQLException {
		Statement st = con.createStatement();
		ResultSet rset = null;
		try {
			Partition p = null;
			rset = st.executeQuery("select * from information_schema.partitions where table_name='" + tableName
					+ "' and not partition_name is null");
			while (rset.next()) {
				String name = rset.getString("PARTITION_NAME");
				String type = rset.getString("PARTITION_METHOD");
				String columns = rset.getString("PARTITION_EXPRESSION");
				String value = rset.getString("PARTITION_DESCRIPTION");
				if (p == null) {
					p = new Partition();
					p.setType(type);
					p.setColumns(columns);
				}
				p.setNeedAdded(false);
				p.getItemsMap().put(name, new PartitionItem(name, value));
			}
			return p;
		} finally {
			if (rset != null)
				rset.close();
			st.close();
		}
	}

	/**
	 * 获取一个表的字段
	 * 
	 * @param tableName
	 *            表名
	 * @return 获取的表字段列表
	 * @throws SQLException
	 *             如果发生数据库访问错误
	 */
	static public List<Column> getColumns(Connection con, String tableName) throws SQLException {
		List<Column> ls = new ArrayList<Column>();
		ResultSet rset = con.getMetaData().getColumns(null, "%", tableName, "%");
		try {
			while (rset.next()) {
				String name = rset.getString("COLUMN_NAME");
				Column c = new Column(name);
				c.setLength(rset.getInt("COLUMN_SIZE"));
				c.setDigits(rset.getInt("DECIMAL_DIGITS"));
				c.setNullable(rset.getInt("NULLABLE") != 0);
				c.setDesp(rset.getString("REMARKS"));
				c.setDataType(new DbColumnDataType(c, rset.getString("TYPE_NAME")));
				c.setAutoIncrement("YES".equalsIgnoreCase(rset.getString("IS_AUTOINCREMENT")));
				c.setDef(rset.getString("COLUMN_DEF"));
				c.setNeedAdded(false);
				ls.add(c);
			}
			return ls;
		} finally {
			rset.close();
		}
	}

	/**
	 * 获取表的主键，如果没有主键，返回null
	 * 
	 * @param con
	 *            数据库连接
	 * @param tableName
	 *            表名
	 * @return 表主键
	 * @throws SQLException
	 *             如果发生数据库访问错误
	 */
	static public Index getPrimaryKey(Connection con, String tableName) throws SQLException {
		DaoResultSet rset = new DaoResultSet(con.getMetaData().getPrimaryKeys(null, "%", tableName), 0);
		try {
			Index index = null;
			final int i = rset.getColumnIndex("PK_NAME") - 1, k = rset.getColumnIndex("KEY_SEQ") - 1;
			rset.sort(new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					int r = o1[i].toString().compareTo(o2[i].toString());
					if (r == 0) {
						return ((Number) o1[k]).intValue() - ((Number) o2[k]).intValue();
					}
					return r;
				}
			});
			while (rset.next()) {
				String name = rset.getString("PK_NAME");
				String column = rset.getString("COLUMN_NAME");
				if (index == null) {
					index = new Index();
					index.setUniqueKey(name);
					index.setColumns(column);
				} else {
					index.setColumns(index.getColumns() + "," + column);
				}
				index.setNeedAdded(false);
			}
			return index;
		} finally {
		}
	}

	/**
	 * 获取一个表的索引
	 * 
	 * @param tableName
	 *            表名
	 * @param isUnique
	 *            是否是唯一索引
	 * @return 获取的表索引列表
	 * @throws SQLException
	 *             如果发生数据库访问错误
	 */
	static public List<Index> getIndexes(Connection con, String tableName, boolean isUnique) throws SQLException {
		List<Index> ls = new ArrayList<Index>();
		DaoResultSet rset = new DaoResultSet(con.getMetaData().getIndexInfo(null, "%", tableName, isUnique, true), 0);
		try {
			Index index = null;
			final int i = rset.getColumnIndex("INDEX_NAME") - 1, k = rset.getColumnIndex("ORDINAL_POSITION") - 1;
			rset.sort(new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					int r = o1[i].toString().compareTo(o2[i].toString());
					if (r == 0) {
						return ((Number) o1[k]).intValue() - ((Number) o2[k]).intValue();
					}
					return r;
				}
			});
			while (rset.next()) {
				String name = rset.getString("INDEX_NAME");
				String column = rset.getString("COLUMN_NAME");
				String ascOrDesc = rset.getString("ASC_OR_DESC");
				boolean unique = !rset.getBoolean("NON_UNIQUE");
				if (isUnique != unique)
					continue;
				if (ascOrDesc != null) {
					column += ascOrDesc.equalsIgnoreCase("A") ? " asc" : " desc";
				}
				if (index == null || !index.getUniqueKey().equalsIgnoreCase(name)) {
					index = new Index();
					index.setUniqueKey(name);
					ls.add(index);
					index.setColumns(column);
				} else {
					index.setColumns(index.getColumns() + "," + column);
				}
				index.setNeedAdded(false);
			}
			return ls;
		} finally {

		}
	}

	/**
	 * 执行一段SQL,以;号为一个SQL的结束符
	 * 
	 * @param connection
	 *            执行SQL的JDBC连接
	 * @param sqlString
	 *            要执行的SQL字串
	 * @param commitCount
	 *            为了增加执行速度，指定运行commitCount条SQL语句后，提交至数据库
	 * @throws SQLException
	 *             如果发生数据库访问错误
	 */
	static public void executeSql(Connection connection, String dbType, String sqlString, int commitCount)
			throws SQLException {
		SQLFileTokenizer tokenizer = new SQLFileTokenizer(sqlString);
		Statement st = connection.createStatement();
		boolean autoCommit = connection.getAutoCommit();
		try {
			connection.setAutoCommit(false);
			int count = 0;
			while (tokenizer.hasMoreElements()) {
				String sql = tokenizer.nextElement().trim();
				if (!sql.isEmpty()) {
					logger.debug(sql);
					try {
						st.execute(processVarSql(dbType, sql));
					} catch (SQLException e) {
						logger.debug("执行失败：", e);
					}
					count++;
					if (count % commitCount == 0)
						connection.commit();
				}
			}
		} finally {
			connection.commit();
			st.close();
			if (autoCommit)
				connection.setAutoCommit(autoCommit);
		}
	}
}
