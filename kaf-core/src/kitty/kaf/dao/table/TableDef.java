package kitty.kaf.dao.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import kitty.kaf.exceptions.CoreException;

/**
 * 表数据结果
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class TableDef {
	String tableName, tableDesp;
	ConcurrentHashMap<String, TableColumnDef> columns = new ConcurrentHashMap<String, TableColumnDef>();
	CopyOnWriteArrayList<TableColumnDef> pkColumns = new CopyOnWriteArrayList<TableColumnDef>();
	String[] secondTables;

	public TableDef() {
		super();
	}

	public TableDef(String tableName, String tableDesp, String[] secondTables) {
		super();
		this.tableName = tableName;
		this.tableDesp = tableDesp;
		this.secondTables = secondTables;
	}

	public String getTableDesp() {
		return tableDesp;
	}

	public void setTableDesp(String tableDesp) {
		this.tableDesp = tableDesp;
	}

	/**
	 * 获取表名
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 设置表名
	 * 
	 * @param v
	 *            表名
	 */
	public void setTableName(String v) {
		this.tableName = v;
	}

	/**
	 * 获取所有列的List
	 */
	public ConcurrentHashMap<String, TableColumnDef> getColumns() {
		return columns;
	}

	public CopyOnWriteArrayList<TableColumnDef> getPkColumns() {
		return pkColumns;
	}

	public void setPkColumns(String... columns) {
		for (String column : columns)
			pkColumns.add(this.columns.get(column));
	}

	TableColumnDef uk = null;

	public synchronized TableColumnDef getUniqueKey() {
		if (uk == null) {
			for (TableColumnDef o : getColumns().values()) {
				if (o.isUiqueKeyField) {
					uk = o;
					break;
				}
			}
			if (uk == null)
				uk = new TableColumnDef();
		}
		return uk == null || uk.getColumnName() == null ? null : uk;
	}

	String fields = null;

	public synchronized String getFields(String alias) {
		if (fields == null) {
			StringBuffer sb = new StringBuffer();
			Collection<TableColumnDef> c = getColumns().values();
			int i = 0;
			for (TableColumnDef d : c) {
				if (i > 0) {
					sb.append(",");
				}
				i++;
				sb.append(alias + "." + d.getColumnName());
			}
			fields = sb.toString();
		}
		return fields;
	}

	public synchronized String getFields() {
		if (fields == null) {
			StringBuffer sb = new StringBuffer();
			Collection<TableColumnDef> c = getColumns().values();
			int i = 0;
			for (TableColumnDef d : c) {
				if (i > 0) {
					sb.append(",");
				}
				i++;
				sb.append(d.getColumnName());
			}
			fields = sb.toString();
		}
		return fields;
	}

	String noPkFields = null;

	public synchronized String getNoPkFields() {
		if (noPkFields == null) {
			StringBuffer sb = new StringBuffer();
			Collection<TableColumnDef> c = getColumns().values();
			int i = 0;
			for (TableColumnDef d : c) {
				if (pkColumns.contains(d))
					continue;
				if (i > 0) {
					sb.append(",");
				}
				i++;
				sb.append(d.getColumnName());
			}
			noPkFields = sb.toString();
		}
		return noPkFields;
	}

	public synchronized String getNoPkFields(String alias) {
		if (noPkFields == null) {
			StringBuffer sb = new StringBuffer();
			Collection<TableColumnDef> c = getColumns().values();
			int i = 0;
			for (TableColumnDef d : c) {
				if (pkColumns.contains(d))
					continue;
				if (i > 0) {
					sb.append(",");
				}
				i++;
				sb.append(alias + "." + d.getColumnName());
			}
			noPkFields = sb.toString();
		}
		return noPkFields;
	}

	String keywordQueryPageSql = null;

	public synchronized DaoSQL getKeywordQueryPageSql(Object keywords) {
		if (keywords == null)
			return null;
		String keyword = keywords.toString().trim();
		if (keyword.length() == 0)
			return null;
		List<String> params = new ArrayList<String>();
		if (keywordQueryPageSql == null) {
			StringBuffer sb = new StringBuffer("(");
			Collection<TableColumnDef> c = getColumns().values();
			int i = 0;
			for (TableColumnDef d : c) {
				if (d.isToStringField() || d.isUiqueKeyField) {
					if (i > 0) {
						sb.append(" or ");
					}
					i++;
					sb.append(d.getColumnName() + " like ?");
					params.add(keyword + "%");
				}
			}
			sb.append(")");
			keywordQueryPageSql = sb.toString();
		} else {
			for (TableColumnDef d : getColumns().values()) {
				if (d.isToStringField() || d.isUiqueKeyField)
					params.add(keyword + "%");
			}
		}
		return params.size() == 0 ? null : new DaoSQL(keywordQueryPageSql, params);
	}

	DaoSQL insertSql = null;

	public DaoSQL getInsertSql() {
		return getInsertSql(tableName);
	}

	public synchronized DaoSQL getInsertSql(String tableName) {
		if (insertSql == null) {
			StringBuffer sb = new StringBuffer("(");
			Collection<TableColumnDef> c = getColumns().values();
			StringBuffer sbValues = new StringBuffer(" values(");
			List<String> params = new ArrayList<String>();
			int i = 0;
			for (TableColumnDef d : c) {
				if (i > 0) {
					sb.append(",");
					sbValues.append(",");
				}
				i++;
				sbValues.append("?");
				sb.append(d.getColumnName());
				params.add(d.getColumnName());
			}
			sb.append(")");
			sbValues.append(")");
			insertSql = new DaoSQL(sb.toString() + sbValues.toString(), params);
		}
		return new DaoSQL("insert into " + tableName + insertSql.getSql(), insertSql.getValueColumns());
	}

	DaoSQL insertNoPkSql = null;

	public DaoSQL getInsertNoPkSql() {
		return getInsertNoPkSql(tableName);
	}

	public synchronized DaoSQL getInsertNoPkSql(String tableName) {
		if (insertNoPkSql == null) {
			StringBuffer sb = new StringBuffer("(");
			Collection<TableColumnDef> c = getColumns().values();
			StringBuffer sbValues = new StringBuffer(" values(");
			List<String> params = new ArrayList<String>();
			int i = 0;
			for (TableColumnDef d : c) {
				if (d.getUpdateMode() == 2 || pkColumns.contains(d))
					continue;
				if (i > 0) {
					sb.append(",");
					sbValues.append(",");
				}
				sbValues.append("?");
				sb.append(d.getColumnName());
				params.add(d.getColumnName());
				i++;
			}
			sb.append(")");
			sbValues.append(")");
			insertNoPkSql = new DaoSQL(sb.toString() + sbValues.toString(), params);
		}
		return new DaoSQL("insert into " + tableName + insertNoPkSql.getSql(), insertNoPkSql.getValueColumns());
	}

	DaoSQL editSql = null;

	public DaoSQL getEditSql() {
		return getEditSql(tableName);
	}

	public synchronized DaoSQL getEditSql(String tableName) {
		if (editSql == null) {
			StringBuffer sb = new StringBuffer(" set ");
			Collection<TableColumnDef> c = getColumns().values();
			List<String> params = new ArrayList<String>();
			int i = 0;
			for (TableColumnDef d : c) {
				if (d.updateMode == 1 || pkColumns.contains(c))
					continue;
				if (i > 0) {
					sb.append(",");
				}
				i++;
				sb.append(d.getColumnName() + "=?");
				params.add(d.getColumnName());
			}
			sb.append(" where " + pkColumns.get(0).getColumnName() + "=?");
			params.add(pkColumns.get(0).getColumnName());
			editSql = new DaoSQL(sb.toString(), params);
		}
		return new DaoSQL("update " + tableName + editSql.getSql(), editSql.getValueColumns());
	}

	DaoSQL deleteSql = null;

	public DaoSQL getDeleteSql() {
		return getDeleteSql(tableName);
	}

	public synchronized DaoSQL getDeleteSql(String tableName) {
		if (deleteSql == null) {
			StringBuffer sb = new StringBuffer(" set is_deleted=1,last_modified_time=${now}");
			deleteSql = new DaoSQL(sb.toString(), null);
		}
		return new DaoSQL("update " + tableName + deleteSql.getSql(), deleteSql.getValueColumns());
	}

	public String getFindByIdSql(int paramsCount) {
		StringBuffer sb = new StringBuffer("select ");
		Collection<TableColumnDef> c = getColumns().values();
		int i = 0;
		for (TableColumnDef d : c) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(d.getColumnName());
			i++;
		}
		sb.append(" from " + getTableName() + " where is_deleted=0 and (");
		for (i = 0; i < paramsCount; i++) {
			if (i > 0)
				sb.append(" or ");
			sb.append(pkColumns.get(0).columnName + "=?");
		}
		sb.append(")");
		return sb.toString();
	}

	public String getFindByNameSql(int paramsCount) {
		return getFindByNameSql(getUniqueKey().columnName, paramsCount);
	}

	public String getFindByNameSql(String uniqueColumnName, int paramsCount) {
		StringBuffer sb = new StringBuffer("select ");
		Collection<TableColumnDef> c = getColumns().values();
		int i = 0;
		for (TableColumnDef d : c) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(d.getColumnName());
			i++;
		}
		sb.append(" from " + getTableName() + " where is_deleted=0 and (");
		for (i = 0; i < paramsCount; i++) {
			if (i > 0)
				sb.append(" or ");
			sb.append(uniqueColumnName + "=?");
		}
		sb.append(")");
		return sb.toString();
	}

	DaoSQL findByIdSql = null;

	public synchronized DaoSQL getFindByIdSql() {
		if (findByIdSql == null) {
			StringBuffer sb = new StringBuffer("select ");
			Collection<TableColumnDef> c = getColumns().values();
			List<String> params = new ArrayList<String>();
			int i = 0;
			for (TableColumnDef d : c) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(d.getColumnName());
				i++;
			}
			sb.append(" from " + getTableName() + " where " + pkColumns.get(0).getColumnName() + "=? and is_deleted=0");
			params.add(pkColumns.get(0).getColumnName());
			findByIdSql = new DaoSQL(sb.toString(), params);
		}
		return findByIdSql;
	}

	DaoSQL findByUKSql = null;

	public synchronized DaoSQL getFindByUKSql() {
		if (findByUKSql == null && getUniqueKey() != null) {
			StringBuffer sb = new StringBuffer("select ");
			Collection<TableColumnDef> c = getColumns().values();
			List<String> params = new ArrayList<String>();
			int i = 0;
			for (TableColumnDef d : c) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(d.getColumnName());
				i++;
			}
			sb.append(" from " + getTableName() + " where " + uk.columnName + "=? and is_deleted=0");
			params.add(uk.columnName);
			findByUKSql = new DaoSQL(sb.toString(), params);
		}
		return findByUKSql;
	}

	public Byte test(String columnName, Byte v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testByte(v, isCreate);
		return v;
	}

	public Long test(String columnName, Long v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testLong(v, isCreate);
		return v;
	}

	public Short test(String columnName, Short v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testShort(v, isCreate);
		return v;
	}

	public Boolean test(String columnName, Boolean v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testBoolean(v, isCreate);
		return v;
	}

	public Integer test(String columnName, Integer v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testInt(v, isCreate);
		return v;
	}

	public Float test(String columnName, Float v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testFloat(v, isCreate);
		return v;
	}

	public Double test(String columnName, Double v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testDouble(v, isCreate);
		return v;
	}

	public Date test(String columnName, Date v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testDate(v, isCreate);
		return v;
	}

	public String test(String columnName, String v, boolean isCreate) {
		TableColumnDef def = columns.get(columnName);
		if (def == null)
			throw new CoreException("列名不存在");
		else
			def.testString(v, isCreate);
		return v;
	}

	public String[] getSecondTables() {
		return secondTables;
	}

}
