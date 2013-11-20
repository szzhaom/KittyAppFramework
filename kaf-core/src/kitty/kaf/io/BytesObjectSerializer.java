package kitty.kaf.io;

import java.io.IOException;

/**
 * 内存数据序列化接口
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public interface BytesObjectSerializer<E> {
	/**
	 * Java对象转换为包含内存字节数组的对象
	 * 
	 * @param value
	 *            Java对象
	 * @return 转换后的包含内存字节数组的对象
	 * @throws IOException
	 *             如果转换失败
	 */
	E objectToBytes(Object value) throws IOException;

	/**
	 * 包含内存字节数组的对象转换为Java对象
	 * 
	 * @param value
	 *            包含内存字节数组的对象
	 * @return 转换后的Java对象
	 * @throws IOException
	 *             如果转换失败
	 */
	Object bytesToObject(E value) throws IOException;
}
