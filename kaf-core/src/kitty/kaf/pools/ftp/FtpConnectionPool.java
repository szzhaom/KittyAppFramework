package kitty.kaf.pools.ftp;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.pools.tcp.TcpConnectionPool;

public class FtpConnectionPool<C extends FtpConnection> extends
		TcpConnectionPool<C> {

	public FtpConnectionPool(String name, int minConnectionSize,
			int maxConnectionSize, int connectionTimeout) {
		super(name, minConnectionSize, maxConnectionSize, connectionTimeout);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected C createConnection() throws ConnectException {
		return (C) new FtpConnection(this, address, getConnectionTimeout(),
				getDataTimeout());
	}

}
