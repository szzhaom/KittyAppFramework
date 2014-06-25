package kitty.kaf.dao.tools;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kitty.kaf.dao.tools.cg.PackageDef;
import kitty.kaf.dao.tools.cg.jsp.TableJspConfig;
import kitty.kaf.helper.SQLHelper;
import kitty.kaf.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Table extends BaseConfigDef {
	List<Column> columns = new ArrayList<Column>();
	List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
	List<ForeignKey> foreignGenVarLists = new ArrayList<ForeignKey>();
	List<ForeignKey> foreignGenVars = new ArrayList<ForeignKey>();
	Index pk;
	List<Index> indexes = new ArrayList<Index>();
	List<Index> uniques = new ArrayList<Index>();
	Partition partition;
	Database database;
	String name;
	String packageName, javaClassName;
	String desp, ejbNamePrefix;
	String cacheConfig;
	String nullId;
	String implementsStr;
	String localCache;
	TradeConfig tradeConfig;
	TableData tableData;
	RightConfig rightConfig;
	static Logger logger = Logger.getLogger(Table.class);
	TableJspConfig jspConfig;
	int orderIndex;
	boolean isTreeCache;
	String treeCacheClass;
	boolean isMainTable;
	List<Table> secondTables = new ArrayList<Table>();
	/**
	 * 表的结束SQL字符串
	 */
	String endString;

	public Table(Element el, Database db, int partitionIndex) throws SQLException {
		this.database = db;
		this.daoSource = db.daoSource;
		isMainTable = true;
		name = el.getAttribute("name");
		if (partitionIndex > -1) {
			NodeList ls = el.getElementsByTagName("partition");
			Element e = (Element) ls.item(partitionIndex);
			partition = new Partition(e, this);
			isMainTable = partition.tableName == null || partition.tableName.equals(name);
			if (!isMainTable)
				name = partition.tableName;
		}
		desp = el.getAttribute("desp");
		if (isMainTable) {
			treeCacheClass = el.getAttribute("treeCacheClass");
			packageName = el.getAttribute("package");
			javaClassName = el.getAttribute("classname");
			ejbNamePrefix = el.getAttribute("ejbNamePrefix");
			cacheConfig = el.hasAttribute("cache-config") ? el.getAttribute("cache-config") : null;
			if (el.hasAttribute("localcache"))
				localCache = el.getAttribute("localcache");
			if (el.hasAttribute("null-id"))
				nullId = el.getAttribute("null-id");
			if (el.hasAttribute("implements"))
				implementsStr = el.getAttribute("implements");
			if (el.hasAttribute("istreecache"))
				isTreeCache = el.getAttribute("istreecache").equalsIgnoreCase("true");
		}
		NodeList ls = el.getElementsByTagName("column");
		for (int i = 0; i < ls.getLength(); i++) {
			Element e = (Element) ls.item(i);
			Column c = new Column(e, this, this.getDatabase().daoSource);
			if (columns.contains(c) || db.getStandardColumns().contains(c)) {
				logger.debug(this + ": 字段重复[" + c.getName() + "]");
			} else
				columns.add(c);
		}
		for (Column c : db.getStandardColumns()) {
			Column o = c.clone();
			o.setTable(this);
			columns.add(o);
		}
		ls = el.getElementsByTagName("primary-key");
		if (ls.getLength() > 0)
			pk = new Index((Element) ls.item(0), this);
		ls = el.getElementsByTagName("unique");
		for (int i = 0; i < ls.getLength(); i++) {
			Element e = (Element) ls.item(i);
			uniques.add(new Index(e, this));
		}
		ls = el.getElementsByTagName("index");
		for (int i = 0; i < ls.getLength(); i++) {
			Element e = (Element) ls.item(i);
			indexes.add(new Index(e, this));
		}
		ls = el.getElementsByTagName("other_config");
		for (int i = 0; i < ls.getLength(); i++) {
			Element e = (Element) ls.item(i);
			NodeList ls1 = e.getElementsByTagName("endstring");
			for (int j = 0; j < ls1.getLength(); j++) {
				e = (Element) ls1.item(j);
				if (e.getAttribute("type").equalsIgnoreCase(daoSource.getType())) {
					endString = e.getAttribute("value");
				}
			}
		}
		ls = el.getElementsByTagName("foreign-key");
		for (int i = 0; i < ls.getLength(); i++) {
			foreignKeys.add(new ForeignKey((Element) ls.item(i), this, daoSource));
		}
		ls = el.getElementsByTagName("trade_config");
		if (ls.getLength() > 0) {
			tradeConfig = new TradeConfig((Element) ls.item(0));
		}
		ls = el.getElementsByTagName("right_config");
		if (ls.getLength() > 0) {
			rightConfig = new RightConfig(getName().substring(2), (Element) ls.item(0));
		} else
			rightConfig = new RightConfig(getName().substring(2));
		ls = el.getElementsByTagName("data");
		if (ls.getLength() > 0)
			tableData = new TableData(this, (Element) ls.item(0));
		jspConfig = new TableJspConfig(this, el);
		getTableInfoFromDatabase();
	}

	public String getTreeCacheClass() {
		return treeCacheClass;
	}

	public void setTreeCacheClass(String treeCacheClass) {
		this.treeCacheClass = treeCacheClass;
	}

	public TableJspConfig getJspConfig() {
		return jspConfig;
	}

	public TradeConfig getTradeConfig() {
		return tradeConfig;
	}

	public void setTradeConfig(TradeConfig tradeConfig) {
		this.tradeConfig = tradeConfig;
	}

	public String getLocalCache() {
		return localCache;
	}

	public void setLocalCache(String localCache) {
		this.localCache = localCache;
	}

	public String getImplementsStr() {
		return implementsStr;
	}

	public void setImplementsStr(String implementsStr) {
		this.implementsStr = implementsStr;
	}

	public String getNullId() {
		return nullId;
	}

	public void setNullId(String nullId) {
		this.nullId = nullId;
	}

	public String getEjbName() {
		return ejbNamePrefix == null ? "k" : ejbNamePrefix + getJavaClassName();
	}

	public String getEjbNamePrefix() {
		return ejbNamePrefix;
	}

	public void setEjbNamePrefix(String ejbNamePrefix) {
		this.ejbNamePrefix = ejbNamePrefix;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	public void setJavaClassName(String className) {
		this.javaClassName = className;
	}

	public Partition getPartition() {
		return partition;
	}

	public void setPartition(Partition partition) {
		this.partition = partition;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getFullBeanClassName() {
		PackageDef def = database.generator.getPackageDef(packageName);
		return def.getBeanPackageName() + "." + getJavaClassName();
	}

	public String getFullDaoHelperClassName() {
		PackageDef def = database.generator.getPackageDef(packageName);
		return def.getEjbPackageName() + ".dao." + getJavaClassName() + "DaoHelper";
	}

	public String getHelperClassName() {
		return getJavaClassName() + "Helper";
	}

	public PackageDef getPackageDef() {
		return database.generator.getPackageDef(packageName);
	}

	public String getFullHelperClassName() {
		PackageDef def = database.generator.getPackageDef(packageName);
		return def.getInfPackageName() + "." + getHelperClassName();
	}

	public String getFullBeanRemoteClassName() {
		PackageDef def = database.generator.getPackageDef(packageName);
		return def.getInfPackageName() + "." + getJavaClassName() + "BeanRemote";
	}

	public Index findIndexByName(String name) {
		for (Index o : indexes) {
			if (o.getUniqueKey().equalsIgnoreCase(name))
				return o;
		}
		return null;
	}

	public Index findUniqueByName(String name) {
		for (Index o : uniques) {
			if (o.getUniqueKey().equalsIgnoreCase(name))
				return o;
		}
		return null;
	}

	public Column findColumnByName(String name) {
		for (Column o : columns) {
			if (o.getName().equalsIgnoreCase(name))
				return o;
		}
		return null;
	}

	public Column getUniqueKeyColumn() {
		for (Column o : columns) {
			if (o.isUniqueKeyField())
				return o;
		}
		return null;
	}

	public Column getDespColumn() {
		for (Column o : columns) {
			if (o.isToStringField())
				return o;
		}
		throw new NullPointerException("Table[" + name + "]无描述字段");
	}

	public void getTableInfoFromDatabase() throws SQLException {
		Connection con = database.daoSource.getMaster();
		this.isNeedAdded = !SQLHelper.tableExists(con, name);
		if (this.isNeedAdded)
			return;
		List<Column> cs = SQLHelper.getColumns(con, name);
		for (Column c : cs) {
			int index = columns.indexOf(c);
			if (index > -1) {
				Column o = columns.get(index);
				o.setNeedAdded(false);
				o.setNeedModified(o.isModified(c));
			} else {
				c.setNeedDeleted(true);
				c.setNeedAdded(false);
				c.setTable(this);
				columns.add(c);
			}
		}
		Index p = SQLHelper.getPrimaryKey(con, name);
		if (p != null) {
			if (pk == null) {
				pk = new Index(this, "");
				pk.setNeedDeleted(true);
			} else {
				pk.setNeedAdded(false);
				pk.setNeedModified(pk.isModified(p));
			}
		}
		List<Index> ls = SQLHelper.getIndexes(con, name, true);
		for (Index o : ls) {
			if (p != null && o.getUniqueKey().equalsIgnoreCase(p.getUniqueKey()))
				continue;
			Index index = findUniqueByName(o.getUniqueKey());
			if (index != null) {
				index.setNeedAdded(false);
				index.setNeedModified(index.isModified(o));
			} else {
				o.setTable(this);
				o.setNeedDeleted(true);
				uniques.add(o);
			}
		}
		ls = SQLHelper.getIndexes(con, name, false);
		for (Index o : ls) {
			Index index = findIndexByName(o.getUniqueKey());
			if (index != null) {
				index.setNeedAdded(false);
				index.setNeedModified(index.isModified(o));
			} else {
				o.setNeedDeleted(true);
				indexes.add(o);
			}
		}
		Partition part = SQLHelper.getPartitionInfo(daoSource.getType(), daoSource.getMaster(), name);
		if (part != null) {
			if (partition != null) {
				for (PartitionItem item : partition.getItemsMap().values()) {
					if (part.getItemsMap().containsKey(item.getName())) {
						item.setNeedAdded(false);
					}
				}
			}
		} else if (partition != null)
			for (PartitionItem item : partition.getItemsMap().values()) {
				item.setNeedAdded(false);
			}
	}

	public Database getDatabase() {
		return database;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Column getPkColumn() {
		if (pk == null)
			return null;
		for (Column c : columns) {
			if (c.getName().equalsIgnoreCase(pk.columns))
				return c;
		}
		return null;
	}

	public Index getPk() {
		return pk;
	}

	public void setPk(Index pk) {
		this.pk = pk;
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	public List<Index> getUniques() {
		return uniques;
	}

	public void setUniques(List<Index> uniques) {
		this.uniques = uniques;
	}

	public String getEndString() {
		return endString;
	}

	public void setEndString(String endString) {
		this.endString = endString;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public String getCacheConfig() {
		return cacheConfig;
	}

	public void setCacheConfig(String cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	@Override
	public String toString() {
		return "Table [name=" + name + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TableData getTableData() {
		return tableData;
	}

	public void setTableData(TableData tableData) {
		this.tableData = tableData;
	}

	public String getDataSql(boolean created) {
		StringBuffer sb = new StringBuffer();
		if (tableData == null)
			return sb.toString();
		if (!created && tableData.createRunOnly)
			return sb.toString();
		sb.append("delete from " + name + ";");
		String p = tableData.cols, p1 = "";
		if (p == null) {
			p = "";
			for (Column o : columns) {
				if (!database.getStandardColumns().contains(o)) {
					if (p.length() > 0)
						p += ",";
					p += o.getName();
				}
			}
		}
		for (Column o : database.getStandardColumns()) {
			Object def = o.convertDefaultToDbDefault();
			if (def == null) {
				p += "," + o.getName();
				p1 += "," + o.def;
			}
		}
		p = "insert into " + name + "(" + p + ") values(";
		for (String str : tableData.rows) {
			sb.append(p + str + p1 + ");");
		}
		return sb.toString();
	}

	public String getCreateSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("create table " + name + "(\r\n");
		boolean first = true;
		for (Column c : columns) {
			if (first) {
				first = false;
			} else
				sb.append(",\r\n");
			sb.append(c.getCreateSql());
		}
		sb.append("\r\n)" + (endString == null ? "" : endString));
		if (partition != null) {
			sb.append("\r\n" + partition.getCreateSql());
		}
		sb.append(";\r\n");
		for (Index c : uniques)
			sb.append(c.getCreateUniqueSql() + "\r\n");
		boolean hasPkSql = pk != null && !(pk.isLogic || !isMainTable);
		for (Index c : indexes) {
			if (!hasPkSql || !pk.columns.trim().equalsIgnoreCase(c.columns.trim())) // 如果主键索引包含，则不再创建
				sb.append(c.getCreateIndexSql() + "\r\n");
		}
		// 主键
		if (pk != null) {
			sb.append(pk.getCreatePkSql() + "\r\n");
			if (daoSource.getType().equalsIgnoreCase("mysql")) {
				Column c = getPkColumn();
				if (c != null && c.autoIncrement) {
					sb.append(c.getModifySql() + "\r\n");
				}
			}
		}
		return sb.toString();
	}

	public String getModifySql() {
		StringBuffer sb = new StringBuffer();
		for (Column c : columns) {
			if (c.getTable() == null)
				c.setTable(this);
			if (c.isNeedAdded()) {
				sb.append(c.getAddSql() + "\r\n");
			} else if (c.isNeedModified)
				sb.append(c.getModifySql() + "\r\n");
			else if (c.isNeedDeleted())
				sb.append(c.getDeleteSql() + "\r\n");
		}
		// 主键
		if (pk != null) {
			if (pk.getTable() == null)
				pk.setTable(this);
			if (pk.isNeedAdded())
				sb.append(pk.getCreatePkSql() + "\r\n");
			else if (pk.isNeedModified())
				sb.append(pk.getModifyPkSql() + "\r\n");
			else if (pk.isNeedDeleted())
				sb.append(pk.getDeletePkSql() + "\r\n");
		}
		for (Index c : uniques) {
			if (c.getTable() == null)
				c.setTable(this);
			if (c.isNeedAdded())
				sb.append(c.getCreateUniqueSql() + "\r\n");
			else if (c.isNeedModified())
				sb.append(c.getModifyUniqueSql() + "\r\n");
			else if (c.isNeedDeleted())
				sb.append(c.getDeleteUniqueSql() + "\r\n");
		}
		boolean hasPkSql = pk != null && !(pk.isLogic || !isMainTable);
		for (Index c : indexes) {
			if (!hasPkSql || !pk.columns.trim().equalsIgnoreCase(c.columns.trim())) { // 如果主键索引包含，则不再创建
				if (c.getTable() == null)
					c.setTable(this);
				if (c.isNeedAdded())
					sb.append(c.getCreateIndexSql() + "\r\n");
				else if (c.isNeedModified())
					sb.append(c.getModifyIndexSql() + "\r\n");
				else if (c.isNeedDeleted())
					sb.append(c.getDeleteIndexSql() + "\r\n");
			}
		}
		if (partition != null) {
			sb.append(partition.getModifySql());
		}
		return sb.toString();
	}

	public RightConfig getRightConfig() {
		return rightConfig;
	}

	public void setRightConfig(RightConfig rightConfig) {
		this.rightConfig = rightConfig;
	}

	public List<ForeignKey> getForeignGenVarLists() {
		return foreignGenVarLists;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	public boolean isTreeCache() {
		return isTreeCache;
	}

	public void setTreeCache(boolean isTreeCache) {
		this.isTreeCache = isTreeCache;
	}

	public List<ForeignKey> getForeignGenVars() {
		return foreignGenVars;
	}

	public List<Table> getSecondTables() {
		return secondTables;
	}

	public void setSecondTables(List<Table> secondTables) {
		this.secondTables = secondTables;
	}
}
