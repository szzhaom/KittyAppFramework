package kitty.kaf.cache.clients.redis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import kitty.kaf.cache.clients.CacheBytesValue;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.logging.Logger;

/**
 * Redis缓存连接
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class RedisProtocol {
	static Logger logger = Logger.getLogger(RedisProtocol.class);
	public static final int MAX_LENGTH = 1024 * 1024;
	static final byte[] CRLF = "\r\n".getBytes();

	public static final byte DOLLAR_BYTE = '$';
	public static final byte ASTERISK_BYTE = '*';
	public static final byte PLUS_BYTE = '+';
	public static final byte MINUS_BYTE = '-';
	public static final byte COLON_BYTE = ':';
	public static final String READONLY = "READONLY";

	public static enum Command {
		PING, SET, GET, QUIT, EXISTS, DEL, TYPE, FLUSHDB, KEYS, RANDOMKEY, RENAME, RENAMENX, RENAMEX, DBSIZE, EXPIRE, EXPIREAT, TTL, SELECT, MOVE, FLUSHALL, GETSET, MGET, SETNX, SETEX, MSET, MSETNX, DECRBY, DECR, INCRBY, INCR, APPEND, SUBSTR, HSET, HGET, HSETNX, HMSET, HMGET, HINCRBY, HEXISTS, HDEL, HLEN, HKEYS, HVALS, HGETALL, RPUSH, LPUSH, LLEN, LRANGE, LTRIM, LINDEX, LSET, LREM, LPOP, RPOP, RPOPLPUSH, SADD, SMEMBERS, SREM, SPOP, SMOVE, SCARD, SISMEMBER, SINTER, SINTERSTORE, SUNION, SUNIONSTORE, SDIFF, SDIFFSTORE, SRANDMEMBER, ZADD, ZRANGE, ZREM, ZINCRBY, ZRANK, ZREVRANK, ZREVRANGE, ZCARD, ZSCORE, MULTI, DISCARD, EXEC, WATCH, UNWATCH, SORT, BLPOP, BRPOP, AUTH, SUBSCRIBE, PUBLISH, UNSUBSCRIBE, PSUBSCRIBE, PUNSUBSCRIBE, PUBSUB, ZCOUNT, ZRANGEBYSCORE, ZREVRANGEBYSCORE, ZREMRANGEBYRANK, ZREMRANGEBYSCORE, ZUNIONSTORE, ZINTERSTORE, SAVE, BGSAVE, BGREWRITEAOF, LASTSAVE, SHUTDOWN, INFO, MONITOR, SLAVEOF, CONFIG, STRLEN, SYNC, LPUSHX, PERSIST, RPUSHX, ECHO, LINSERT, DEBUG, BRPOPLPUSH, SETBIT, GETBIT, BITPOS, SETRANGE, GETRANGE, EVAL, EVALSHA, SCRIPT, SLOWLOG, OBJECT, BITCOUNT, BITOP, SENTINEL, DUMP, RESTORE, PEXPIRE, PEXPIREAT, PTTL, INCRBYFLOAT, PSETEX, CLIENT, TIME, MIGRATE, HINCRBYFLOAT, SCAN, HSCAN, SSCAN, ZSCAN, WAIT, CLUSTER, ASKING, PFADD, PFCOUNT, PFMERGE;

		public final byte[] raw;

		Command() {
			raw = this.name().getBytes();
		}
	}

	public static class DataParam {
		byte[] data;
		int offset;
		int len;

		public DataParam(byte[] data, int offset, int len) {
			super();
			this.data = data;
			this.offset = offset;
			this.len = len;
		}
	}

	DataReadStream readStream;
	DataWriteStream writeStream;
	boolean isCacheBytesMode;

	public RedisProtocol() {
		super();
	}

	public RedisProtocol(DataReadStream readStream, DataWriteStream writeStream, boolean isCacheBytesMode) {
		this.readStream = readStream;
		this.writeStream = writeStream;
		this.isCacheBytesMode = isCacheBytesMode;
	}

	void sendCommand(Command command, Object... args) throws IOException {
		sendCommand(command.raw, args);
	}

	void sendCommand(byte[] command, Object[] args) throws IOException {
		DataWriteStream os = writeStream;
		os.writeByte(ASTERISK_BYTE);
		os.writeString(Integer.toString(args.length + 1));
		os.write(CRLF);
		os.writeByte(DOLLAR_BYTE);
		os.writeString(Integer.toString(command.length));
		os.write(CRLF);
		os.write(command);
		os.write(CRLF);

		for (final Object arg : args) {
			os.writeByte(DOLLAR_BYTE);
			if (arg instanceof byte[]) {
				os.writeString(Integer.toString(((byte[]) arg).length));
				os.write(CRLF);
				os.write((byte[]) arg);
			} else if (arg instanceof DataParam) {
				DataParam o = (DataParam) arg;
				os.writeString(Integer.toString(o.len));
				os.write(CRLF);
				os.write(o.data, o.offset, o.len);
			} else if (arg instanceof String) {
				byte[] b = ((String) arg).getBytes();
				os.writeString(Integer.toString(b.length));
				os.write(CRLF);
				os.write(b);
			} else if (arg instanceof CacheBytesValue) {
				CacheBytesValue v = (CacheBytesValue) arg;
				os.writeString(Integer.toString(v.getValue().length + 2));
				os.write(CRLF);
				os.writeShort(v.getFlags());
				os.write(v.getValue());
			} else {
				byte[] b = (arg.toString()).getBytes();
				os.writeString(Integer.toString(b.length));
				os.write(CRLF);
				os.write(b);
			}
			os.write(CRLF);
		}
	}

	public Object readReply() throws IOException {
		return readReply(readStream);
	}

	public CacheBytesValue replyToCacheBytesValue(Object o) {
		byte[] b = (byte[]) o;
		CacheBytesValue v = new CacheBytesValue(new byte[b.length - 1], b[0]);
		System.arraycopy(b, 1, v.getValue(), 0, v.getValue().length);
		return v;
	}

	Object readReply(DataReadStream rs) throws IOException {
		switch (rs.readByte()) {
		case MINUS_BYTE:
			readErrorReply(rs);
			break;
		case ASTERISK_BYTE:
			return readMultiBulkReply(rs);
		case COLON_BYTE:
			return readColonReply(rs);
		case DOLLAR_BYTE:
			return readBulkReply(rs);
		case PLUS_BYTE:
			return readStatusCodeReply(rs);
		default:
			throw new IOException("错误的响应");
		}
		return null;
	}

	private byte[] readStatusCodeReply(DataReadStream rs) throws IOException {
		return rs.readln(CRLF, true);
	}

	private Object readBulkReply(DataReadStream rs) throws NumberFormatException, IOException {
		int len = Integer.parseInt(new String(rs.readln(CRLF, false)));
		if (len == -1) {
			return null;
		}
		Object ret;
		if (isCacheBytesMode) {
			int flags = rs.readShort();
			ret = new CacheBytesValue(rs.readFully(len - 2), flags);
		} else
			ret = rs.readFully(len);
		rs.skipBytes(2);
		return ret;
	}

	private long readColonReply(DataReadStream rs) throws IOException {
		String num = new String(rs.readln(CRLF, false));
		return Long.valueOf(num);
	}

	private List<Object> readMultiBulkReply(DataReadStream rs) throws NumberFormatException, IOException {
		int num = Integer.parseInt(new String(rs.readln(CRLF, false)));
		if (num == -1) {
			return null;
		}
		List<Object> ret = new ArrayList<Object>(num);
		for (int i = 0; i < num; i++) {
			ret.add(readReply(rs));
		}
		return ret;
	}

	private void readErrorReply(DataReadStream rs) throws UnsupportedEncodingException, IOException {
		String message = new String(rs.readln(CRLF, false), "utf-8");
		throw new RedisReplyException(message);
	}

}
