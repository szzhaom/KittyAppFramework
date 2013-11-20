package kitty.kaf.dao.table;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.io.Copyable;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.Writable;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.trade.pack.HttpRequest;

/**
 * 表对象，所有与数据库表操作相关的数据基类
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
abstract public class TableObject implements kitty.kaf.io.Readable, Writable, Copyable<TableObject> {
	private static final long serialVersionUID = 1L;

	/**
	 * 最后修改时间
	 */
	private Date lastModifiedTime = new Date();

	/**
	 * 创建时间
	 */
	private Date creationTime = new Date();

	/**
	 * 是否删除
	 */
	private Boolean isDeleted = false;

	/**
	 * 获取该数据的表定义，由于表定义只有一个，继续类通常应该返回一个静态变量。
	 */
	abstract public TableDef getTableDef();

	/**
	 * 新生成一个实例
	 * 
	 * @return 新的实例
	 */
	abstract public TableObject newInstance();

	@Override
	public TableObject copy() {
		TableObject o = newInstance();
		copyData(o);
		return o;
	}

	/**
	 * 获得最后修改时间
	 */
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * 获得创建时间
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * 获得是否删除
	 */
	public Boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * 设置最后修改时间
	 */
	public void setLastModifiedTime(Date v) {
		lastModifiedTime = v;
	}

	/**
	 * 设置创建时间
	 */
	public void setCreationTime(Date v) {
		creationTime = v;
	}

	/**
	 * 设置是否删除
	 */
	public void setIsDeleted(Boolean v) {
		isDeleted = v;
	}

	@Override
	public void copyData(TableObject other) {
		this.creationTime = other.creationTime;
		this.isDeleted = other.isDeleted;
		this.lastModifiedTime = other.lastModifiedTime;
	}

	@Override
	public void writeToStream(DataWrite stream) throws IOException {
		stream.writeBoolean(isDeleted);
		stream.writeDate(creationTime);
		stream.writeDate(lastModifiedTime);
	}

	@Override
	public void readFromStream(DataRead stream) throws IOException {
		isDeleted = stream.readBoolean();
		creationTime = stream.readDate();
		lastModifiedTime = stream.readDate();
	}

	/**
	 * 从数据库结果集中取数据
	 * 
	 * @param rset
	 *            结果集
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public void readFromDb(DaoResultSet rset) throws SQLException {
		this.creationTime = rset.getDate("creation_time");
		this.isDeleted = rset.getBoolean("is_deleted");
		this.lastModifiedTime = rset.getDate("last_modified_time");
	}

	public Object getByColumn(String columnName) {
		if (columnName.equalsIgnoreCase("is_deleted"))
			return isDeleted;
		else if (columnName.equalsIgnoreCase("creation_time"))
			return creationTime;
		else if (columnName.equalsIgnoreCase("last_modified_time"))
			return lastModifiedTime;
		else
			throw new NoSuchFieldError();
	}

	public void toJson(JSONObject json) throws JSONException {
	}

	public void readFromRequest(HttpRequest request, boolean isCreate) throws Exception {
		if (isCreate)
			creationTime = new Date();
		lastModifiedTime = new Date();
	}
}
