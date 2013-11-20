package kitty.kaf.dao.source;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 包含主、从两个JDBC数据源的Dao数据源
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public class DaoSource {
	protected DataSource masterSource, slaveSource;
	protected Connection master, slave;
	private String type;

	/**
	 * 构造函数
	 * 
	 * @param type
	 *            数据库类型字串
	 * @param masterSource
	 *            主数据库连接池
	 * @param slaveSource
	 *            从数据库连接池
	 */
	public DaoSource(String type, DataSource masterSource,
			DataSource slaveSource) {
		super();
		this.type = type;
		this.masterSource = masterSource;
		if (slaveSource == null)
			this.slaveSource = this.masterSource;
		else
			this.slaveSource = slaveSource;
	}

	/**
	 * 构造函数，主、从数据源为同一个
	 * 
	 * @param masterSource
	 *            主、从数据库源
	 */
	public DaoSource(String type, DataSource masterSource) {
		super();
		this.type = type;
		this.masterSource = masterSource;
		this.slaveSource = masterSource;
	}

	/**
	 * 获取主数据库连接
	 * 
	 * @throws SQLException
	 */
	public Connection getMaster() throws SQLException {
		if (master == null) {
			master = masterSource.getConnection();
			if (masterSource == slaveSource || slaveSource == null)
				slave = master;
		}
		return master;
	}

	/**
	 * 获取从数据库连接
	 * 
	 * @throws SQLException
	 */
	public Connection getSlave() throws SQLException {
		if (slave == null) {
			if (masterSource == slaveSource || slaveSource == null)
				slave = getMaster();
			else
				slave = slaveSource.getConnection();
		}
		return slave;
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		if (master == slave) {
			if (master != null) {
				try {
					master.close();
				} catch (Throwable e) {
				}
			}
		} else {
			if (master != null) {
				try {
					master.close();
				} catch (Throwable e) {
				}
			}
			if (slave != null) {
				try {
					slave.close();
				} catch (Throwable e) {
				}
			}
		}
	}

	/**
	 * 获取数据库的类型字串
	 * 
	 * @return 数据库类型字串
	 */
	public String getType() {
		return type;
	}
}
