package kitty.kaf.io;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import kitty.kaf.exceptions.TimeoutException;

/**
 * DataWrite 接口用于将任意 Java 基本类型转换为一系列字节，并将这些字节写入二进制流。
 * 
 * 对于此接口中写入字节的所有方法，如果由于某种原因无法写入某个字节，则抛出
 * IOException。当在指定时间内无法写入相应的数据，则都将抛出TimeoutException(IOException 的一种）
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public interface DataWrite {
	/**
	 * 将数组 b 中的 len 个字节按顺序写入输出流。如果 b 为 null，则抛出 NullPointerException。如果 off 为负，或
	 * len 为负，抑或 off+len 大于数组 b 的长度，则抛出 IndexOutOfBoundsException。如果 len
	 * 为零，则不写入字节。否则，首先写入字节 b[off]，然后写入字节 b[off+1]，依此类推；最后一个写入字节是 b[off+len-1]。
	 * 
	 * @param b
	 *            要写入的数据
	 * @param offset
	 *            数据中的起始偏移量
	 * @param len
	 *            写入的最大字节数
	 * @return 实际写入的字节总数。
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	int write(byte[] b, int offset, int len) throws IOException;

	/**
	 * 写数据，相当于write(b,0,b.length);
	 * 
	 * @param b
	 *            要写入的数据
	 * @return 实际写入的字节总数。
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#write(byte[], int, int)
	 */
	int write(byte[] b) throws IOException;

	/**
	 * 将数据全部写入至输出流，如果一段时间未成功写入，则一直阻塞至抛出TimeoutException。
	 * 
	 * @param b
	 *            要写入的数据
	 * @param offset
	 *            数据中的起始偏移量
	 * @param len
	 *            写入的字节数
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeFully(byte[] b, int offset, int len) throws IOException;

	/**
	 * 效果等同于writeFully(b,0,b.length);
	 * 
	 * @param b
	 *            要写入的数据
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#writeFully(byte[], int, int)
	 */
	void writeFully(byte[] b) throws IOException;

	/**
	 * 将一个 boolean 值写入输出流。如果参数 v 为 true，则写入值 (byte)1；如果 v 为 false，则写入值 (byte)0。
	 * 
	 * @param v
	 *            要写入的boolean值。
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeBoolean(boolean v) throws IOException;

	/**
	 * 将参数 v 的八个低位写入输出流。忽略 v 的 24 个高位。（这意味着 writeByte 的作用与使用整数做参数的 write 完全相同。）
	 * 
	 * @param v
	 *            要写入的字节值 。
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeByte(int v) throws IOException;

	/**
	 * 将两个字节写入输出流，用它们表示参数值。要写入的字节值（按顺序显示）是：
	 * 
	 * <pre>
	 * <code>
	 * (byte)(0xff & (v >> 8))
	 * (byte)(0xff & v)</code>
	 * </pre>
	 * 
	 * 如果是网络字节序，则顺序反转。
	 * 
	 * @param v
	 *            要写入的short值
	 * @param isNetByteOrder
	 *            是否是网络字节序。
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeShort(int v, boolean isNetByteOrder) throws IOException;

	/**
	 * 写入一个Short值
	 * 
	 * @param 要写入的Short值
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeShort(int v) throws IOException;;

	/**
	 * 将一个 int 值写入输出流，该值由四个字节组成。要写入的字节值（按顺序显示）是：
	 * 
	 * <pre>
	 * <code>
	 * (byte)(0xff & (v >> 24))
	 * (byte)(0xff & (v >> 16))
	 * (byte)(0xff & (v >>    8))
	 * (byte)(0xff & v)</code>
	 * </pre>
	 * 
	 * 如果是网络字节序，则顺序反转。
	 * 
	 * @param v
	 *            要写入的 int 值
	 * @param isNetByteOrder
	 *            是否是网络字节序。
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeInt(int v, boolean isNetByteOrder) throws IOException;

	/**
	 * 写入一个Int值
	 * 
	 * @param 要写入的Int值
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeInt(int v) throws IOException;;

	/**
	 * 将一个 long 值写入输出流，该值由八个字节组成。要写入的字节值（按顺序显示）是：
	 * 
	 * <pre>
	 * <code>
	 * (byte)(0xff & (v >> 56))
	 * (byte)(0xff & (v >> 48))
	 * (byte)(0xff & (v >> 40))
	 * (byte)(0xff & (v >> 32))
	 * (byte)(0xff & (v >> 24))
	 * (byte)(0xff & (v >> 16))
	 * (byte)(0xff & (v >>  8))
	 * (byte)(0xff & v)</code>
	 * </pre>
	 * 
	 * 
	 * @param v
	 *            要写入的long值。
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeLong(long v) throws IOException;

	/**
	 * 将一个 float 值写入输出流，该值由四个字节组成。实现这一点的方式是：首先使用与 Float.floatToIntBits
	 * 方法完全相同的方式将此 float 值转换为一个 int 值，然后使用与 writeInt 方法完全相同的方式写入该 int 值。
	 * 
	 * @param v
	 *            要写入的 float 值
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeFloat(float v) throws IOException;

	/**
	 * 写入八个输入字节并返回一个 double 值。实现这一点的方法是：先使用与 writelong 方法完全相同的方式构造一个 long
	 * 值，然后使用与 Double.longBitsToDouble 方法完全相同的方式将此 long 值转换成一个 double
	 * 值。此方法适用于写入用接口 DataOutput 的 writeDouble 方法写入的字节。
	 * 
	 * @param v
	 *            要写入的 double 值
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeDouble(double v) throws IOException;

	/**
	 * 将 v.getTime() 以long的方式写入输出流中。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * writeLong(v.getTime())</code>
	 * </pre>
	 * 
	 * @param v
	 *            要写入的 Date 值
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#writeLong(long)
	 * @see Date#getTime()
	 */

	void writeDate(Date v) throws IOException;

	/**
	 * 写入一段经过打包处理的字节。打包格式为：1个字节表示字节数组的长度+字节数组，实际写入数据的长度为返回的字节数组长度+1.<br>
	 * 如果v.length> 255，则截取前255个字节。写入过程为：
	 * <ol>
	 * <li>写入一个字节，字节值为v.length</li>
	 * <li>写入v后返回。</li>
	 * </ol>
	 * 
	 * 
	 * @param v
	 *            要写入的字节数组
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writePacketByteLen(byte[] v) throws IOException;

	/**
	 * 写入一段经过打包处理的字节。打包格式为：2个字节表示字节数组的长度+字节数组，实际写入数据的长度为返回的字节数组长度+2.<br>
	 * 如果v.length>65535，则截取前65535个字节。写入过程为：
	 * <ol>
	 * <li>写入2个字节，值为v.length</li>
	 * <li>写入v后返回。</li>
	 * </ol>
	 * 
	 * @param v
	 *            要写入的字节数组
	 * @param isNetByOrder
	 *            长度的2字节是否采用网络字节序
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writePacketShortLen(byte[] v, boolean isNetByteOrder) throws IOException;

	/**
	 * 写入一段经过打包处理的字节。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * writePacketShortLen(v, false)</code>
	 * </pre>
	 * 
	 * @param v
	 *            要写入的字节数组
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writePacketShortLen(byte[] v) throws IOException;

	/**
	 * 写入一段经过打包处理的字节。打包格式为：4个字节表示字节数组的长度+字节数组，实际写入数据的长度为返回的字节数组长度+4.<br>
	 * 如果v.length>65535000，则截取前65535000个字节。写入过程为：
	 * <ol>
	 * <li>写入4个字节，值为v.length</li>
	 * <li>写入v后返回。</li>
	 * </ol>
	 * 
	 * 
	 * @param v
	 *            要写入的字节数组
	 * @param isNetByOrder
	 *            长度的4字节是否采用网络字节序
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writePacketIntLen(byte[] v, boolean isNetByteOrder) throws IOException;

	/**
	 * 写入一段经过打包处理的字节。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * writePacketIntLen(v, false)</code>
	 * </pre>
	 * 
	 * @param v
	 *            要写入的字节数组
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writePacketIntLen(byte[] v) throws IOException;

	/**
	 * 将指定utf-8字符串v写入输出流。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * DataWrite.writeFully(v.getBytes("utf-8"));</code>
	 * </pre>
	 * 
	 * @param len
	 *            要写入的字节长度
	 * @return 写入的字符串
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#writeFully(byte[])
	 */
	void writeString(String v) throws IOException;

	/**
	 * 写入一段经过打包处理的utf-8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * writePacketByteLen(v.getBytes("utf-8"));</code>
	 * </pre>
	 * 
	 * @param v
	 *            要写入的字符串
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#writePacketByteLen()
	 */
	void writePacketByteLenString(String v) throws IOException;

	/**
	 * 写入一段经过打包处理的utf-8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * writePacketShortLen(v.getBytes("utf-8"),isNetByteOrder);</code>
	 * </pre>
	 * 
	 * @param v
	 *            要写入的字符串
	 * @param isNetByteOrder
	 *            包长度是否是网络字节序
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#writePacketShortLen(boolean)
	 */
	void writePacketShortLenString(String v, boolean isNetByteOrder) throws IOException;

	/**
	 * 写入一段经过打包处理的utf-8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * writePacketShortLenString(v,false);</code>
	 * </pre>
	 * 
	 * @param v
	 *            要写入的字符串
	 * @param isNetByteOrder
	 *            包长度是否是网络字节序
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#writePacketShortLen(boolean)
	 */
	void writePacketShortLenString(String v) throws IOException;

	/**
	 * 写入一段经过打包处理的utf-8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * writePacketIntLen(v.getBytes("utf-8"),isNetByteOrder);</code>
	 * </pre>
	 * 
	 * @param v
	 *            要写入的字符串
	 * @param isNetByteOrder
	 *            包长度是否是网络字节序
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#writePacketShortLen(boolean)
	 */
	void writePacketIntLenString(String v, boolean isNetByteOrder) throws IOException;

	/**
	 * 写入一段经过打包处理的utf-8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * writePacketIntLenString(v,false);</code>
	 * </pre>
	 * 
	 * @param v
	 *            要写入的字符串
	 * @param isNetByteOrder
	 *            包长度是否是网络字节序
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataWrite#writePacketShortLen(boolean)
	 */
	void writePacketIntLenString(String v) throws IOException;

	/**
	 * 写入一个byte list
	 * 
	 * @param ls
	 *            byte list
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeByteList(List<Byte> ls) throws IOException;

	/**
	 * 写入一个short list
	 * 
	 * @param ls
	 *            short list
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeShortList(List<Short> ls) throws IOException;

	/**
	 * 写入一个int list
	 * 
	 * @param ls
	 *            int list
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeIntList(List<Integer> ls) throws IOException;

	/**
	 * 写入一个long list
	 * 
	 * @param ls
	 *            long list
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeLongList(List<Long> ls) throws IOException;

	/**
	 * 写入一个float list
	 * 
	 * @param ls
	 *            float list
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeFloatList(List<Float> ls) throws IOException;

	/**
	 * 写入一个double list
	 * 
	 * @param ls
	 *            double list
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void writeDoubleList(List<Double> ls) throws IOException;

	/**
	 * 写入一个 list
	 * 
	 * @param ls
	 *            list
	 * @throws TimeoutException
	 *             如果写数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	<T extends Writable> void writeList(List<T> ls) throws IOException;

}
