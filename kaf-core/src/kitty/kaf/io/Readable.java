package kitty.kaf.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;

import kitty.kaf.exceptions.TimeoutException;

/**
 * 具备可读能力的接口
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public interface Readable extends Serializable {
	/**
	 * 从输入流中读取数据到接口中。适用读取Writable#writeToStream写入的数据
	 * 
	 * @param stream
	 *            输入流
	 * @throws EOFException
	 *             如果此流在读取所有字节之前到达末尾。
	 * @throws TimeoutException
	 *             如果读数据超时
	 * @throws IOException
	 *             如果发生IO错误
	 */
	void readFromStream(DataRead stream) throws IOException;
}
