package kitty.kaf.dao.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kitty.kaf.helper.StringHelper;
import kitty.kaf.io.UniqueKeyable;

import org.w3c.dom.Element;

/**
 * 数据库表的索引
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class Index extends BaseConfigDef implements UniqueKeyable {
	private static final long serialVersionUID = 1L;
	private String name;
	String columns;
	String tablespace;
	String javaClassName;
	Table table;

	public Index() {
		super();
	}

	public Index(Table table, String name) {
		this.table = table;
		this.daoSource = table.daoSource;
		this.name = name;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
		this.daoSource = table.daoSource;
	}

	/**
	 * 从XML Element中读取索引配置
	 * 
	 * @param el
	 *            索引配置element
	 */
	public Index(Element el, Table table) {
		this.table = table;
		this.daoSource = table.daoSource;
		name = el.getAttribute("name");
		tablespace = el.getAttribute("tablespace");
		columns = el.getAttribute("columns");
		javaClassName = el.hasAttribute("classname") ? el
				.getAttribute("classname") : null;
	}

	public String getUniqueKey() {
		return name;
	}

	public void setUniqueKey(String name) {
		this.name = name;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getTablespace() {
		return tablespace;
	}

	public void setTablespace(String tablespace) {
		this.tablespace = tablespace;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getCreatePkSql() {
		return "alter table " + table.getName() + " add primary key " + name
				+ "(" + columns + ");";
	}

	public String getCreateUniqueSql() {
		return "alter table " + table.getName() + " add unique " + name + "("
				+ columns + ");";
	}

	public String getCreateIndexSql() {
		return "create index " + name + " on " + table.getName() + "("
				+ columns + ");";
	}

	public String getDeletePkSql() {
		return "alter table " + table.getName() + " drop primary key " + name
				+ ";";
	}

	public String getDeleteUniqueSql() {
		return "alter table " + table.getName() + " drop index " + name + ";";
	}

	public String getDeleteIndexSql() {
		return "alter table " + table.getName() + " drop index " + name + ";";
	}

	public String getModifyPkSql() {
		return getDeletePkSql() + "\r\n" + getCreatePkSql();
	}

	public String getModifyUniqueSql() {
		return getDeleteUniqueSql() + "\r\n" + getCreateUniqueSql();
	}

	public String getModifyIndexSql() {
		return getDeleteIndexSql() + "\r\n" + getCreateIndexSql();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Index other = (Index) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}

	/**
	 * 获取完整的索引列字串，将未指定asc或desc的列，默认添加asc. 如：id ==> id asc, id asc ==> asc
	 * 
	 * @return 格式化后的字串
	 */
	public String getFullColumns() {
		String[] s = columns.split(",");
		String ret = "";
		for (String str : s) {
			String[] s1 = StringHelper.splitBlank(str);
			if (ret.length() > 0)
				ret += ",";
			if (s1.length == 1)
				ret += str + " asc";
			else
				ret += str;
		}
		return ret;
	}

	public boolean isModified(Index other) {
		boolean ret;
		ret = getFullColumns().equalsIgnoreCase(other.getFullColumns());
		return !ret;
	}

	public List<Column> getTableColumns() throws SQLException {
		List<Column> ret = new ArrayList<Column>();
		String[] s = columns.split(",");
		for (String str : s) {
			String[] s1 = StringHelper.splitBlank(str);
			Column c = null;
			for (Column o : table.columns) {
				if (o.getName().equalsIgnoreCase(s[0])) {
					c = o;
					break;
				}
			}
			if (c == null)
				throw new SQLException(s1[0] + " not exists");
			ret.add(c);
		}
		return ret;
	}

}
