package kitty.kaf.pools.memcached;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import kitty.kaf.helper.BytesHelper;
import kitty.kaf.helper.SecurityHelper;
import kitty.kaf.io.BytesObjectSerializer;
import kitty.kaf.io.DataWriteStream;

/**
 * Memcached的序列化转换器
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class MemcachedBytesObjectSerializer implements
		BytesObjectSerializer<MemcachedValue> {
	public static final int MARKER_BYTE = 1;
	public static final int MARKER_BOOLEAN = 8192;
	public static final int MARKER_INTEGER = 4;
	public static final int MARKER_LONG = 16384;
	public static final int MARKER_CHARACTER = 16;
	public static final int MARKER_STRING = 32;
	public static final int MARKER_STRINGBUFFER = 64;
	public static final int MARKER_FLOAT = 128;
	public static final int MARKER_SHORT = 256;
	public static final int MARKER_DOUBLE = 512;
	public static final int MARKER_DATE = 1024;
	public static final int MARKER_STRINGBUILDER = 2048;
	public static final int MARKER_BYTEARR = 4096;
	public static final int MARKER_SERIALIZED = 8;
	public static final int MARKER_COMPRESSED = 2;

	@Override
	public MemcachedValue objectToBytes(Object value) throws IOException {
		int flags = 0;
		byte[] buf;
		if (value instanceof Byte) {
			flags |= MARKER_BYTE;
			buf = new byte[] { ((Byte) value).byteValue() };
		} else if (value instanceof Boolean) {
			flags |= MARKER_BOOLEAN;
			buf = new byte[] { (byte) (((Boolean) value).booleanValue() ? 1 : 0) };
		} else if (value instanceof Integer) {
			flags |= MARKER_INTEGER;
			buf = BytesHelper.intToBytes(((Integer) value).intValue());
		} else if (value instanceof Long) {
			flags |= MARKER_LONG;
			buf = BytesHelper.longToBytes(((Long) value).longValue());
		} else if (value instanceof Character) {
			flags |= MARKER_CHARACTER;
			buf = BytesHelper.shortToBytes((short) ((Character) value)
					.charValue());
		} else if (value instanceof String) {
			flags |= MARKER_STRING;
			buf = ((String) value).getBytes("UTF-8");
		} else if (value instanceof StringBuffer) {
			flags |= MARKER_STRINGBUFFER;
			buf = ((StringBuffer) value).toString().getBytes("UTF-8");
		} else if (value instanceof Float) {
			flags |= MARKER_FLOAT;
			buf = BytesHelper.floatToBytes(((Float) value).floatValue());
		} else if (value instanceof Short) {
			flags |= MARKER_SHORT;
			buf = BytesHelper.shortToBytes(((Short) value).shortValue());
		} else if (value instanceof Double) {
			flags |= MARKER_DOUBLE;
			buf = BytesHelper.doubleToBytes(((Double) value).doubleValue());
		} else if (value instanceof Date) {
			flags |= MARKER_DATE;
			buf = BytesHelper.longToBytes(((Date) value).getTime());
		} else if (value instanceof StringBuilder) {
			flags |= MARKER_STRINGBUILDER;
			buf = ((StringBuilder) value).toString().getBytes("UTF-8");
		} else if (value instanceof byte[]) {
			flags |= MARKER_BYTEARR;
			buf = (byte[]) value;
		} else if (value instanceof kitty.kaf.io.Writable) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataWriteStream out = new DataWriteStream(bos, 3000);
			((kitty.kaf.io.Writable) value).writeToStream(out);
			flags |= MARKER_BYTEARR;
			buf = bos.toByteArray();
		} else if (value instanceof Serializable) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			(new ObjectOutputStream(bos)).writeObject(value);
			flags |= MARKER_SERIALIZED;
			buf = bos.toByteArray();
		} else
			throw new MemcachedException("必须是序列化对象");
		MemcachedValue mv = new MemcachedValue(buf, flags);
		if (mv.getValue().length > 512) { // 如果大于512字节，则压缩
			mv.setValue(SecurityHelper.zipCompressIncludeUncompressLen(
					mv.getValue(), 0, mv.getValue().length));
			mv.setFlags(mv.getFlags() | MARKER_COMPRESSED);
		}
		return mv;
	}

	@Override
	public Object bytesToObject(MemcachedValue value) throws IOException {
		if (value.getValue().length < 1)
			return null;
		else {
			int flags = value.getFlags();
			if ((value.getFlags() & MARKER_COMPRESSED) == MARKER_COMPRESSED) {
				value.setValue(SecurityHelper
						.zipDecompressIncludeUncompressLen(value.getValue(), 0,
								value.getValue().length));
			}
			byte[] b = value.getValue();
			if ((flags & MARKER_BYTE) == MARKER_BYTE)
				return new Byte(b[0]);
			else if ((flags & MARKER_BOOLEAN) == MARKER_BOOLEAN)
				return b[0] == 1 ? Boolean.TRUE : Boolean.FALSE;
			else if ((flags & MARKER_INTEGER) == MARKER_INTEGER)
				return BytesHelper.bytesToInt(b, 0);
			else if ((flags & MARKER_LONG) == MARKER_LONG)
				return BytesHelper.bytesToLong(b, 0);
			else if ((flags & MARKER_CHARACTER) == MARKER_CHARACTER)
				return new Character((char) BytesHelper.bytesToShort(b, 0));
			else if ((flags & MARKER_STRING) == MARKER_STRING)
				return new String(b, "UTF-8");
			else if ((flags & MARKER_STRINGBUFFER) == MARKER_STRINGBUFFER)
				return new StringBuffer(new String(b, "UTF-8"));
			else if ((flags & MARKER_FLOAT) == MARKER_FLOAT)
				return BytesHelper.bytesToFloat(b, 0);
			else if ((flags & MARKER_SHORT) == MARKER_SHORT)
				return BytesHelper.bytesToShort(b, 0);
			else if ((flags & MARKER_DOUBLE) == MARKER_DOUBLE)
				return BytesHelper.bytesToDouble(b, 0);
			else if ((flags & MARKER_DATE) == MARKER_DATE)
				return new Date(BytesHelper.bytesToLong(b, 0));
			else if ((flags & MARKER_STRINGBUILDER) == MARKER_STRINGBUILDER)
				return new StringBuilder(new String(b, "UTF-8"));
			else if ((flags & MARKER_BYTEARR) == MARKER_BYTEARR)
				return b;
			else {
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(b));
				try {
					return ois.readObject();
				} catch (ClassNotFoundException e) {
					throw new MemcachedException(e);
				}
			}
		}
	}

}
