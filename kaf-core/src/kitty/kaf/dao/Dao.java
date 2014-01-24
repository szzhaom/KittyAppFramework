package kitty.kaf.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import kitty.kaf.cache.CacheValueList;
import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.dao.source.DaoSource;
import kitty.kaf.dao.source.DaoSourceFactory;
import kitty.kaf.dao.table.DaoSQL;
import kitty.kaf.dao.table.IdTableObject;
import kitty.kaf.dao.table.TableColumnDef;
import kitty.kaf.dao.table.TableDef;
import kitty.kaf.dao.table.TableObject;
import kitty.kaf.io.KeyValue;
import kitty.kaf.util.DateTime;

/**
 * 数据库访问对象
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class Dao {
	DaoSource source;
	boolean isMaster;
	DaoDelegation delegation;

	public static void main(String[] args) throws SQLException {
		Dao dao = new Dao("mysql");
		DaoResultSet rset = dao.query("select * from t_user");
		while (rset.next()) {
			System.out.println(rset.getString(1));
		}
		dao.close();
	}

	/**
	 * 构建Dao
	 * 
	 * @param source
	 *            数据库访问源
	 */
	public Dao(DaoSource source) {
		this.source = source;
	}

	/**
	 * 构建Dao
	 * 
	 * @param name
	 *            数据库名字
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public Dao(String name) throws SQLException {
		this.source = DaoSourceFactory.getDaoSource(this, name);
	}

	/**
	 * 获取数据源
	 */
	public DaoSource getSource() {
		return source;
	}

	/**
	 * 关闭数据库访问对象，释放数据库资源
	 */
	public void close() {
		if (source != null) {
			source.close();
			source = null;
		}
	}

	/**
	 * 获取当前的数据库代理
	 */
	public DaoDelegation getDelegation() throws SQLException {
		if (delegation == null) {
			if (source.getType().equals("mysql")) {
				delegation = new MysqlDelegation();
			} else
				throw new SQLException("Unsupported database[" + source.getType() + "]");
			delegation.setConnection(isMaster ? source.getMaster() : source.getSlave());
		}
		return delegation;
	}

	/**
	 * 开始更新数据库。此条语句执行之后，全部使用主数据库服务器
	 * 
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public void beginUpdateDatabase() throws SQLException {
		isMaster = true;
		if (delegation != null)
			delegation.setConnection(source.getMaster());
	}

	/**
	 * 停止更新数据库。此条语句执行之后，所有操作均采用从数据库服务器
	 * 
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public void endUpdateDatabase() throws SQLException {
		isMaster = false;
		if (delegation != null)
			delegation.setConnection(source.getSlave());
	}

	/**
	 * 提交
	 * 
	 * @throws SQLException
	 *             当数据库访问发生错误时
	 */
	public void commit() throws SQLException {
		source.getMaster().commit();
	}

	/**
	 * 回滚
	 * 
	 * @throws SQLException
	 *             当数据库访问发生错误时
	 */
	public void rollback() throws SQLException {
		source.getMaster().rollback();
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
		source.getMaster().setAutoCommit(autoCommit);
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
	public DaoResultSet query(int maxResults, String sql, Collection<?> params) throws SQLException {
		return getDelegation().query(sql, maxResults, params);
	}

	/**
	 * 查询SQL，无参数，并根据maxResults取回指定的记录数，如果maxResults为0，取回全部记录数
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
	public DaoResultSet query(int maxResults, String sql, Object... params) throws SQLException {
		if (params == null || params.length == 0)
			return getDelegation().query(sql, maxResults);
		else {
			List<Object> ls = new ArrayList<Object>();
			for (Object o : params)
				ls.add(o);
			return getDelegation().query(sql, maxResults, ls);
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
	public DaoResultSet query(String sql, Object... params) throws SQLException {
		return query(0, sql, params);
	}

	/**
	 * 查询SQL，返回具体的对象列表
	 * 
	 * @param clazz
	 *            生成的对象的类定义
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
	 * @return 查询到的结果
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public <E extends TableObject> List<E> query(Class<E> clazz, String fromWhereCause, String orderGroupByCause,
			int maxResults, Collection<?> params) throws SQLException {
		try {
			E o = clazz.newInstance();
			String sql = getDelegation().buildSql(o.getTableDef().getColumns().values(), fromWhereCause,
					orderGroupByCause, maxResults, params);
			DaoResultSet rset = getDelegation().query(sql, maxResults, params);
			List<E> ret = new ArrayList<E>();
			while (rset.next()) {
				o = clazz.newInstance();
				o.readFromDb(rset);
				ret.add(o);
			}
			return ret;
		} catch (Throwable e) {
			throw new SQLException(e);
		}
	}

	/**
	 * 查询SQL，无参数，返回具体的对象列表
	 * 
	 * @param clazz
	 *            生成的对象的类定义
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
	 * @return 查询到的结果
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public <E extends TableObject> List<E> query(Class<E> clazz, String fromWhereCause, String orderGroupByCause,
			int maxResults, Object... params) throws SQLException {
		try {
			E o = clazz.newInstance();
			String sql = getDelegation().buildSql(o.getTableDef().getColumns().values(), fromWhereCause,
					orderGroupByCause, maxResults, params);
			DaoResultSet rset = getDelegation().query(sql, maxResults, params);
			List<E> ret = new ArrayList<E>();
			while (rset.next()) {
				o = clazz.newInstance();
				o.readFromDb(rset);
				ret.add(o);
			}
			return ret;
		} catch (Throwable e) {
			throw new SQLException(e);
		}
	}

	/**
	 * 查询单条记录，并转换成Table对象
	 * 
	 * @param clazz
	 *            对象生成的类
	 * @param sql
	 *            查询SQL
	 * @param params
	 *            查询参数
	 * @return 查到的对象
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public <E extends TableObject> E querySingle(Class<E> clazz, String sql, Collection<?> params) throws SQLException {
		DaoResultSet rset = query(1, sql, params);
		if (rset.next()) {
			E o;
			try {
				o = clazz.newInstance();
			} catch (Throwable e) {
				throw new SQLException(e);
			}
			o.readFromDb(rset);
			return o;
		} else
			return null;
	}

	/**
	 * 查询单条记录，并转换成Table对象
	 * 
	 * @param clazz
	 *            对象生成的类
	 * @param sql
	 *            查询SQL
	 * @param params
	 *            查询参数
	 * @return 查到的对象
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public <E extends TableObject> E querySingle(Class<E> clazz, String sql, Object... params) throws SQLException {
		DaoResultSet rset = query(1, sql, params);
		if (rset.next()) {
			E o;
			try {
				o = clazz.newInstance();
			} catch (Throwable e) {
				throw new SQLException(e);
			}
			o.readFromDb(rset);
			return o;
		} else
			return null;
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
	public boolean execute(String sql, Collection<?> params) throws SQLException {
		return getDelegation().execute(sql, params);
	}

	/**
	 * 执行sql，无参数
	 * 
	 * @param sql
	 *            SQL字串
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public boolean execute(String sql, Object... params) throws SQLException {
		return getDelegation().execute(sql, params);
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
	public DaoResultSet queryCall(int maxResults, String sql, Collection<?> params) throws SQLException {
		return getDelegation().queryCall(sql, maxResults, params);
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
	public DaoResultSet queryCall(int maxResults, String sql, Object... params) throws SQLException {
		return getDelegation().queryCall(sql, maxResults, params);
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
	public DaoResultSet queryCall(String sql, Object... params) throws SQLException {
		return queryCall(0, sql, params);
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
	public boolean executeCall(String sql, Collection<?> params) throws SQLException {
		return getDelegation().executeCall(sql, params);
	}

	/**
	 * 执行存储过程sql，无参数
	 * 
	 * @param sql
	 *            存储过程SQL字串
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public boolean executeCall(String sql, Object... params) throws SQLException {
		return getDelegation().executeCall(sql, params);
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
			int maxResults, Collection<?> params) throws SQLException {
		return getDelegation().queryPage(fields, fromWhereCause, orderGroupByCause, firstIndex, maxResults, params);
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
			int maxResults, Object... params) throws SQLException {
		return getDelegation().queryPage(fields, fromWhereCause, orderGroupByCause, firstIndex, maxResults, params);
	}

	/**
	 * 分页查询，无参数
	 * 
	 * @param clazz
	 *            生成的对象的类定义
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
	 * @return 查询到的结果
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public <E extends TableObject> KeyValue<Integer, List<E>> queryPage(Class<E> clazz, String fromWhereCause,
			String orderGroupByCause, long firstIndex, int maxResults, Collection<?> params) throws SQLException {
		try {
			E o = clazz.newInstance();
			DaoResultSet rset = getDelegation().queryPage(o.getTableDef().getColumns().values(), fromWhereCause,
					orderGroupByCause, firstIndex, maxResults, params);
			KeyValue<Integer, List<E>> ret = new KeyValue<Integer, List<E>>(rset.getAllRecordCount(),
					new ArrayList<E>());
			while (rset.next()) {
				o = clazz.newInstance();
				o.readFromDb(rset);
				ret.getValue().add(o);
			}
			return ret;
		} catch (Throwable e) {
			throw new SQLException(e);
		}
	}

	/**
	 * 分页查询，无参数
	 * 
	 * @param clazz
	 *            生成的对象的类定义
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
	 * @return 查询到的结果
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public <E extends TableObject> KeyValue<Integer, List<E>> queryPage(Class<E> clazz, String fromWhereCause,
			String orderGroupByCause, long firstIndex, int maxResults, Object... params) throws SQLException {
		try {
			E o = clazz.newInstance();
			DaoResultSet rset = getDelegation().queryPage(o.getTableDef().getColumns().values(), fromWhereCause,
					orderGroupByCause, firstIndex, maxResults, params);
			KeyValue<Integer, List<E>> ret = new KeyValue<Integer, List<E>>(rset.getAllRecordCount(),
					new ArrayList<E>());
			while (rset.next()) {
				o = clazz.newInstance();
				o.readFromDb(rset);
				ret.getValue().add(o);
			}
			return ret;
		} catch (Throwable e) {
			throw new SQLException(e);
		}
	}

	/**
	 * 关键字查询
	 * 
	 * @param clazz
	 * @param fromWhereCause
	 * @param orderGroupByCause
	 * @param firstIndex
	 * @param maxResults
	 * @param keyword
	 * @return
	 * @throws SQLException
	 */
	public <E extends TableObject> KeyValue<Integer, List<E>> keywordQuery(Class<E> clazz, String fromWhereCause,
			String orderGroupByCause, long firstIndex, int maxResults, Object keyword, Object... params)
			throws SQLException {
		try {
			E o = clazz.newInstance();
			DaoSQL sql = o.getTableDef().getKeywordQueryPageSql(keyword);
			if (sql == null)
				return queryPage(clazz, fromWhereCause, orderGroupByCause, firstIndex, maxResults, params);
			List<Object> s = new ArrayList<Object>();
			if (fromWhereCause.contains(" where "))
				fromWhereCause += " and ";
			else
				fromWhereCause += " where ";
			fromWhereCause += sql.getSql();
			if (params != null)
				for (Object p : params)
					s.add(p);
			s.addAll(sql.getValueColumns());
			DaoResultSet rset = getDelegation().queryPage(o.getTableDef().getColumns().values(), fromWhereCause,
					orderGroupByCause, firstIndex, maxResults, s);
			KeyValue<Integer, List<E>> ret = new KeyValue<Integer, List<E>>(rset.getAllRecordCount(),
					new ArrayList<E>());
			while (rset.next()) {
				o = clazz.newInstance();
				o.readFromDb(rset);
				ret.getValue().add(o);
			}
			return ret;
		} catch (Throwable e) {
			throw new SQLException(e);
		}
	}

	/**
	 * 根据ID查询数据对象
	 * 
	 * @param clazz
	 *            要生成的数据对象类
	 * @param id
	 *            数据对象的ID
	 * @return 查到的数据对象
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public <K, E extends IdTableObject<K>> E findById(Class<E> clazz, K id) throws SQLException {
		E o;
		try {
			o = clazz.newInstance();
		} catch (Throwable e) {
			throw new SQLException(e);
		}
		o.setId(id);
		DaoSQL sql = o.getTableDef().getFindByIdSql();
		DaoResultSet rset = query(1, sql.getSql(), sql.getParams(o));
		if (rset.next()) {
			o.readFromDb(rset);
			return o;
		} else
			return null;
	}

	/**
	 * 根据ID查询数据对象列表
	 * 
	 * @param clazz
	 *            要生成的数据对象类
	 * @param id
	 *            数据对象的ID
	 * @return 查到的数据对象
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public <K, E extends IdTableObject<K>> List<E> findByIdList(Class<E> clazz, List<K> idList) throws SQLException {
		if (idList == null || idList.size() == 0)
			return new ArrayList<E>();
		E o;
		try {
			o = clazz.newInstance();
		} catch (Throwable e) {
			throw new SQLException(e);
		}
		DaoResultSet rset = query(idList.size(), o.getTableDef().getFindByIdSql(idList.size()), idList);
		List<E> ls = new ArrayList<E>();
		while (rset.next()) {
			try {
				o = clazz.newInstance();
			} catch (Throwable e) {
				throw new SQLException(e);
			}
			o.readFromDb(rset);
			ls.add(o);
		}
		return ls;
	}

	/**
	 * 根据名字列表查询数据对象列表
	 * 
	 * @param clazz
	 *            要生成的数据对象类
	 * @param id
	 *            数据对象的ID
	 * @return 查到的数据对象
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public <K, E extends IdTableObject<K>> List<E> findByNameList(Class<E> clazz, List<String> keyList)
			throws SQLException {
		if (keyList == null || keyList.size() == 0)
			return new ArrayList<E>();
		E o;
		try {
			o = clazz.newInstance();
		} catch (Throwable e) {
			throw new SQLException(e);
		}
		DaoResultSet rset = query(keyList.size(), o.getTableDef().getFindByNameSql(keyList.size()), keyList);
		List<E> ls = new ArrayList<E>();
		while (rset.next()) {
			try {
				o = clazz.newInstance();
			} catch (Throwable e) {
				throw new SQLException(e);
			}
			o.readFromDb(rset);
			ls.add(o);
		}
		return ls;
	}

	/**
	 * 根据uk查询数据对象
	 * 
	 * @param clazz
	 *            要生成的数据对象类
	 * @param uk
	 *            数据对象的uk
	 * @return 查到的数据对象
	 * @throws SQLException
	 *             当数据库访问错误时
	 */
	public <E extends IdTableObject<?>> E findByUniqueKey(Class<E> clazz, String uk) throws SQLException {
		E o;
		try {
			o = clazz.newInstance();
		} catch (Throwable e) {
			throw new SQLException(e);
		}
		DaoSQL sql = o.getTableDef().getFindByUKSql();
		List<String> ls = new ArrayList<String>();
		ls.add(uk);
		DaoResultSet rset = query(1, sql.getSql(), ls);
		if (rset.next()) {
			o.readFromDb(rset);
			return o;
		} else
			return null;
	}

	/**
	 * 插入一个数据
	 * 
	 * @param o
	 *            数据对象
	 * @return 插入后的数据对象
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public <E extends IdTableObject<?>> E insert(E o) throws SQLException {
		beginUpdateDatabase();
		try {
			TableColumnDef uk = o.getTableDef().getUniqueKey();
			if (uk != null) {
				DaoSQL sql = o.getTableDef().getFindByUKSql();
				DaoResultSet rset = query(1, sql.getSql(), sql.getParams(o));
				if (rset.next())
					throw new SQLException(uk.getColumnDesp() + "[" + o.getByColumn(uk.getColumnName()) + "]已经被占用");
			}
			if (o.getId() == null) { // 从序列取自增
				String seq = o.getTableDef().getPkColumns().get(0).getSequence();
				String id = getDelegation().getSequenceNextValue(seq);
				if (id != null)
					o.setIdString(id);
			}
			DaoSQL sql = o.getId() == null ? o.getTableDef().getInsertNoPkSql() : o.getTableDef().getInsertSql();
			if (o.getId() == null) { // 通过JDBC取自增
				String id = getDelegation().executeAutoGenKeys(sql.getSql(), sql.getParams(o));
				if (id != null)
					o.setIdString(id);
			} else
				getDelegation().execute(sql.getSql(), sql.getParams(o));
			if (o.getId() == null) {// 通过特定接口取自增
				o.setIdString(getDelegation().getAutoIncrementValue());
			}
			return o;
		} finally {
			endUpdateDatabase();
		}
	}

	/**
	 * 编辑一个数据
	 * 
	 * @param o
	 *            数据对象
	 * @return 要编辑的数据对象
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public <E extends IdTableObject<?>> E edit(E o) throws SQLException {
		DaoSQL sql = o.getTableDef().getFindByIdSql();
		DaoResultSet rset = query(1, sql.getSql(), sql.getParams(o));
		if (!rset.next())
			throw new SQLException("找不到要编辑的" + o.getTableDef().getTableDesp() + "[id=" + o.getIdString() + "]");
		beginUpdateDatabase();
		try {
			TableColumnDef uk = o.getTableDef().getUniqueKey();
			if (uk != null) {
				String pk = o.getTableDef().getPkColumns().get(0).getColumnName();
				sql = o.getTableDef().getFindByUKSql();
				List<Object> params = sql.getParams(o);
				params.add(o.getByColumn(pk));
				rset = query(1, sql.getSql() + " and " + pk + "!=?", params);
				if (rset.next())
					throw new SQLException(uk.getColumnDesp() + "[" + o.getByColumn(uk.getColumnName()) + "]已经被占用");
			}
			sql = o.getTableDef().getEditSql();
			execute(sql.getSql(), sql.getParams(o));
			return o;
		} finally {
			endUpdateDatabase();
		}
	}

	/**
	 * 删除数据
	 * 
	 * @param o
	 *            数据对象
	 * @return 要删除的ID列表
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public void delete(TableDef def, Collection<?> idList) throws SQLException {
		if (idList != null & idList.size() > 0) {
			beginUpdateDatabase();
			try {
				DaoSQL sql = def.getDeleteSql();
				String n = def.getPkColumns().get(0).getColumnName();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < idList.size(); i++) {
					if (i > 0)
						sb.append(" or ");
					sb.append(n + "=?");
				}
				execute(sql.getSql() + " where " + sb.toString(), idList);
			} finally {
				endUpdateDatabase();
			}
		}
	}

	/**
	 * 查找最近更新的记录
	 * 
	 * @param clazz
	 *            返回的对象类
	 * @param whereCause
	 * @param groupOrderCause
	 * @param firstIndex
	 *            起始记录数，-1表示返回总记录数
	 * @param maxResults
	 *            本次最大返回的记录数
	 * @param groupOrder
	 * @param lastModified
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <K extends Serializable, V extends IdTableObject<K>> CacheValueList<K, V> queryLatest(Class<V> clazz,
			String from, String whereCause, String groupOrderCause, long firstIndex, int maxResults, Date lastModified,
			Collection<?> params) throws SQLException {
		if (whereCause.length() == 0)
			whereCause = " where ";
		else
			whereCause += " and ";
		if (lastModified == null)
			whereCause += "a.is_deleted=0";
		else
			whereCause += "a.last_modified_time>" + getDelegation().getToDateTimeFormat(new DateTime(lastModified));
		CacheValueList<K, V> r = new CacheValueList<K, V>();
		KeyValue<Integer, List<V>> kv = queryPage(clazz, from + " " + whereCause, " " + groupOrderCause, firstIndex,
				maxResults, params);
		r.setTotalCount(kv.getKey());
		for (V v : kv.getValue()) {
			if (v.isDeleted())
				r.getDeletedList().add(v);
			else
				r.getModifiedList().add(v);
		}
		return r;
	}

	public <K extends Serializable, V extends IdTableObject<K>> CacheValueList<K, V> queryLatest(Class<V> clazz,
			String from, String whereCause, String groupOrderCause, long firstIndex, int maxResults, Date lastModified)
			throws SQLException {
		if (whereCause.length() == 0)
			whereCause = " where ";
		else
			whereCause += " and ";
		if (lastModified == null)
			whereCause += "a.is_deleted=0";
		else
			whereCause += "a.last_modified_time>" + getDelegation().getToDateTimeFormat(new DateTime(lastModified));
		CacheValueList<K, V> r = new CacheValueList<K, V>();
		KeyValue<Integer, List<V>> kv = queryPage(clazz, "from " + from + " " + whereCause, " " + groupOrderCause,
				firstIndex, maxResults);
		r.setTotalCount(kv.getKey());
		for (V v : kv.getValue()) {
			if (v.isDeleted())
				r.getDeletedList().add(v);
			else
				r.getModifiedList().add(v);
		}
		return r;
	}

	/**
	 * 获取数据的最新更新时间，采用主服务器
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public <K extends Serializable, V extends IdTableObject<K>> Date getLastModified(Class<V> clazz, Object id)
			throws SQLException {
		if (id == null)
			return null;
		if (id instanceof List<?>) {
			List<?> list = (List<?>) id;
			if (list.size() == 0)
				return null;
			id = list.get(list.size() - 1);
			if (id == null)
				return null;
		} else if (id instanceof Object[]) {
			Object[] list = (Object[]) id;
			if (list.length == 0)
				return null;
			id = list[list.length - 1];
			if (id == null)
				return null;
		}
		V o;
		try {
			o = clazz.newInstance();
		} catch (Throwable e) {
			throw new SQLException(e);
		}
		beginUpdateDatabase();
		try {
			DaoResultSet rset = query("select last_modified_time from " + o.getTableDef().getTableName() + " where "
					+ o.getTableDef().getPkColumns().get(0).getColumnName() + "=?", id);
			if (rset.next())
				return rset.getDate(1);
			else
				return new Date();
		} finally {
			endUpdateDatabase();
		}
	}
}
