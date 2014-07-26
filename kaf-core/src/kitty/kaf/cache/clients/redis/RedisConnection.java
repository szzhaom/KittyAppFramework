package kitty.kaf.cache.clients.redis;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kitty.kaf.cache.clients.CacheBytesValue;
import kitty.kaf.cache.clients.ICacheClientConnection;
import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.exceptions.DataException;
import kitty.kaf.io.ValueObject;
import kitty.kaf.logging.Logger;
import kitty.kaf.pools.ConnectionPool;
import kitty.kaf.pools.tcp.MasterSlaveTcpConnection;

/**
 * Redis缓存连接
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class RedisConnection extends MasterSlaveTcpConnection implements ICacheClientConnection {
	static Logger logger = Logger.getLogger(RedisConnection.class);
	private String lastMasterChangeFlag;

	public RedisConnection() {
		super();
	}

	public RedisConnection(ConnectionPool<?> pool) {
		super(pool);
	}

	@Override
	public void keepAlive() throws DataException {
		try {
			checkMasterChangeEvent();
			RedisProtocol protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
			protocol.sendCommand(RedisProtocol.Command.PING);
			protocol.readReply();
		} catch (Throwable e) {
		}
		try {
			RedisProtocol protocol = new RedisProtocol(getReadStream(false), getWriteStream(false), false);
			protocol.sendCommand(RedisProtocol.Command.PING);
			protocol.readReply();
		} catch (Throwable e) {
		}
	}

	protected void connect() throws IOException {
		try {
			super.open();
			lastMasterChangeFlag = ((RedisConnectionPool) getPool()).getMasterChangeFlag();
		} catch (ConnectException e) {
			throw new IOException(e);
		}
	}

	protected boolean checkMasterChangeEvent() throws IOException {
		RedisConnectionPool pool = (RedisConnectionPool) getPool();
		if (lastMasterChangeFlag == null) {
			if (pool.getMasterChangeFlag() == null)
				return false;
		} else if (lastMasterChangeFlag.equals(pool.getMasterChangeFlag()))
			return false;
		forceClose();
		connect();
		return true;
	}

	@Override
	public void set(Object key, CacheBytesValue value, Date expiry) throws IOException, InterruptedException {
		checkMasterChangeEvent();
		long expiryMs = 0;
		if (expiry != null) {
			expiryMs = expiry.getTime() - System.currentTimeMillis();
			if (expiryMs < 0)
				expiryMs = 0;
		}
		RedisProtocol protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
		IOException ex = null;
		try {
			if (expiryMs > 0)
				protocol.sendCommand(RedisProtocol.Command.SET, key, value, "PX", expiryMs + "");
			else
				protocol.sendCommand(RedisProtocol.Command.SET, key, value);
			protocol.readReply();
		} catch (RedisReplyException e) {
			if (e.getMessage().trim().startsWith(RedisProtocol.READONLY))
				ex = e;
		} catch (IOException e) {
			ex = e;
		}
		if (ex != null) {
			// 重新获取主从服务器配置
			((RedisConnectionPool) getPool()).findMasterSlaves();
			if (checkMasterChangeEvent()) {// 如果主从服务器变化，则重新set
				protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
				if (expiryMs > 0)
					protocol.sendCommand(RedisProtocol.Command.SET, key, value, "PX", expiryMs + "");
				else
					protocol.sendCommand(RedisProtocol.Command.SET, key, value);
				protocol.readReply();
			} else
				throw ex;
		}
	}

	@Override
	public CacheBytesValue get(Object key) throws IOException, InterruptedException {
		if (((RedisConnectionPool) getPool()).getAddressList().length <= 1)
			((RedisConnectionPool) getPool()).findMasterSlaves();
		checkMasterChangeEvent();
		try {
			logger.debug(getSocket(false));
			RedisProtocol protocol = new RedisProtocol(getReadStream(false), getWriteStream(false), true);
			protocol.sendCommand(RedisProtocol.Command.GET, key);
			return (CacheBytesValue) protocol.readReply();
		} catch (RedisReplyException e) {
			throw e;
		} catch (IOException e) {
			// 重新获取主从服务器配置
			((RedisConnectionPool) getPool()).findMasterSlaves();
			if (checkMasterChangeEvent()) {// 如果主从服务器变化，则重新get
				RedisProtocol protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), true);
				protocol.sendCommand(RedisProtocol.Command.GET, key);
				return (CacheBytesValue) protocol.readReply();
			} else
				throw e;
		}
	}

	@Override
	public boolean incrdecr(Object key, long stepValue, ValueObject<Long> value) throws IOException,
			InterruptedException {
		checkMasterChangeEvent();
		RedisProtocol protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
		IOException ex = null;
		try {
			protocol.sendCommand(stepValue > 0 ? RedisProtocol.Command.INCRBY : RedisProtocol.Command.DECRBY, key,
					String.valueOf(Math.abs(stepValue)));
			value.setValue((Long) protocol.readReply());
		} catch (RedisReplyException e) {
			if (e.getMessage().trim().startsWith(RedisProtocol.READONLY))
				ex = e;
		} catch (IOException e) {
			ex = e;
		}
		if (ex != null) {
			// 重新获取主从服务器配置
			((RedisConnectionPool) getPool()).findMasterSlaves();
			if (checkMasterChangeEvent()) {// 如果主从服务器变化，则重新set
				protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
				protocol.sendCommand(stepValue > 0 ? RedisProtocol.Command.INCRBY : RedisProtocol.Command.DECRBY, key,
						String.valueOf(Math.abs(stepValue)));
				value.setValue((Long) protocol.readReply());
			} else
				throw ex;
		}
		return true;
	}

	@Override
	public boolean delete(Object key) throws IOException, InterruptedException {
		checkMasterChangeEvent();
		RedisProtocol protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
		IOException ex = null;
		boolean ret = false;
		try {
			protocol.sendCommand(RedisProtocol.Command.DEL, key);
			ret = (Long) protocol.readReply() > 0;
		} catch (RedisReplyException e) {
			if (e.getMessage().trim().startsWith(RedisProtocol.READONLY))
				ex = e;
		} catch (IOException e) {
			ex = e;
		}
		if (ex != null) {
			// 重新获取主从服务器配置
			((RedisConnectionPool) getPool()).findMasterSlaves();
			if (checkMasterChangeEvent()) {// 如果主从服务器变化，则重新set
				protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
				protocol.sendCommand(RedisProtocol.Command.DEL, key);
				ret = (Long) protocol.readReply() > 0;
			} else
				throw ex;
		}
		return ret;
	}

	@Override
	public boolean delete(Object[] keys) throws IOException, InterruptedException {
		checkMasterChangeEvent();
		RedisProtocol protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
		IOException ex = null;
		boolean ret = false;
		try {
			protocol.sendCommand(RedisProtocol.Command.DEL.raw, keys);
			ret = (Long) protocol.readReply() > 0;
		} catch (RedisReplyException e) {
			if (e.getMessage().trim().startsWith(RedisProtocol.READONLY))
				ex = e;
		} catch (IOException e) {
			ex = e;
		}
		if (ex != null) {
			// 重新获取主从服务器配置
			((RedisConnectionPool) getPool()).findMasterSlaves();
			if (checkMasterChangeEvent()) {// 如果主从服务器变化，则重新set
				protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), false);
				protocol.sendCommand(RedisProtocol.Command.DEL.raw, keys);
				ret = (Long) protocol.readReply() > 0;
			} else
				throw ex;
		}
		return ret;
	}

	@Override
	public void get(List<String> keys, Map<String, CacheBytesValue> map) throws InterruptedException, IOException {
		if (((RedisConnectionPool) getPool()).getAddressList().length <= 1)
			((RedisConnectionPool) getPool()).findMasterSlaves();
		checkMasterChangeEvent();
		Object ks[] = keys.toArray();
		List<?> ls = null;
		try {
			logger.debug(getSocket(false));
			RedisProtocol protocol = new RedisProtocol(getReadStream(false), getWriteStream(false), true);
			protocol.sendCommand(RedisProtocol.Command.MGET, ks);
			ls = (List<?>) protocol.readReply();
		} catch (RedisReplyException e) {
			throw e;
		} catch (IOException e) {
			// 重新获取主从服务器配置
			((RedisConnectionPool) getPool()).findMasterSlaves();
			if (checkMasterChangeEvent()) {// 如果主从服务器变化，则重新get
				RedisProtocol protocol = new RedisProtocol(getReadStream(true), getWriteStream(true), true);
				protocol.sendCommand(RedisProtocol.Command.MGET, ks);
				ls = (List<?>) protocol.readReply();
			} else
				throw e;
		}
		for (int i = 0; i < ls.size(); i++) {
			CacheBytesValue v = (CacheBytesValue) ls.get(i);
			if (v != null)
				map.put(ks[i].toString(), v);
		}
	}
}
