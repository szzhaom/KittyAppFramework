package kitty.kaf.dao;

import java.sql.SQLException;
import java.util.Collection;

import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.helper.SQLHelper;
import kitty.kaf.util.DateTime;

/**
 * mysql数据库访问代理
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class MysqlDelegation extends DaoDelegation {
	/**
	 * 构建MysqlDelegation
	 * 
	 */
	public MysqlDelegation() {
	}

	@Override
	public String getToDateTimeFormat(DateTime o) {
		return "str_to_date('" + o.format("yyyy-MM-dd HH:mm:ss")
				+ "','%Y-%m-%d %T')";
	}

	@Override
	public String getSequenceNextValue(Object sequence) throws SQLException {
		return null;
	}

	@Override
	public String getAutoIncrementValue() throws SQLException {
		DaoResultSet r = query("select last_insert_id()");
		if (r.first())
			return r.getString(1);
		else
			throw new SQLException("No Records");
	}

	@Override
	public String processSqlVar(String sql) {
		return SQLHelper.processVarSql("mysql", sql);
	}

	@Override
	protected String buildPageSql(Object fields, String fromWhereCause,
			String orderGroupByCause, long firstIndex, int maxResults,
			Object params) throws SQLException {
		StringBuffer sb = new StringBuffer("select ");
		if (fields instanceof Object[])
			sb.append(encodeSqlByFields((Object[]) fields));
		else if (fields instanceof Collection<?>)
			sb.append(encodeSqlByFields((Collection<?>) fields));
		else
			sb.append(fields.toString());
		sb.append(" " + fromWhereCause.trim() + " " + orderGroupByCause);
		if (maxResults > 0) {
			if (firstIndex < 0)
				firstIndex = 0;
			sb.append(" limit " + firstIndex + "," + maxResults);
		}
		return sb.toString();
	}

}
