package kitty.kaf.pools.tcp;

import java.io.IOException;
import java.net.Socket;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.pools.Connection;
import kitty.kaf.pools.ConnectionPool;

abstract public class MasterSlaveTcpConnection extends Connection {
	Socket masterSocket, slaveSocket;
	DataReadStream masterReadStream, slaveReadStream;
	DataWriteStream masterWriteStream, slaveWriteStream;

	public MasterSlaveTcpConnection() {
	}

	public MasterSlaveTcpConnection(ConnectionPool<?> pool) {
		super(pool);
	}

	@Override
	public void open() throws ConnectException {
		TcpConnectionPool<?> pool = (TcpConnectionPool<?>) getPool();
		try {
			slaveSocket = masterSocket = new Socket();
			masterSocket.connect(pool.getAddress(), pool.getConnectionTimeout());
			masterSocket.setSoTimeout(pool.getSoTimeout());
			slaveReadStream = masterReadStream = new DataReadStream(masterSocket.getInputStream(),
					pool.getDataTimeout());
			slaveWriteStream = masterWriteStream = new DataWriteStream(masterSocket.getOutputStream(),
					pool.getDataTimeout());
			for (int i = 1; i < pool.getAddressList().length; i++) {
				try {
					slaveSocket = new Socket();
					slaveSocket.connect(pool.getAddressList()[i], pool.getConnectionTimeout());
					slaveSocket.setSoTimeout(pool.getSoTimeout());
					slaveReadStream = new DataReadStream(slaveSocket.getInputStream(), pool.getDataTimeout());
					slaveWriteStream = new DataWriteStream(slaveSocket.getOutputStream(), pool.getDataTimeout());
					break;
				} catch (IOException e) {
					try {
						slaveSocket.close();
					} catch (Throwable ex) {
					}
					slaveSocket = masterSocket;
					slaveReadStream = masterReadStream;
					slaveWriteStream = masterWriteStream;
				}
			}
			setLastAliveTime(System.currentTimeMillis());
		} catch (IOException e) {
			throw new ConnectException(e);
		}
	}

	@Override
	public boolean isClosed() {
		return masterSocket == null || masterSocket.isClosed();
	}

	@Override
	public void forceClose() {
		if (!isClosed()) {
			masterReadStream = null;
			masterWriteStream = null;
			slaveReadStream = null;
			slaveWriteStream = null;
			try {
				if (slaveSocket != null && masterSocket != slaveSocket)
					slaveSocket.close();
			} catch (Throwable e) {
			}
			try {
				masterSocket.close();
			} catch (Throwable e) {
			}
			slaveSocket = null;
			masterSocket = null;
		}
	}

	/**
	 * 当从服务器出现问题，调用此函数，切换从服务器
	 */
	public void switchSlave() {
		if (slaveSocket != masterSocket) {
			try {
				slaveSocket.close();
			} catch (Throwable e) {
			}
			slaveSocket = null;
		}
		TcpConnectionPool<?> pool = (TcpConnectionPool<?>) getPool();
		if (pool.getAddressList().length <= 1)
			return;
		for (int i = 1; i < pool.getAddressList().length; i++) {
			try {
				slaveSocket = new Socket();
				slaveSocket.connect(pool.getAddressList()[i], pool.getConnectionTimeout());
				slaveSocket.setSoTimeout(pool.getSoTimeout());
				slaveReadStream = new DataReadStream(slaveSocket.getInputStream(), pool.getDataTimeout());
				slaveWriteStream = new DataWriteStream(slaveSocket.getOutputStream(), pool.getDataTimeout());
				break;
			} catch (IOException e) {
				try {
					slaveSocket.close();
				} catch (Throwable ex) {
				}
				slaveSocket = masterSocket;
				slaveReadStream = masterReadStream;
				slaveWriteStream = masterWriteStream;
			}
		}
	}

	/**
	 * 检查主从服务器是否改变
	 * 
	 * @throws ConnectException
	 */
	protected void checkMasterChanged() throws ConnectException {
		TcpConnectionPool<?> pool = (TcpConnectionPool<?>) getPool();
		if (masterSocket != null) {
			if (!(pool.getAddress().getAddress().getHostAddress()
					.equals(masterSocket.getInetAddress().getHostAddress()) && pool.getAddress().getPort() == masterSocket
					.getPort())) {
				// 假如主从发生改变，则重新打开
				forceClose();
				open();
			}
		} else
			open();
	}

	public Socket getSocket(boolean isMaster) {
		return isMaster ? masterSocket : slaveSocket;
	}

	public DataReadStream getReadStream(boolean isMaster) throws ConnectException {
		checkMasterChanged();
		return isMaster ? masterReadStream : slaveReadStream;
	}

	public DataWriteStream getWriteStream(boolean isMaster) throws ConnectException {
		checkMasterChanged();
		return isMaster ? masterWriteStream : slaveWriteStream;
	}

}
