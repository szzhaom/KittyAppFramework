package kitty.kaf.dao.resultset;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据集。该数据集引用ResultSet，并依据数据库类型和日期等数据类型做了一些与框架相关的特殊处理
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class DaoResultSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 数据列表
	 */
	private List<Object[]> items = new ArrayList<Object[]>();
	/**
	 * 当前项
	 */
	private int currentIndex;
	/**
	 * 全部记录数
	 */
	private int allRecordCount;
	private Map<String, Integer> nameIndexMap = new HashMap<String, Integer>();
	private Object[] current = null;

	/**
	 * 构建空数据集
	 */
	public DaoResultSet() {
		currentIndex = -1;
		current = null;
	}

	/**
	 * 构建DaoResultSet
	 * 
	 * @param rset
	 *            ResultSet对象
	 * @param maxResults
	 *            最大取出多少条数据
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	public DaoResultSet(ResultSet rset, int maxResults) throws SQLException {
		super();
		this.fetchDataFromResultset(rset, maxResults);
	}

	/**
	 * 记录集排序，应该用在记录操作之前
	 * 
	 * @param comparator
	 *            比较接口
	 */
	public void sort(Comparator<Object[]> comparator) {
		Collections.sort(items, comparator);
	}

	/**
	 * 获取某列名的列索引
	 * 
	 * @param columnName
	 *            列名
	 * @return 所在列索引
	 */
	public Integer getColumnIndex(String columnName) {
		return nameIndexMap.get(columnName);
	}

	/**
	 * 获取全部记录数，用于分页查询计算多页
	 */
	public int getAllRecordCount() {
		return allRecordCount;
	}

	/**
	 * 设置全部记录数，用于分布查询计算多页
	 * 
	 * @param allRecordCount
	 *            全部记录数
	 */
	public void setAllRecordCount(int allRecordCount) {
		this.allRecordCount = allRecordCount;
	}

	/**
	 * 当前记录集的记录数
	 */
	public int getSize() {
		return items.size();
	}

	/**
	 * 从ResultSet中取出数据
	 * 
	 * @param rset
	 *            ResultSet对象
	 * @param maxResults
	 *            最大取出多少条数据
	 * @throws SQLException
	 *             如果数据库发生访问错误
	 */
	protected void fetchDataFromResultset(ResultSet rset, int maxResults) throws SQLException {
		currentIndex = -1;
		current = null;
		ResultSetMetaData mt = rset.getMetaData();
		nameIndexMap.clear();
		items.clear();
		int columnCount = mt.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			nameIndexMap.put(mt.getColumnName(i), i);
		}
		int c = 0;
		if (columnCount == 1) {
			while (rset.next()) {
				items.add(new Object[] { getResultSetField(rset, 1) });
				c++;
				if (maxResults > 0 && c >= maxResults)
					break;
			}
		} else
			while (rset.next()) {
				Object[] o = new Object[columnCount];
				for (int i = 0; i < o.length; i++)
					o[i] = getResultSetField(rset, i + 1);
				items.add(o);
				c++;
				if (maxResults > 0 && c >= maxResults)
					break;
			}

	}

	/**
	 * 获取rset中的某个索引值
	 * 
	 * @param rset
	 *            结果集
	 * @param index
	 *            字段索引，从1开始计
	 * @return 值
	 * @throws SQLException
	 *             如果发生数据库访问错误
	 */
	private Object getResultSetField(ResultSet rset, int index) throws SQLException {
		int type = rset.getMetaData().getColumnType(index);
		if (type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP)
			return rset.getTimestamp(index);
		else
			return rset.getObject(index);
	}

	/**
	 * 获取下一个值。如果获取成功，返回true，否则，返回false
	 * 
	 */
	public boolean next() {
		return absolute(currentIndex + 1);
	}

	/**
	 * 获取上一个值。如果获取成功，返回true，否则，返回false
	 * 
	 */
	public boolean previous() {
		return absolute(currentIndex - 1);
	}

	/**
	 * 获取第一个值。如果获取成功，返回true，否则，返回false
	 * 
	 */
	public boolean first() {
		return absolute(0);
	}

	/**
	 * 获取最后一个值。如果获取成功，返回true，否则，返回false
	 * 
	 */
	public boolean last() {
		return absolute(items.size() - 1);
	}

	/**
	 * 获取数据集的记录数
	 */
	public int size() {
		return items.size();
	}

	/**
	 * 获取index所在位置的值
	 * 
	 * @param index
	 *            指定的索引
	 * @return 如果获取成功，返回true，否则，返回false
	 */
	public boolean absolute(int index) {
		if (index < 0 || index >= items.size())
			return false;
		else {
			currentIndex = index;
			current = items.get(index);
			return true;
		}
	}

	/**
	 * 通过列索引column获取某列的Object值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列Object值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 */
	public Object getObject(int column) throws SQLException {
		if (current != null) {
			if (column < 1 || column > current.length)
				throw new IndexOutOfBoundsException();
			else
				return current[column - 1];
		} else
			throw new SQLException("No records");
	}

	/**
	 * 通过列名称columnName获取某列的Object值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列Object值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 */
	public Object getObject(String columnName) throws SQLException {
		Integer column = nameIndexMap.get(columnName);
		if (column == null)
			throw new SQLException("Column name[" + columnName + "] does not exist");
		return getObject(column);
	}

	/**
	 * 通过列索引column获取某列的String值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列String值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 */
	public String getString(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof String)
				return (String) r;
			else
				return r.toString();
		} else
			return null;
	}

	/**
	 * 通过列名称columnName获取某列的String值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列String值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 */
	public String getString(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof String)
				return (String) r;
			else
				return r.toString();
		} else
			return null;
	}

	/**
	 * 通过列索引column获取某列的Boolean值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列Boolean值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 */
	public boolean getBoolean(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof Boolean)
				return (Boolean) r;
			else if (r instanceof Number) {
				return ((Number) r).intValue() != 0;
			} else
				return "true".equalsIgnoreCase(r.toString());
		} else
			return false;
	}

	/**
	 * 通过列名称columnName获取某列的Boolean值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列Boolean值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof Boolean)
				return (Boolean) r;
			else if (r instanceof Number) {
				return ((Number) r).intValue() != 0;
			} else
				return Boolean.valueOf(r.toString());
		} else
			return false;
	}

	/**
	 * 通过列索引column获取某列的Byte值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列Byte值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 * @throws NumberFormatException
	 *             当列值不能转换为byte时
	 */
	public byte getByte(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof Byte)
				return (Byte) r;
			else if (r instanceof Number) {
				return ((Number) r).byteValue();
			} else
				return Byte.valueOf(r.toString());
		} else
			return 0;
	}

	/**
	 * 通过列名称columnName获取某列的Byte值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列Byte值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 * @throws NumberFormatException
	 *             当列值不能转换为byte时
	 */
	public byte getByte(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof Byte)
				return (Byte) r;
			else if (r instanceof Number) {
				return ((Number) r).byteValue();
			} else
				return Byte.valueOf(r.toString());
		} else
			return 0;
	}

	/**
	 * 通过列索引column获取某列的short值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列short值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 * @throws NumberFormatException
	 *             当列值不能转换为short时
	 */
	public short getShort(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof Short)
				return (Short) r;
			else if (r instanceof Number) {
				return ((Number) r).shortValue();
			} else
				return Short.valueOf(r.toString());
		} else
			return 0;
	}

	/**
	 * 通过列名称columnName获取某列的short值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列short值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 * @throws NumberFormatException
	 *             当列值不能转换为short时
	 */
	public short getShort(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof Short)
				return (Short) r;
			else if (r instanceof Number) {
				return ((Number) r).shortValue();
			} else
				return Short.valueOf(r.toString());
		} else
			return 0;
	}

	/**
	 * 通过列索引column获取某列的int值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列int值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 * @throws NumberFormatException
	 *             当列值不能转换为int时
	 */
	public int getInt(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof Integer)
				return (Integer) r;
			else if (r instanceof Number) {
				return ((Number) r).intValue();
			} else
				return Integer.valueOf(r.toString());
		} else
			return 0;
	}

	/**
	 * 通过列名称columnName获取某列的int值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列int值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 * @throws NumberFormatException
	 *             当列值不能转换为int时
	 */
	public int getInt(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof Integer)
				return (Integer) r;
			else if (r instanceof Number) {
				return ((Number) r).intValue();
			} else
				return Integer.valueOf(r.toString());
		} else
			return 0;
	}

	/**
	 * 通过列索引column获取某列的long值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列long值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 * @throws NumberFormatException
	 *             当列值不能转换为long时
	 */
	public long getLong(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof Long)
				return (Long) r;
			else if (r instanceof Number) {
				return ((Number) r).longValue();
			} else
				return Long.valueOf(r.toString());
		} else
			return 0L;
	}

	/**
	 * 通过列名称columnName获取某列的long值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列long值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 * @throws NumberFormatException
	 *             当列值不能转换为long时
	 */
	public long getLong(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof Long)
				return (Long) r;
			else if (r instanceof Number) {
				return ((Number) r).longValue();
			} else
				return Long.valueOf(r.toString());
		} else
			return 0L;
	}

	/**
	 * 通过列索引column获取某列的float值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列float值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 * @throws NumberFormatException
	 *             当列值不能转换为float时
	 */
	public float getFloat(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof Float)
				return (Float) r;
			else if (r instanceof Number) {
				return ((Number) r).floatValue();
			} else
				return Float.valueOf(r.toString());
		} else
			return 0;
	}

	/**
	 * 通过列名称columnName获取某列的float值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列float值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 * @throws NumberFormatException
	 *             当列值不能转换为float时
	 */
	public float getFloat(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof Float)
				return (Float) r;
			else if (r instanceof Number) {
				return ((Number) r).floatValue();
			} else
				return Float.valueOf(r.toString());
		} else
			return 0;
	}

	/**
	 * 通过列索引column获取某列的double值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列double值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 * @throws NumberFormatException
	 *             当列值不能转换为double时
	 */
	public double getDouble(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof Double)
				return (Double) r;
			else if (r instanceof Number) {
				return ((Number) r).doubleValue();
			} else
				return Double.valueOf(r.toString());
		} else
			return 0.0;
	}

	/**
	 * 通过列名称columnName获取某列的double值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列double值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 * @throws NumberFormatException
	 *             当列值不能转换为double时
	 */
	public double getDouble(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof Double)
				return (Double) r;
			else if (r instanceof Number) {
				return ((Number) r).doubleValue();
			} else
				return Double.valueOf(r.toString());
		} else
			return 0.0;
	}

	/**
	 * 通过列索引column获取某列的Date值
	 * 
	 * @param column
	 *            索引，从1开始计数
	 * @return 获取的该列Date值
	 * @throws SQLException
	 *             如果当前没有记录
	 * @throws IndexOutOfBoundsException
	 *             当column小于1或者column大于总列数时
	 */
	public Date getDate(int column) throws SQLException {
		Object r = getObject(column);
		if (r != null) {
			if (r instanceof Date)
				return (Date) r;
			else if (r instanceof Number) {
				return new Date(((Number) r).longValue());
			} else
				throw new SQLException("Type can not be converted to a date");
		} else
			return null;
	}

	/**
	 * 通过列名称columnName获取某列的Date值
	 * 
	 * @param columnName
	 *            列名称
	 * @return 获取的该列Date值
	 * @throws SQLException
	 *             如果当前没有记录或列名不存在
	 */
	public Date getDate(String columnName) throws SQLException {
		Object r = getObject(columnName);
		if (r != null) {
			if (r instanceof Date)
				return (Date) r;
			else if (r instanceof Number) {
				return new Date(((Number) r).longValue());
			} else
				throw new SQLException("Type can not be converted to a date");
		} else
			return null;
	}

	public List<Long> getLongList(int column) {
		List<Long> r = new ArrayList<Long>();
		for (Object[] o : items) {
			if (o[column] instanceof Number)
				r.add(((Number) o[column]).longValue());
		}
		return r;
	}

	public List<Integer> getIntList(int column) {
		List<Integer> r = new ArrayList<Integer>();
		for (Object[] o : items) {
			if (o[column] instanceof Number)
				r.add(((Number) o[column]).intValue());
		}
		return r;
	}

	public List<Short> getShortList(int column) {
		List<Short> r = new ArrayList<Short>();
		for (Object[] o : items) {
			if (o[column] instanceof Number)
				r.add(((Number) o[column]).shortValue());
		}
		return r;
	}

	public List<Byte> getByteList(int column) {
		List<Byte> r = new ArrayList<Byte>();
		for (Object[] o : items) {
			if (o[column] instanceof Number)
				r.add(((Number) o[column]).byteValue());
		}
		return r;
	}

	public List<String> getStringList(int column) {
		List<String> r = new ArrayList<String>();
		for (Object[] o : items) {
			r.add(o[column] != null ? o[column].toString() : null);
		}
		return r;
	}
}
