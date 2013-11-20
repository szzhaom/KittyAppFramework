package kitty.kaf.io;

import java.io.EOFException;
import java.io.IOException;
import java.util.Date;

import kitty.kaf.exceptions.TimeoutException;

/**
 * DataRead 接口用于从二进制流中读取字节，并重构所有 Java 基本类型数据。
 * 
 * 对于此接口中的所有数据读取例程来说，如果在读取到所需字节数的数据之前已经到达文件末尾 (end of file)，则都将抛出
 * EOFException（IOException 的一种）。如果因为文件末尾以外的其他原因无法读取字节，则抛出 IOException 而不是
 * EOFException。尤其在输入流已关闭的情况下，将抛出
 * IOException。当在指定时间内无法读取相应的数据，则都将抛出TimeoutException(IOException 的一种）
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public interface DataRead {
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
	 * @return 读入缓冲区的字节总数，如果因为已经到达流的末尾而没有更多的数据，则返回 -1。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	int read(byte[] b, int offset, int len) throws IOException;

	/**
	 * 读数据，相当于read(b,0,b.length);
	 * 
	 * @param b
	 *            存储读取数据的缓冲区
	 * @return 读入缓冲区的字节总数，如果因为已经到达流的末尾而没有更多的数据，则返回 -1。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#read(byte[], int, int)
	 */
	int read(byte[] b) throws IOException;

	/**
	 * 从输入流中读取 len 个字节。 <br>
	 * 在出现以下条件之一以前，此方法将阻塞至超时：
	 * <ul>
	 * <li>输入数据的 len 个字节是可用的，在这种情况下，正常返回。</li>
	 * <li>检测到文件末尾，在这种情况下，抛出 EOFException。</li>
	 * <li>
	 * 如果发生 I/O 错误，在这种情况下，将抛出 IOException，而不是 EOFException。</li>
	 * </ul>
	 * 如果 b 为 null，则抛出 NullPointerException。如果 off 为负，或 len 为负，抑或 off+len 大于数组 b
	 * 的长度，则抛出 IndexOutOfBoundsException。如果 len 为零，则不读取字节。否则，将读取的第一个字节存储到元素
	 * b[off] 中，下一个字节存储到 b[off+1] 中，依此类推。读取的字节数至多等于 b[0]。
	 * 
	 * @param b
	 *            存储读取数据的缓冲区
	 * @param offset
	 *            读取的数据存储的起始偏移量
	 * @param len
	 *            读取的最大字节数
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void readFully(byte[] b, int offset, int len) throws IOException;

	/**
	 * 效果等同于readFully(b,0,b.length);
	 * 
	 * @param b
	 *            存储读取数据的缓冲区
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#readFully(byte[], int, int)
	 */
	void readFully(byte[] b) throws IOException;

	/**
	 * 完整从输入流中读取长度为len的字节数组。
	 * 
	 * @param len
	 *            要读取的字节数组长度
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#readFully(byte[], int, int)
	 */
	byte[] readFully(int len) throws IOException;

	/**
	 * 读取一个输入字节，如果该字节不是零，则返回 true，如果是零，则返回 false。此方法适用于读取用接口 DataWrite 的
	 * writeBoolean 方法写入的字节。
	 * 
	 * @return 读取的boolean值。
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	boolean readBoolean() throws IOException;

	/**
	 * 读取并返回一个输入字节。该字节被看作是 -128 到 127（包含）范围内的一个有符号值。此方法适用于读取用接口 DataWrite 的
	 * writeByte 方法写入的字节。
	 * 
	 * @return 读取的8位值。
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	byte readByte() throws IOException;

	/**
	 * 读取一个输入字节，将它左侧补零 (zero-extend) 转变为 int 类型，并返回结果，所以结果的范围是 0 到 255。如果接口
	 * DataWrite 的 writeByte 方法的参数是 0 到 255 之间的值，则此方法适用于读取用 writeByte 写入的字节。
	 * 
	 * @return 读取的无符号8位值。
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	int readUnsignedByte() throws IOException;

	/**
	 * 读取两个输入字节并返回一个 short 值。设 a 为第一个读取字节，b 为第二个读取字节。返回的值是：
	 * 
	 * <pre>
	 * <code>
	 * (short)((a << 8) | (b & 0xff))</code>
	 * </pre>
	 * 
	 * 如果是网络字节序，则a,b顺序反转。此方法适用于读取用接口 DataWrite 的 writeShort 方法写入的字节。
	 * 
	 * @param isNetByteOrder
	 *            是否是网络字节序。
	 * @return 读取的16位值。
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	short readShort(boolean isNetByteOrder) throws IOException;

	/**
	 * 读取Short值
	 * 
	 * @return 读到的Short值
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	short readShort() throws IOException;

	/**
	 * 读取两个输入字节，并返回 0 到 65535 范围内的一个 int 值。设 a 为第一个读取字节，b 为第二个读取字节。返回的值是：
	 * 
	 * <pre>
	 * <code>
	 * (((a & 0xff) << 8) | (b & 0xff))</code>
	 * </pre>
	 * 
	 * 此方法适用于读取用接口 DataWrite 的 writeShort 方法写入的字节。
	 * 
	 * @param isNetByteOrder
	 *            是否是网络字节序。
	 * @return 读取的无符号16位值。
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	int readUnsignedShort(boolean isNetByteOrder) throws IOException;

	/**
	 * 读取无符号Short值
	 * 
	 * @return 读到的无符号Short值
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	int readUnsignedShort() throws IOException;

	/**
	 * 读取四个输入字节并返回一个 int 值。设 a 为第一个读取字节，b 为第二个读取字节，c 为第三个读取字节，d 为第四个读取字节。返回的值是：
	 * 
	 * <pre>
	 * <code>
	 * (((a & 0xff) << 24) | ((b & 0xff) << 16) |
	 *   ((c & 0xff) << 8) | (d & 0xff))</code>
	 * </pre>
	 * 
	 * 如果是网络字节序，则a,b,c,d顺序反转。此方法适用于读取用接口 DataWrite 的 writeInt 方法写入的字节。
	 * 
	 * @param isNetByteOrder
	 *            是否是网络字节序。
	 * @return 读取的int值。
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	int readInt(boolean isNetByteOrder) throws IOException;

	/**
	 * 读取Int值
	 * 
	 * @return 读到的Int值
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	int readInt() throws IOException;

	/**
	 * 读取八个输入字节并返回一个 long 值。设 a 为第一个读取字节，b 为第二个读取字节，c 为第三个读取字节，d 为第四个读取字节，e
	 * 为五个读取字节，f 为第六个读取字节，g 为第七个读取字节，h 为第八个读取字节。返回的值是：
	 * 
	 * <pre>
	 * <code>
	 * (((long)(a & 0xff) << 56) |
	 *   ((long)(b & 0xff) << 48) |
	 *   ((long)(c & 0xff) << 40) |
	 *   ((long)(d & 0xff) << 32) |
	 *   ((long)(e & 0xff) << 24) |
	 *   ((long)(f & 0xff) << 16) |
	 *   ((long)(g & 0xff) <<  8) |
	 *   ((long)(h & 0xff)))</code>
	 * </pre>
	 * 
	 * 此方法适用于读取用接口 DataWrite 的 writeInt 方法写入的字节。
	 * 
	 * @return 读取的long值。
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	long readLong() throws IOException;

	/**
	 * 读取四个输入字节并返回一个 float 值。实现这一点的方法是：先使用与 readInt 方法完全相同的方式构造一个 int 值，然后使用与
	 * Float.intBitsToFloat 方法完全相同的方式将此 int 值转换成一个 float 值。此方法适用于读取用接口 DataWrite
	 * 的 writeFloat 方法写入的字节。
	 * 
	 * @return 读取的 float 值
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	float readFloat() throws IOException;

	/**
	 * 读取八个输入字节并返回一个 double 值。实现这一点的方法是：先使用与 readlong 方法完全相同的方式构造一个 long 值，然后使用与
	 * Double.longBitsToDouble 方法完全相同的方式将此 long 值转换成一个 double 值。此方法适用于读取用接口
	 * DataWrite 的 writeDouble 方法写入的字节。
	 * 
	 * @return 读取的 double 值
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	double readDouble() throws IOException;

	/**
	 * 读取八个输入字节并返回一个 Date 值。此方法适用于读取用接口 DataWrite 的 writeDate 方法写入的字节。
	 * 
	 * @return 读取的 double 值
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */

	Date readDate() throws IOException;

	/**
	 * 读取一段经过打包处理的字节。打包格式为：1个字节表示字节数组的长度+字节数组，实际读取数据的长度为返回的字节数组长度+1，读取过程为：
	 * <ol>
	 * <li>读取一个字节，得到要读取的后续字节数组的长度n</li>
	 * <li>读取n个字节后返回。</li>
	 * </ol>
	 * 此方法适用于读取用接口 DataWrite 的 writePacketByteLen 方法写入的字节。
	 * 
	 * @return 读取的字节数组
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	byte[] readPacketByteLen() throws IOException;

	/**
	 * 读取一段经过打包处理的字节。打包格式为：2个字节表示字节数组的长度+字节数组，实际读取数据的长度为返回的字节数组长度+2，读取过程为：
	 * <ol>
	 * <li>读取2个字节，得到要读取的后续字节数组的长度n</li>
	 * <li>读取n个字节后返回。</li>
	 * </ol>
	 * 此方法适用于读取用接口 DataWrite 的 writePacketShortLen 方法写入的字节。
	 * 
	 * @param isNetByOrder
	 *            长度的2字节是否采用网络字节序
	 * @return 读取的字节数组
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	byte[] readPacketShortLen(boolean isNetByteOrder) throws IOException;

	/**
	 * 读取打包字节。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * readPacketShortLen(false);</code>
	 * </pre>
	 * 
	 * @return 读取的字节数组
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	byte[] readPacketShortLen() throws IOException;

	/**
	 * 读取一段经过打包处理的字节。打包格式为：4个字节表示字节数组的长度+字节数组，实际读取数据的长度为返回的字节数组长度+4，读取过程为：
	 * <ol>
	 * <li>读取4个字节，得到要读取的后续字节数组的长度n</li>
	 * <li>读取n个字节后返回。</li>
	 * </ol>
	 * 此方法适用于读取用接口 DataWrite 的 writePacketIntLen 方法写入的字节。
	 * 
	 * @param isNetByOrder
	 *            长度的4字节是否采用网络字节序
	 * @return 读取的字节数组
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	byte[] readPacketIntLen(boolean isNetByteOrder) throws IOException;

	/**
	 * 读取打包字节。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * readPacketIntLen(false);</code>
	 * </pre>
	 * 
	 * @return 读取的字节数组
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	byte[] readPacketIntLen() throws IOException;

	/**
	 * 试图在输入流中跳过数据的 n 个字节，并丢弃跳过的字节。
	 * 
	 * @param n
	 *            要跳过的字节数
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void skipBytes(int n) throws IOException;

	/**
	 * 读取 len 长度的字节，转换成字符串后返回。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * byte[] b = new byte[len];
	 * DataRead.readFully(b);
	 * return new String(b);</code>
	 * </pre>
	 * 
	 * @param len
	 *            要读取的字节长度
	 * @return 读取的字符串
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#readFully(byte[])
	 */
	String readString(int len) throws IOException;

	/**
	 * 读取一段经过打包处理的utf8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * new String(readPacketByteLen(),"utf-8")</code>
	 * </pre>
	 * 
	 * @param len
	 *            要读取的字节长度
	 * @return 读取的字符串
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#readPacketByteLen()
	 */
	String readPacketByteLenString() throws IOException;

	/**
	 * 读取一段经过打包处理的utf8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * new String(readPacketShortLen(isNetByteOrder),"utf-8")</code>
	 * </pre>
	 * 
	 * @param isNetByteOrder
	 *            包长度是否是网络字节序
	 * @return 读取的字符串
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#readPacketShortLen(boolean)
	 */
	String readPacketShortLenString(boolean isNetByteOrder) throws IOException;

	/**
	 * 用正常的字节序读取一段经过打包处理的utf8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * readPacketShortLen(false)</code>
	 * </pre>
	 * 
	 * @param isNetByteOrder
	 *            包长度是否是网络字节序
	 * @return 读取的字符串
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#readPacketShortLen(boolean)
	 */
	String readPacketShortLenString() throws IOException;

	/**
	 * 读取一段经过打包处理的utf8字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * new String(readPacketIntLen(isNetByteOrder),"utf-8")</code>
	 * </pre>
	 * 
	 * @param isNetByteOrder
	 *            包长度是否是网络字节序
	 * @return 读取的字符串
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#readPacketShortLen(boolean)
	 */
	String readPacketIntLenString(boolean isNetByteOrder) throws IOException;

	/**
	 * 用正常的字节序读取一段经过打包处理的字符串。等效代码：
	 * 
	 * <pre>
	 * <code>
	 * readPacketIntLenString(false)</code>
	 * </pre>
	 * 
	 * @return 读取的字符串
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 * @see DataRead#readPacketShortLen(boolean)
	 */
	String readPacketIntLenString() throws IOException;

	/**
	 * 读取字节，直到遇到行结束符eofs后返回。
	 * 
	 * @param eofs
	 *            行结束符
	 * @param returnIncludeEofs
	 *            返回数据中是否包含eofs
	 * @return 读取的字节数组
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	byte[] readln(byte[] eofs, boolean returnIncludeEofs) throws IOException;
}
