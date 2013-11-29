package kitty.kaf.pools.ftp;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.pools.tcp.TcpConnectionPool;

public class FtpConnectionPool<C extends FtpConnection> extends TcpConnectionPool<C> {
	String user, pwd;

	public FtpConnectionPool(String name, String user, String pwd, int minConnectionSize, int maxConnectionSize,
			int connectionTimeout) {
		super(name, minConnectionSize, maxConnectionSize, connectionTimeout);
		this.user = user;
		this.pwd = pwd;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected C createConnection() throws ConnectException {
		C c = (C) new FtpConnection(this, address, getConnectionTimeout(), getDataTimeout());
		c.setUser(user);
		c.setPwd(pwd);
		try {
			c.open();
		} catch (Throwable e) {
			throw new ConnectException(e);
		}
		return c;
	}

}
