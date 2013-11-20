package kitty.kaf.dao.source;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import kitty.kaf.pools.db.DatabaseConnection;
import kitty.kaf.pools.jndi.JndiConnection;
import kitty.kaf.pools.jndi.JndiConnectionFactory;
import kitty.kaf.pools.jndi.Lookuper;

/**
 * 基于JNDI的Dao数据源
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class JndiDaoSource extends DaoSource {
	protected JndiConnection con = null;

	public JndiDaoSource(String type, String masterJndi) throws SQLException {
		super(type, null);
		try {
			con = JndiConnectionFactory.getLocalConnection(this);
			masterSource = slaveSource = con
					.lookup(Lookuper.JNDI_TYPE_DATASOURCE, masterJndi,
							DataSource.class);
		} catch (Throwable e) {
			if (con != null)
				try {
					con.close();
				} catch (Throwable e1) {
				}
			throw new SQLException(e);
		}
	}

	public JndiDaoSource(String type, String masterJndi, String slaveJndi)
			throws SQLException {
		super(type, null);
		try {
			con = JndiConnectionFactory.getLocalConnection(this);
			masterSource = con.lookup(Lookuper.JNDI_TYPE_DATASOURCE,
					masterJndi, DataSource.class);
			if (slaveJndi == null)
				slaveSource = masterSource;
			else
				slaveSource = con.lookup(Lookuper.JNDI_TYPE_DATASOURCE,
						slaveJndi, DataSource.class);
		} catch (Throwable e) {
			if (con != null)
				try {
					con.close();
				} catch (Throwable e1) {
				}
			throw new SQLException(e);
		}
	}

	@Override
	public void close() {
		super.close();
		if (con != null) {
			try {
				con.close();
			} catch (Throwable e) {
			}
			con = null;
		}
	}

	@Override
	public Connection getMaster() throws SQLException {
		if (master == null) {
			master = new DatabaseConnection(null, masterSource.getConnection());
			if (masterSource == slaveSource || slaveSource == null)
				slave = master;
		}
		return master;
	}

	@Override
	public Connection getSlave() throws SQLException {
		if (slave == null) {
			if (masterSource == slaveSource || slaveSource == null)
				slave = getMaster();
			else
				slave = new DatabaseConnection(null,
						slaveSource.getConnection());
		}
		return slave;
	}

}
