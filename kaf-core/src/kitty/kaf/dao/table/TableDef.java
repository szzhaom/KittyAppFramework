package kitty.kaf.dao.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 表数据结果
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class TableDef {
	String tableName;
	ConcurrentHashMap<String, TableColumnDef> columns = new ConcurrentHashMap<String, TableColumnDef>();
	CopyOnWriteArrayList<TableColumnDef> pkColumns = new CopyOnWriteArrayList<TableColumnDef>();

	public TableDef() {
		super();
	}

	public TableDef(String tableName) {
		super();
		this.tableName = tableName;
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

	public synchronized DaoSQL getInsertSql() {
		if (insertSql == null) {
			StringBuffer sb = new StringBuffer("insert into " + getTableName() + "(");
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
		return insertSql;
	}

	DaoSQL insertNoPkSql = null;

	public synchronized DaoSQL getInsertNoPkSql() {
		if (insertNoPkSql == null) {
			StringBuffer sb = new StringBuffer("insert into " + getTableName() + "(");
			Collection<TableColumnDef> c = getColumns().values();
			StringBuffer sbValues = new StringBuffer(" values(");
			List<String> params = new ArrayList<String>();
			int i = 0;
			for (TableColumnDef d : c) {
				if (pkColumns.contains(d))
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
		return insertNoPkSql;
	}

	DaoSQL editSql = null;

	public synchronized DaoSQL getEditSql() {
		if (editSql == null) {
			StringBuffer sb = new StringBuffer("update " + getTableName() + " set ");
			Collection<TableColumnDef> c = getColumns().values();
			List<String> params = new ArrayList<String>();
			int i = 0;
			for (TableColumnDef d : c) {
				if (!d.isEditEnabled() || pkColumns.contains(c))
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
		return editSql;
	}

	DaoSQL deleteSql = null;

	public synchronized DaoSQL getDeleteSql() {
		if (deleteSql == null) {
			StringBuffer sb = new StringBuffer("update " + getTableName()
					+ " set is_deleted=1,last_modified_time=${now}");
			deleteSql = new DaoSQL(sb.toString(), null);
		}
		return deleteSql;
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
}
