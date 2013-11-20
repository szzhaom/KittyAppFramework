package kitty.kaf.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基于字节数组处理的助手工具
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class BytesHelper {
	public static void main(String[] args) {
		String[] array = new String[] { "1", "2", "3", "4" };
		List<String> s = Arrays.asList(array), s1 = new ArrayList<String>();
		list(s, "", "-", 0, s1);
		System.out.println(s1.toString());
	}

	public static void list(List<String> s, String prefix, String sp, int from, List<String> dest) {
		if (prefix.length() > 0)
			dest.add(prefix);
		for (int i = from; i < s.size(); i++) {
			String p = s.get(i);
			if (prefix.length() > 0 && !prefix.endsWith(sp))
				prefix += sp;
			list(s, prefix + p, sp, i + 1, dest);
		}
	}

	/**
	 * 内存批量填充
	 * 
	 * @param src
	 *            要填充的内存数组块
	 * @param ch
	 *            要填充的值
	 * @param offset
	 *            从src的哪个位置开始填充
	 * @param len
	 *            填充长度
	 */
	static public void memset(byte[] src, int ch, int offset, int len) {
		if (len <= 0 || src == null)
			return;
		if (offset < 0)
			offset = 0;
		if (offset + len > src.length)
			return;
		for (int i = offset; i < len; i++)
			src[i] = (byte) ch;
	}

	/**
	 * 内存比较
	 * 
	 * @param src
	 *            源字节数组
	 * @param srcpos
	 *            从src的哪个位置开始比较
	 * @param dest
	 *            目标字节数组
	 * @param destpos
	 *            从dest的哪个位置开始比较
	 * @param len
	 *            要比较的长度
	 * @return 0 - 两者相等 <br>
	 *         <0 - src < dest <br>
	 *         >0 - src > dest
	 */
	public static int memcmp(byte[] src, int srcpos, byte[] dest, int destpos, int len) {
		for (int i = 0; i < len; i++) {
			int si = srcpos + i;
			int di = destpos + i;
			if (si >= src.length) {
				if (di >= dest.length)
					return 0;
				else
					return -1;
			} else if (di >= dest.length)
				return 1;
			int ret = src[si] - dest[di];
			if (ret != 0)
				return ret;
		}
		return 0;
	}

	/**
	 * 将字节转换为无符号数
	 * 
	 * @param b
	 *            字节值
	 * @return 无符号数
	 */
	static public int byteToUnsigned(byte b) {
		return b & 0xff;
	}

	/**
	 * 将short转换为无符号数
	 * 
	 * @param b
	 *            要转换的short值
	 * @return 无符号数
	 */
	static public int shortToUnsigned(short b) {
		return b & 0xffff;
	}

	/**
	 * 二进制字节数组转浮点数,一个浮点数占4个字节,应该保证b.length>=offset+4
	 * 
	 * @param b
	 *            要转换二进制字节数组
	 * @param offset
	 *            偏移量,即从字节数组的第几个几字开始转换
	 * @return 转换后的浮点数
	 */
	static public float bytesToFloat(byte[] b, int offset) {
		return Float.intBitsToFloat(bytesToInt(b, offset));
	}

	/**
	 * 浮点数转换为二进制字节数组
	 * 
	 * @param f
	 *            要转换浮点数
	 * @return 转换后的二进制字节数组
	 */
	static public byte[] floatToBytes(float f) {
		return intToBytes(Float.floatToIntBits(f));
	}

	/**
	 * 二进制字节数组转双精度浮点数,一个双精度浮点数占8个字节,应该保证b.length>=offset+8
	 * 
	 * @param b
	 *            要转换二进制字节数组
	 * @param offset
	 *            偏移量,即从字节数组的第几个几字开始转换
	 * @return 转换后的双精度浮点数
	 */
	static public double bytesToDouble(byte[] b, int offset) {
		return Double.longBitsToDouble(bytesToLong(b, offset));
	}

	/**
	 * 双精浮点数转换为二进制字节数组
	 * 
	 * @param f
	 *            要转换双精浮点数
	 * @return 转换后的二进制字节数组
	 */
	static public byte[] doubleToBytes(double f) {
		return longToBytes(Double.doubleToLongBits(f));
	}

	/**
	 * 二进制字节数组转长整数,一个长整数占8个字节,由于一个整数可以只占1~8个字节,本函数可以从 要转换的字节数组,提取1~8个字节来转换.
	 * 
	 * @param b
	 *            要转换二进制字节数组
	 * @param offset
	 *            偏移量,即从字节数组的第几个几字开始转换
	 * @param len
	 *            长度.将多少个字节转换成长整数
	 * @return 转换后长整数
	 */
	static public long bytesToLong(byte[] b, int offset, int len) {
		long tmp = 0;
		long ret = 0;
		for (int i = 0; i < len; i++) {
			tmp = (b[offset + i] & 0xff);
			tmp = tmp << (i * 8);
			ret |= tmp;
		}
		return ret;
	}

	/**
	 * 二进制字节数组转长整数,一个长整数占8个字节,要确保b.length>=offset+8.
	 * 
	 * @param b
	 *            要转换二进制字节数组
	 * @param offset
	 *            偏移量,即从字节数组的第几个几字开始转换
	 * @return 转换后长整数
	 */
	static public long bytesToLong(byte[] b, int offset) {
		return bytesToLong(b, offset, 8);
	}

	/**
	 * 长整数转换成二进制字节数组
	 * 
	 * @param v
	 *            要转换的长整数
	 * @return 转换后的二进制字节数组
	 */
	static public byte[] longToBytes(long v) {
		byte[] ret = new byte[8];
		for (int i = 0; i < 8; i++) {
			ret[i] = (byte) ((v >> (i * 8)) & 0xff);
		}
		return ret;
	}

	/**
	 * 长整数转换成二进制字节数组,只转换低len位字节
	 * 
	 * @param v
	 *            要转换的长整数
	 * @param len
	 *            转换后的字节长度
	 * @return 转换后的二进制字节数组
	 */
	static public byte[] longToBytes(long v, int len) {
		byte[] ret = new byte[len];
		for (int i = 0; i < len; i++) {
			ret[i] = (byte) ((v >> (i * 8)) & 0xff);
		}
		return ret;
	}

	/**
	 * 二进制字节数组转换成整数,应确保b.length>=offset+4
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            转换偏移量,基于字节数组b
	 * @return 转换后的整数
	 */
	static public int bytesToInt(byte[] b, int offset) {
		return (int) bytesToLong(b, offset, 4);
	}

	/**
	 * 整数转换成二进制字节数组
	 * 
	 * @param v
	 *            要转换的整数
	 * @return 转换后的二进制字节数组
	 */
	static public byte[] intToBytes(int v) {
		byte[] ret = new byte[4];
		for (int i = 0; i < 4; i++) {
			ret[i] = (byte) ((v >> (i * 8)) & 0xff);
		}
		return ret;
	}

	/**
	 * 二进制字节数组转换成短整数
	 * 
	 * @param b
	 *            二进制字节数组
	 * @param offset
	 *            转换偏移量
	 * @return 转换后的短整数
	 */
	static public short bytesToShort(byte[] b, int offset) {
		return (short) bytesToLong(b, offset, 2);
	}

	/**
	 * 短整数转换成二进制字节数组
	 * 
	 * @param v
	 *            要转换的短整数
	 * @return 转换后的二进制字节数组
	 */
	static public byte[] shortToBytes(short v) {
		byte[] ret = new byte[2];
		for (int i = 0; i < 2; i++) {
			ret[i] = (byte) ((v >> (i * 8)) & 0xff);
		}
		return ret;
	}

	/**
	 * 字节反相输出,即高位字节和低位字节互换
	 * 
	 * @param b
	 *            字节数组
	 * @param offset
	 *            偏移量
	 * @param len
	 *            转换长度
	 * @return 反相输出后的字节数组
	 */
	static public byte[] bytesReverse(byte[] b, int offset, int len) {
		byte[] ret = new byte[len];
		for (int i = 0; i < len; i++)
			ret[i] = b[len + offset - i - 1];
		return ret;
	}

	/**
	 * 将主机字节的整数转换为网络字节的整数
	 * 
	 * @param b
	 *            主机字节的整数
	 * @return 网络字节的整数
	 */
	static int htonl(int b) {
		return htonl(intToBytes(b), 0);
	}

	/**
	 * 将主机字节的二进制字节数组转换为网络字节的整数,应确保b.length>=offset+4
	 * 
	 * @param b
	 *            主机字节数组
	 * @param offset
	 *            转换字节数组的偏移量
	 * @return 网络字节的整数
	 */
	static int htonl(byte[] b, int offset) {
		return bytesToInt(bytesReverse(b, offset, 4), 0);
	}

	/**
	 * 将主机字节的整数转换为网络字节的短整数
	 * 
	 * @param b
	 *            主机字节的整数
	 * @return 网络字节的整数
	 */
	static short htons(short b) {
		return htons(shortToBytes(b), 0);
	}

	/**
	 * 将主机字节的二进制字节数组转换为网络字节的短整数,应确保b.length>=offset+2
	 * 
	 * @param b
	 *            主机字节数组
	 * @param offset
	 *            转换字节数组的偏移量
	 * @return 网络字节的整数
	 */
	static short htons(byte[] b, int offset) {
		return bytesToShort(bytesReverse(b, offset, 2), 0);
	}

	/**
	 * 将网络字节的整数转换为主机字节的整数
	 * 
	 * @param b
	 *            网络字节的整数
	 * @return 主机字节的整数
	 */
	static int ntohl(int b) {
		return ntohl(intToBytes(b), 0);
	}

	/**
	 * 将网络字节的二进制字节数组转换为主机字节的整数,应确保b.length>=offset+4
	 * 
	 * @param b
	 *            网络字节数组
	 * @param offset
	 *            转换字节数组的偏移量
	 * @return 主机字节的整数
	 */
	static int ntohl(byte[] b, int offset) {
		return bytesToInt(bytesReverse(b, offset, 4), 0);
	}

	/**
	 * 将网络字节的整数转换为主机字节的短整数
	 * 
	 * @param b
	 *            网络字节的整数
	 * @return 主机字节的整数
	 */
	static short ntohs(short b) {
		return ntohs(shortToBytes(b), 0);
	}

	/**
	 * 将网络字节的二进制字节数组转换为主机字节的短整数,应确保b.length>=offset+2
	 * 
	 * @param b
	 *            网络字节数组
	 * @param offset
	 *            转换字节数组的偏移量
	 * @return 主机网络字节的整数
	 */
	static short ntohs(byte[] b, int offset) {
		return bytesToShort(bytesReverse(b, offset, 2), 0);
	}

}
