package kitty.kaf.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import kitty.kaf.exceptions.TimeoutException;
import kitty.kaf.helper.BytesHelper;

/**
 * 数据写入流，实现DataWrite接口
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 * 
 */
public class DataWriteStream implements DataWrite {
	OutputStream outputStream;
	int timeout;

	/**
	 * 构造DataWriteStream
	 * 
	 * @param outputStream
	 *            输出流
	 * @param timeout
	 *            写数据超时时间，以毫秒为单位
	 */
	public DataWriteStream(OutputStream outputStream, int timeout) {
		super();
		this.outputStream = outputStream;
		this.timeout = timeout;
	}

	/**
	 * 获取写数据超时时间，以毫秒为单位
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * 设置写数据超时时间，以毫秒为单位
	 * 
	 * @param timeout
	 *            超时时间
	 */

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 获取输出流
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	protected int write(byte[] b, int offset, int len, int timeout) throws IOException {
		if (len == 0)
			return 0;
		if (b == null)
			throw new NullPointerException();
		if (offset < 0 || (offset + len) > b.length)
			throw new IndexOutOfBoundsException();
		if (outputStream == null)
			throw new IOException("尚未指定具体的输出流对象，不允许写数据!");
		outputStream.write(b, offset, len);
		outputStream.flush();
		return len;
	}

	@Override
	public int write(byte[] b, int offset, int len) throws IOException {
		return write(b, offset, len, timeout);
	}

	@Override
	public int write(byte[] b) throws IOException {
		if (b == null)
			throw new NullPointerException();
		return write(b, 0, b.length);
	}

	@Override
	public void writeFully(byte[] b, int offset, int len) throws IOException {
		long prev = System.currentTimeMillis();
		int leftLen = len;
		while (leftLen > 0 && (System.currentTimeMillis() - prev) < timeout) {
			int l = write(b, offset + len - leftLen, leftLen, timeout - (int) (System.currentTimeMillis() - prev));
			if (l > 0) {
				prev = System.currentTimeMillis() - timeout + 1000;
				leftLen -= l;
				if (leftLen <= 0)
					break;
			}
		}
		if (leftLen > 0) {
			throw new TimeoutException("写数据超时");
		}
	}

	@Override
	public void writeFully(byte[] b) throws IOException {
		if (b == null)
			b = new byte[0];
		writeFully(b, 0, b.length);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		writeByte(v ? -1 : 0);
	}

	@Override
	public void writeByte(int v) throws IOException {
		byte[] b = new byte[1];
		b[0] = (byte) v;
		writeFully(b);
	}

	@Override
	public void writeShort(int v, boolean isNetByteOrder) throws IOException {
		byte[] b = BytesHelper.shortToBytes((short) v);
		if (isNetByteOrder)
			b = BytesHelper.bytesReverse(b, 0, b.length);
		writeFully(b);
	}

	@Override
	public void writeInt(int v, boolean isNetByteOrder) throws IOException {
		byte[] b = BytesHelper.intToBytes(v);
		if (isNetByteOrder)
			b = BytesHelper.bytesReverse(b, 0, b.length);
		writeFully(b);
	}

	@Override
	public void writeLong(long v) throws IOException {
		byte[] b = BytesHelper.longToBytes(v);
		writeFully(b);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		byte[] b = BytesHelper.floatToBytes(v);
		writeFully(b);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		byte[] b = BytesHelper.doubleToBytes(v);
		writeFully(b);
	}

	@Override
	public void writeDate(Date v) throws IOException {
		if (v == null)
			v = new Date(0);
		byte[] b = BytesHelper.longToBytes(v.getTime());
		writeFully(b);
	}

	@Override
	public void writePacketByteLen(byte[] v) throws IOException {
		if (v == null)
			v = new byte[0];
		int len = v.length;
		if (len > 255)
			len = 255;
		writeByte(len);
		writeFully(v, 0, len);
	}

	@Override
	public void writePacketShortLen(byte[] v, boolean isNetByteOrder) throws IOException {
		if (v == null)
			v = new byte[0];
		int len = v.length;
		if (len > 65535)
			len = 65535;
		writeShort(len, isNetByteOrder);
		writeFully(v, 0, len);
	}

	@Override
	public void writePacketIntLen(byte[] v, boolean isNetByteOrder) throws IOException {
		if (v == null)
			v = new byte[0];
		int len = v.length;
		if (len > 65535000)
			len = 65535000;
		writeInt(len, isNetByteOrder);
		writeFully(v, 0, len);
	}

	@Override
	public void writeString(String v) throws IOException {
		if (v == null)
			v = "";
		writeFully(v.getBytes("utf-8"));
	}

	@Override
	public void writePacketByteLenString(String v) throws IOException {
		if (v == null)
			v = "";
		writePacketByteLen(v.getBytes("utf-8"));
	}

	@Override
	public void writePacketShortLenString(String v, boolean isNetByteOrder) throws IOException {
		if (v == null)
			v = "";
		writePacketShortLen(v.getBytes("utf-8"), isNetByteOrder);
	}

	@Override
	public void writePacketShortLen(byte[] v) throws IOException {
		writePacketShortLen(v, false);
	}

	@Override
	public void writePacketIntLen(byte[] v) throws IOException {
		writePacketIntLen(v, false);
	}

	@Override
	public void writePacketShortLenString(String v) throws IOException {
		if (v == null)
			v = "";
		writePacketShortLenString(v, false);
	}

	@Override
	public void writePacketIntLenString(String v, boolean isNetByteOrder) throws IOException {
		if (v == null)
			v = "";
		writePacketIntLen(v.getBytes("utf-8"), isNetByteOrder);
	}

	@Override
	public void writePacketIntLenString(String v) throws IOException {
		if (v == null)
			v = "";
		writePacketIntLenString(v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		writeShort(v, false);
	}

	@Override
	public void writeInt(int v) throws IOException {
		writeInt(v, false);
	}

	@Override
	public void writeByteList(List<Byte> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (Byte o : ls)
				writeByte(o);
		}
	}

	@Override
	public void writeShortList(List<Short> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (Short o : ls)
				writeShort(o);
		}
	}

	@Override
	public void writeIntList(List<Integer> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (Integer o : ls)
				writeInt(o);
		}
	}

	@Override
	public void writeLongList(List<Long> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (Long o : ls)
				writeLong(o);
		}
	}

	@Override
	public void writeFloatList(List<Float> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (Float o : ls)
				writeFloat(o);
		}
	}

	@Override
	public void writeDoubleList(List<Double> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (Double o : ls)
				writeDouble(o);
		}
	}

	@Override
	public <T extends Writable> void writeList(List<T> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (T o : ls)
				o.writeToStream(this);
		}
	}

	@Override
	public void writePacketByteLenStringList(List<String> ls) throws IOException {
		int c = 0;
		if (ls != null)
			c = ls.size();
		writeInt(c);
		if (ls != null)
			for (String o : ls)
				writePacketByteLenString(o);
	}

	@Override
	public void writePacketShortLenStringList(List<String> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (String o : ls)
				writePacketShortLenString(o);
		}
	}

	@Override
	public void writePacketIntLenStringList(List<String> ls) throws IOException {
		if (ls == null)
			writeInt(0);
		else {
			writeInt(ls.size());
			for (String o : ls)
				writePacketIntLenString(o);
		}
	}

}
