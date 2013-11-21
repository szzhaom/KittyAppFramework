package kitty.kaf.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kitty.kaf.exceptions.TimeoutException;
import kitty.kaf.helper.BytesHelper;

/**
 * 数据读取流，实现DataRead接口
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * @see DataRead
 * 
 */
public class DataReadStream implements DataRead {
	InputStream inputStream;
	int timeout;

	/**
	 * 构建DataReadStream
	 * 
	 * @param inputStream
	 *            输入流
	 * @param timeout
	 *            读数据超时，以毫秒为单位
	 */
	public DataReadStream(InputStream inputStream, int timeout) {
		super();
		this.inputStream = inputStream;
		this.timeout = timeout;
	}

	/**
	 * 获取输入流
	 * 
	 * @return 输入流
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * 获取读数据超时时间，以毫秒为单位
	 * 
	 * @return 读数据超时时间
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * 设置读数据超时时间，以毫秒为单位
	 * 
	 * @param timeout
	 *            读数据超时时间
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 从所包含的输入流中将 len 个字节读入一个字节数组中。尽量读取 len
	 * 个字节，但可能读取较少的字节数，该字节数也可能为零。以整数形式返回实际读取的字节数。
	 * 在输入数据可用、检测到文件末尾或抛出异常之前，此方法将阻塞至读取超时。 <br>
	 * 如果 b 为 null，则抛出 NullPointerException。 <br>
	 * 如果 off 为负，或 len 为负，抑或 off+len 大于数组 b 的长度，则抛出 IndexOutOfBoundsException。 <br>
	 * 如果 len 为零，则不读取字节并返回 0；否则至少试图读取一个字节。如果因为该流在文件未尾而无字节可用，则返回 -1
	 * 值；否则至少读取一个字节并将其存储到 b 中。 <br>
	 * 将读取的第一个字节存储到元素 b[off] 中，将下一个字节存储到 b[off+1] 中，依此类推。读取的字节数至多等于 len。设 k
	 * 为实际读取的字节数；这些字节将存储在 b[off] 到 b[off+k-1] 的元素中，b[off+k] 到 b[off+len-1]
	 * 的元素不受影响。 <br>
	 * 在所有的情况下，b[0] 到 b[off] 的元素和 b[off+len] 到 b[b.length-1] 的元素都不受影响。 <br>
	 * 如果因为文件末尾以外的其他原因而无法读取第一个字节，则抛出 IOException。尤其在输入流已关闭的情况下，将抛出 IOException。
	 * 
	 * @param b
	 *            存储读取数据的缓冲区
	 * @param offset
	 *            读取的数据存储的起始偏移量
	 * @param len
	 *            读取的最大字节数
	 * @param timeout
	 *            指定的超时时间
	 * @return 读入缓冲区的字节总数，如果因为已经到达流的末尾而没有更多的数据，则返回 -1。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	protected int read(byte[] b, int offset, int len, int timeout) throws IOException {
		if (b == null)
			throw new NullPointerException();
		if (inputStream == null)
			throw new IOException("尚未指定具体的输入流对象，不允许读数据!");
		int ret = inputStream.read(b, offset, len);
		if (ret <= 0) {
			throw new IOException("流通道已经关闭或断开");
		}
		return ret;
	}

	@Override
	public int read(byte[] b, int offset, int len) throws IOException {
		return read(b, offset, len, timeout);
	}

	@Override
	public int read(byte[] b) throws IOException {
		if (b == null)
			throw new NullPointerException();
		return read(b, 0, b.length);
	}

	@Override
	public void readFully(byte[] b, int offset, int len) throws IOException {
		if (b == null)
			throw new NullPointerException();
		long prev = System.currentTimeMillis();
		int leftLen = len;
		while (leftLen > 0 && (System.currentTimeMillis() - prev) < timeout) {
			int l = read(b, offset + len - leftLen, leftLen, timeout - (int) (System.currentTimeMillis() - prev));
			if (l > 0) {
				prev = System.currentTimeMillis() - timeout + 1000;
				leftLen -= l;
				if (leftLen <= 0)
					break;
			}
		}
		if (leftLen > 0) {
			throw new TimeoutException("读数据超时");
		}
	}

	@Override
	public byte[] readFully(int len) throws IOException {
		byte[] b = new byte[len];
		readFully(b, 0, b.length);
		return b;
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		if (b == null)
			throw new NullPointerException();
		readFully(b, 0, b.length);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		return readFully(1)[0];
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return BytesHelper.byteToUnsigned(readByte());
	}

	@Override
	public short readShort(boolean isNetByteOrder) throws IOException {
		byte[] b = readFully(2);
		if (isNetByteOrder)
			b = BytesHelper.bytesReverse(b, 0, 2);
		return BytesHelper.bytesToShort(b, 0);
	}

	@Override
	public int readUnsignedShort(boolean isNetByteOrder) throws IOException {
		return BytesHelper.shortToUnsigned(readShort(isNetByteOrder));
	}

	@Override
	public int readInt(boolean isNetByteOrder) throws IOException {
		byte[] b = readFully(4);
		if (isNetByteOrder)
			b = BytesHelper.bytesReverse(b, 0, 4);
		return BytesHelper.bytesToInt(b, 0);
	}

	@Override
	public long readLong() throws IOException {
		byte[] b = readFully(8);
		return BytesHelper.bytesToLong(b, 0);
	}

	@Override
	public float readFloat() throws IOException {
		int r = readInt(false);
		return Float.intBitsToFloat(r);
	}

	@Override
	public double readDouble() throws IOException {
		long r = readLong();
		return Double.longBitsToDouble(r);
	}

	@Override
	public Date readDate() throws IOException {
		return new Date(readLong());
	}

	@Override
	public byte[] readPacketByteLen() throws IOException {
		int len = readUnsignedByte();
		return readFully(len);
	}

	@Override
	public byte[] readPacketShortLen(boolean isNetByteOrder) throws IOException {
		int len = readUnsignedShort(isNetByteOrder);
		return readFully(len);
	}

	@Override
	public byte[] readPacketIntLen(boolean isNetByteOrder) throws IOException {
		int len = readInt(isNetByteOrder) & 0xffffffff;
		return readFully(len);
	}

	@Override
	public void skipBytes(int n) throws IOException {
		readFully(n);
	}

	@Override
	public String readString(int len) throws IOException {
		return new String(readFully(len), "utf-8");
	}

	@Override
	public String readPacketByteLenString() throws IOException {
		return new String(readPacketByteLen(), "utf-8");
	}

	@Override
	public String readPacketShortLenString(boolean isNetByteOrder) throws IOException {
		return new String(readPacketShortLen(isNetByteOrder), "utf-8");
	}

	@Override
	public byte[] readln(byte[] eofs, boolean returnIncludeEofs) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(readFully(eofs.length));
		long prev = System.currentTimeMillis();
		while ((System.currentTimeMillis() - prev) < timeout) {
			byte[] b = stream.toByteArray();
			if (BytesHelper.memcmp(b, b.length - eofs.length, eofs, 0, eofs.length) == 0) {
				int len = b.length;
				if (!returnIncludeEofs)
					len -= eofs.length;
				byte[] ret = new byte[len];
				System.arraycopy(b, 0, ret, 0, len);
				return ret;
			}
			stream.write(readFully(1));
		}
		throw new TimeoutException("读数据超时");
	}

	@Override
	public byte[] readPacketShortLen() throws IOException {
		return readPacketShortLen(false);
	}

	@Override
	public byte[] readPacketIntLen() throws IOException {
		return readPacketIntLen(false);
	}

	@Override
	public String readPacketShortLenString() throws IOException {
		return new String(readPacketShortLen(false), "utf-8");
	}

	@Override
	public String readPacketIntLenString(boolean isNetByteOrder) throws IOException {
		return new String(readPacketIntLen(false), "utf-8");
	}

	@Override
	public String readPacketIntLenString() throws IOException {
		return readPacketIntLenString(false);
	}

	@Override
	public short readShort() throws IOException {
		return readShort(false);
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return readUnsignedShort(false);
	}

	@Override
	public int readInt() throws IOException {
		return readInt(false);
	}

	@Override
	public List<Byte> readByteList() throws IOException {
		List<Byte> r = new ArrayList<Byte>();
		int c = readInt(false);
		for (int i = 0; i < c; i++)
			r.add(readByte());
		return r;
	}

	@Override
	public List<Short> readShortList() throws IOException {
		List<Short> r = new ArrayList<Short>();
		int c = readInt(false);
		for (int i = 0; i < c; i++)
			r.add(readShort());
		return r;
	}

	@Override
	public List<Integer> readIntList() throws IOException {
		List<Integer> r = new ArrayList<Integer>();
		int c = readInt(false);
		for (int i = 0; i < c; i++)
			r.add(readInt());
		return r;
	}

	@Override
	public List<Long> readLongList() throws IOException {
		List<Long> r = new ArrayList<Long>();
		int c = readInt(false);
		for (int i = 0; i < c; i++)
			r.add(readLong());
		return r;
	}

	@Override
	public List<Float> readFloatList() throws IOException {
		List<Float> r = new ArrayList<Float>();
		int c = readInt(false);
		for (int i = 0; i < c; i++)
			r.add(readFloat());
		return r;
	}

	@Override
	public List<Double> readDoubleList() throws IOException {
		List<Double> r = new ArrayList<Double>();
		int c = readInt(false);
		for (int i = 0; i < c; i++)
			r.add(readDouble());
		return r;
	}

	@Override
	public <T extends Readable> List<T> readList(Class<T> clazz) throws IOException {
		List<T> r = new ArrayList<T>();
		int c = readInt(false);
		for (int i = 0; i < c; i++) {
			T t;
			try {
				t = clazz.newInstance();
			} catch (InstantiationException e) {
				throw new IOException(e);
			} catch (IllegalAccessException e) {
				throw new IOException(e);
			}
			t.readFromStream(this);
			r.add(t);
		}

		return r;
	}

}
