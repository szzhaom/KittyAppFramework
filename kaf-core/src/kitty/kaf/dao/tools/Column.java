package kitty.kaf.dao.tools;

import java.sql.SQLException;

import kitty.kaf.dao.source.DaoSource;
import kitty.kaf.dao.tools.datatypes.ColumnDataType;
import kitty.kaf.helper.StringHelper;

import org.w3c.dom.Element;

/**
 * 数据库表的一列
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class Column extends BaseConfigDef {
	private String name;
	ColumnDataType dataType;
	boolean isNullable;
	boolean isVarLength = true;
	String def;
	int length;
	int digits;
	String desp;
	boolean autoIncrement, toJson;
	Table table;
	boolean isUniqueKeyField;
	boolean isToStringField;
	String sequence;
	boolean isSecret, isMd5;
	boolean editEnabled;

	public Column(String name) {
		this.name = name;
	}

	public boolean isToJson() {
		return toJson;
	}

	public void setToJson(boolean toJson) {
		this.toJson = toJson;
	}

	/**
	 * 从XML Element中读取字段配置
	 * 
	 * @param el
	 *            字段配置element
	 * @throws SQLException
	 */
	public Column(Element el, Table table, DaoSource daoSource) throws SQLException {
		this.table = table;
		this.daoSource = daoSource;
		name = el.getAttribute("name");
		desp = el.getAttribute("desp");
		isVarLength = !el.hasAttribute("varlength") ? true : Boolean.valueOf(el.getAttribute("varlength"));
		dataType = ColumnDataType.getColumnDataType(this, el.getAttribute("type"),
				el.hasAttribute("classname") ? el.getAttribute("classname") : null);
		isNullable = el.hasAttribute("nullable") ? el.getAttribute("nullable").equals("true") : true;
		autoIncrement = el.hasAttribute("auto_increment") ? el.getAttribute("auto_increment").equals("true") : false;
		toJson = el.hasAttribute("toJson") ? el.getAttribute("toJson").equals("true") : true;
		def = el.hasAttribute("default") ? el.getAttribute("default") : null;
		length = !el.hasAttribute("length") ? 0 : Integer.valueOf(el.getAttribute("length"));
		sequence = el.hasAttribute("sequence") ? el.getAttribute("sequence") : null;
		digits = !el.hasAttribute("digits") ? 0 : Integer.valueOf(el.getAttribute("digits"));
		isUniqueKeyField = el.hasAttribute("isUKeyField") ? "true".equalsIgnoreCase(el.getAttribute("isUKeyField"))
				: false;
		isToStringField = el.hasAttribute("isToStringField") ? "true".equalsIgnoreCase(el
				.getAttribute("isToStringField")) : false;
		isSecret = el.hasAttribute("isSecret") ? "true".equalsIgnoreCase(el.getAttribute("isSecret")) : false;
		isMd5 = el.hasAttribute("md5") ? "true".equalsIgnoreCase(el.getAttribute("md5")) : false;
		editEnabled = el.hasAttribute("editEnabled") ? "true".equalsIgnoreCase(el.getAttribute("editEnabled")) : true;
	}

	public Column() {
	}

	public Column clone() {
		Column c = new Column();
		c.name = this.name;
		c.def = this.def;
		c.desp = this.desp;
		c.daoSource = this.daoSource;
		c.autoIncrement = this.autoIncrement;
		c.dataType = this.dataType;
		c.dataType.setColumn(this);
		c.digits = this.digits;
		c.isNullable = this.isNullable;
		c.table = this.table;
		c.isUniqueKeyField = this.isUniqueKeyField;
		c.isSecret = this.isSecret;
		c.editEnabled = this.editEnabled;
		c.isToStringField = this.isToStringField;
		c.isMd5 = this.isMd5;
		return c;
	}

	public boolean isEditEnabled() {
		return editEnabled;
	}

	public void setEditEnabled(boolean editEnabled) {
		this.editEnabled = editEnabled;
	}

	public boolean isSecret() {
		return isSecret;
	}

	public void setSecret(boolean isSecret) {
		this.isSecret = isSecret;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public String getDef() {
		return def;
	}

	public int getLength() {
		return length;
	}

	public String getDesp() {
		return desp;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public DaoSource getDaoSource() {
		return daoSource;
	}

	public void setDaoSource(DaoSource daoSource) {
		this.daoSource = daoSource;
	}

	public boolean isVarLength() {
		return isVarLength;
	}

	public void setVarLength(boolean isVarLength) {
		this.isVarLength = isVarLength;
	}

	@Override
	public String toString() {
		return name;
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
		Column other = (Column) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}

	public int getDigits() {
		return digits;
	}

	public void setDigits(int digits) {
		this.digits = digits;
	}

	public boolean isModified(Column other) {
		boolean ret = true;
		if (ret)
			ret = length == 0 || other.length == length;
		if (ret)
			ret = other.digits == digits;
		if (ret) {
			if (dataType == null || other.dataType == null)
				throw new NullPointerException();
			ret = other.dataType.getDbDataType().equalsIgnoreCase(dataType.getDbDataType());
		}
		if (ret)
			ret = other.isNullable == isNullable;
		if (ret)
			ret = other.autoIncrement == autoIncrement;
		if (ret) {
			String def = convertDefaultToDbDefault();
			String odef = other.convertDefaultToDbDefault();
			if (odef == null)
				ret = def == null;
			else
				ret = odef.equalsIgnoreCase(def);
		}
		if (ret) {
			if (!daoSource.getType().equalsIgnoreCase("sybase")) {
				ret = other.desp.equalsIgnoreCase(desp);
			}
		}
		return !ret;
	}

	public void copyData(Column other) {
		this.length = other.length;
		this.digits = other.digits;
		this.dataType = other.dataType;
		this.isNullable = other.isNullable;
		this.desp = other.desp;
	}

	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public void setDef(String def) {
		this.def = def;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String convertDefaultToDbDefault() {
		if (def == null)
			return null;
		if (dataType != null) {
			if (dataType.getDataType().equals("boolean"))
				return def.equalsIgnoreCase("true") ? "-1" : "0";
			else if (dataType.getDataType().equals("date")) {
				if (daoSource.getType().equals("mysql"))
					return null;
			}
		}
		return def;
	}

	public String getCreateSql() {
		String sql = "";
		sql += "\t" + StringHelper.fillString(name, "left", ' ', 40) + " ";
		String dt = dataType.getDbDataType();
		if (length > 0) {
			dt += "(" + length;
			if (digits > 0)
				dt += "," + digits;
			dt += ")";
		}
		sql += StringHelper.fillString(dt, "left", ' ', 20) + " ";
		if (!isNullable())
			sql += "not null";
		else
			sql += "null    ";
		String def = convertDefaultToDbDefault();
		if (def != null) {
			if (getDataType().getDataType().equals("string"))
				def = "'" + def + "'";
			sql += " default " + def;
		}
		if (daoSource.getType().equals("mysql")) {
			// if (autoIncrement){
			// sql += " auto_increment";
			// }
			sql += " comment '" + desp + "'";
		}
		return sql;
	}

	public String getAddSql() {
		if (this.table == null)
			throw new NullPointerException(getName() + ".table is null");
		return "alter table " + this.table.getName() + " add column " + getCreateSql() + ";";
	}

	public String getModifySql() {
		String sql = "alter table " + this.table.getName() + " modify column " + getCreateSql() + ";";
		if (daoSource.getType().equals("mysql")) {
			int index = sql.lastIndexOf("comment");
			String f = sql.substring(0, index);
			String f1 = sql.substring(index);
			if (autoIncrement) {
				f += "auto_increment ";
			}
			sql = f + f1;
		}
		return sql;
	}

	public String getDeleteSql() {
		return "alter table " + this.table.getName() + " drop column " + getName() + ";";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUniqueKeyField() {
		return isUniqueKeyField;
	}

	public void setUniqueKeyField(boolean isUniqueKeyField) {
		this.isUniqueKeyField = isUniqueKeyField;
	}

	public boolean isToStringField() {
		return isToStringField;
	}

	public void setToStringField(boolean isToStringField) {
		this.isToStringField = isToStringField;
	}

	public ColumnDataType getDataType() {
		return dataType;
	}

	public void setDataType(ColumnDataType dataType) {
		this.dataType = dataType;
	}

	public String getVarName() {
		return StringHelper.toVarName(name);
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public boolean isMd5() {
		return isMd5;
	}

	public void setMd5(boolean isMd5) {
		this.isMd5 = isMd5;
	}
}
