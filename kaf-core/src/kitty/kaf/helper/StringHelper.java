package kitty.kaf.helper;

import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 字符串处理相关的助手工具
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class StringHelper {
	/**
	 * 格式化内存显示。小于1g字节显示为m，否则显示为g
	 * 
	 * @param size
	 * @return
	 */
	public static String formatMemory(long size) {
		double m = (double) size / (1024 * 1024);
		if (m < 1024)
			return String.format("%.3f", m) + "m";
		else
			return String.format("%.3f", m / 1024) + "g";
	}

	/**
	 * 从file://XXXX中提取XXXX
	 * 
	 * @param fileUrl
	 *            要提取的URL
	 * @return 去除file://后的路径字串
	 */
	public static String parseFileUrlPath(String fileUrl) {
		if (fileUrl != null && !fileUrl.isEmpty()) {
			if (fileUrl.toLowerCase().startsWith("file:")) {
				fileUrl = fileUrl.substring(5);
				fileUrl = fileUrl.replace("//", "/");
				if (fileUrl.indexOf(":") > -1 && fileUrl.startsWith("/"))
					fileUrl = fileUrl.substring(1);
			}
		}
		return fileUrl;
	}

	/**
	 * 获取包含环境变量的字符串，${变量名}
	 * 
	 * @param text
	 *            原始字串
	 * @return 转换后的字串
	 */
	public static String getAttributeText(String text) {
		char[] b = text.toCharArray();
		StringBuffer buf = new StringBuffer("");
		int markIndex = -1;
		int lastIndex = 0;
		for (int i = 0; i < b.length; i++) {
			char c = b[i];
			if (markIndex > -1) {
				if (c == '}') {
					buf.append(b, lastIndex, markIndex - lastIndex - 2);
					String attr = new String(b, markIndex, i - markIndex);
					buf.append(System.getProperty(attr, ""));
					lastIndex = i + 1;
					markIndex = -1;
				}
			} else if (c == '$') {
				if (b[i + 1] == '{') {
					i++;
					markIndex = i + 1;
				}
			}
		}
		if (lastIndex < b.length) {
			buf.append(b, lastIndex, b.length - lastIndex);
		}
		return buf.toString();
	}

	/**
	 * 分隔字串，字串v中遇到字串s打断，依次打断成多个字串，添加到ls中
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @param ls
	 *            保存分隔的字串列表
	 */

	public static void split(String v, String s, List<String> ls) {
		if (v == null)
			return;
		int index = v.indexOf(s);
		int size = s.length();
		if (index >= 0) {
			while (index >= 0) {
				ls.add(v.substring(0, index));
				v = v.substring(index + size);
				index = v.indexOf(s);
			}
			ls.add(v);
		} else if (v.length() > 0)
			ls.add(v);
	}

	/**
	 * 分隔字串，字串v中遇到字串s打断，依次打断成多个字串，添加到ls中，忽略空行
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @param ls
	 *            保存分隔的字串列表
	 */

	public static void splitIngoreEmptyLine(String v, String s, List<String> ls) {
		if (v == null)
			return;
		v = v.trim();
		int index = v.indexOf(s);
		int size = s.length();
		if (index >= 0) {
			while (index >= 0) {
				String v1 = v.substring(0, index).trim();
				if (v1.length() > 0)
					ls.add(v.substring(0, index));
				v = v.substring(index + size).trim();
				index = v.indexOf(s);
			}
			if (v.length() > 0)
				ls.add(v);
		} else if (v.length() > 0)
			ls.add(v);
	}

	/**
	 * 分隔字串，字串v中遇到字串s打断，依次打断成多个字串返回
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @return 分隔后字串数组
	 */
	public static String[] splitToStringArray(String v, String s) {
		if (v == null || v.trim().length() == 0)
			return new String[0];
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		String[] r = new String[ls.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = ls.get(i);
		ls = null;
		return r;
	}

	/**
	 * 分隔字串，字串v中遇到字串s打断，依次打断成多个字串返回，忽略空行
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @return 分隔后字串数组
	 */
	public static String[] splitToStringArrayIngoreEmptyLine(String v, String s) {
		if (v == null || v.trim().length() == 0)
			return new String[0];
		ArrayList<String> ls = new ArrayList<String>();
		splitIngoreEmptyLine(v, s, ls);
		String[] r = new String[ls.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = ls.get(i);
		ls = null;
		return r;
	}

	/**
	 * 分隔字串，并将分隔后的字串转换成整数，保存到List中返回
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @return 分隔后的整数列表
	 */
	public static List<Integer> splitToIntList(String v, String s) {
		if (v == null)
			return null;
		ArrayList<Integer> r = new ArrayList<Integer>();
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Integer.valueOf(str));
			}
		}
		ls = null;
		return r;
	}

	/**
	 * 分隔字串，并将分隔后的字串转换成长整数，保存到List中返回
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @return 分隔后的长整数列表
	 */
	public static List<Long> splitToLongList(String v, String s) {
		if (v == null)
			return null;
		ArrayList<Long> r = new ArrayList<Long>();
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Long.valueOf(str));
			}
		}
		ls = null;
		return r;
	}

	/**
	 * 分隔字串，并将分隔后的字串转换成整数，保存到Integer[]中返回
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @return 分隔后的整数数组
	 */
	public static Integer[] splitToIntArray(String v, String s) {
		List<Integer> r = splitToIntList(v, s);
		Integer ri[] = new Integer[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	/**
	 * 分隔字串，并将分隔后的字串转换成整数，保存到Long[]中返回
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @return 分隔后的长整数数组
	 */

	public static Long[] splitToLongArray(String v, String s) {
		List<Long> r = splitToLongList(v, s);
		Long ri[] = new Long[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	/**
	 * 分隔字串，并将分隔后的字串转换成短整数，保存到List中返回
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @return 分隔后的短整数列表
	 */
	public static List<Short> splitToShortList(String v, String s) {
		if (v == null)
			return null;
		ArrayList<Short> r = new ArrayList<Short>();
		ArrayList<String> ls = new ArrayList<String>();
		split(v, s, ls);
		for (int i = 0; i < ls.size(); i++) {
			String str = ls.get(i);
			if (str != null) {
				str = str.trim();
				if (!str.isEmpty())
					r.add(Short.valueOf(str));
			}
		}
		ls = null;
		return r;
	}

	/**
	 * 分隔字串，并将分隔后的字串转换成短整数，保存到Short[]中返回
	 * 
	 * @param v
	 *            要分隔的字串
	 * @param s
	 *            分隔字串
	 * @return 分隔后的短整数数组
	 */
	public static Short[] splitToShortArray(String v, String s) {
		List<Short> r = splitToShortList(v, s);
		Short ri[] = new Short[r.size()];
		for (int i = 0; i < ri.length; i++)
			ri[i] = r.get(i);
		return ri;
	}

	/**
	 * 首字大写
	 * 
	 * @param o
	 * @return
	 */
	public static String firstWordCap(String o) {
		if (o != null && o.length() > 0)
			return o.substring(0, 1).toUpperCase() + o.substring(1);
		else
			return o;
	}

	/**
	 * 首字小写
	 * 
	 * @param o
	 * @return
	 */
	public static String firstWordLower(String o) {
		if (o != null && o.length() > 0)
			return o.substring(0, 1).toLowerCase() + o.substring(1);
		else
			return o;
	}

	/**
	 * 补足字串。如果src.length()>=length 直接返回src，否则，根据对齐方式用fillChar补足长度后返回
	 * 
	 * @param src
	 *            原始字串
	 * @param align
	 *            对齐方式：left,center,right
	 * @param fillChar
	 *            不足长度时补足的字符
	 * @param length
	 *            补足后的字串字节长度
	 * @return 补足后的字串
	 */
	public static String fillString(String src, String align, int fillChar, int length) {
		int len = 0;
		for (int i = 0; i < src.length(); i++) {
			if (src.charAt(i) > 255)
				len += 2;
			else
				len++;
		}
		int leftlen = length - len;
		if (leftlen > 0) {
			if (align.equalsIgnoreCase("left"))
				return src + fillChar(fillChar, leftlen);
			else if (align.equalsIgnoreCase("right"))
				return fillChar(fillChar, leftlen) + src;
			else
				return fillChar(fillChar, leftlen / 2) + src + fillChar(fillChar, leftlen - leftlen / 2);
		} else
			return src;
	}

	/**
	 * 将一串字符按固定宽度分成多行文字
	 * 
	 * @param src
	 *            源字符串
	 * @param align
	 *            对齐方式：left-左对齐;right-右对齐;center-居中对齐
	 * @param fillchar
	 *            对齐时填充的空白字符
	 * @param width
	 *            每行的宽度
	 * @param maxLines
	 *            最大的行数
	 * @param ls
	 *            输出的多行文字列表
	 */
	public static void wordBreak(String src, String align, byte fillchar, int width, int maxLines, List<String> ls) {
		if (src == null)
			src = "";
		String f = Character.toString((char) fillchar), lstr = "", rstr = "";
		int pos = 0, clen = 0, tl;
		for (int i = 0; i < src.length(); i++) {
			if (src.charAt(i) > 255)
				tl = 2;
			else
				tl = 1;
			if (clen + tl > width) {
				if (clen < width)
					ls.add(src.substring(pos, i) + f);
				else
					ls.add(src.substring(pos, i));
				if (maxLines > 0 && ls.size() >= maxLines)
					return;
				pos = i;
				clen = 0;
			}
			clen += tl;
		}
		clen = width - clen;
		if (clen > 0) {
			if (align.equalsIgnoreCase("left")) {
				rstr = fillChar(fillchar, clen);
			} else if (align.equalsIgnoreCase("right")) {
				lstr = fillChar(fillchar, clen);
			} else {
				int l = clen / 2;
				if (l > 0)
					lstr = fillChar(fillchar, l);
				rstr = fillChar(fillchar, clen - l);
			}
		}
		ls.add(lstr + src.substring(pos, src.length()) + rstr);
	}

	/**
	 * 获取一段连续len个ch的字符串
	 * 
	 * @param ch
	 *            每个字节的值
	 * @param len
	 *            字节长度
	 * @return 获取的字串
	 */
	public static String fillChar(int ch, int len) {
		byte b[] = new byte[len];
		BytesHelper.memset(b, ch, 0, len);
		return new String(b);
	}

	/**
	 * 人民币大写金额转换
	 * 
	 * @param money
	 *            以分为单位的金额数字
	 * @return 转换后的大写人民币
	 */
	public static String upperRMB(long money) {
		long fen = money % 100; // 分
		long jiao = fen / 10; // 角
		fen %= 10;
		money /= 100;
		if (money > 1000000000000L)
			throw new InvalidParameterException("参数不能大于1万亿");
		char[] ch = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' };
		String[] radices = { "", "拾", "佰", "仟" };
		String[] bigRadices = { "", "万", "亿" };
		int zeroCount = 0;
		String ret = "";
		char a[] = Long.toString(money).toCharArray();
		for (int i = 0; i < a.length; i++) {
			int p = a.length - i - 1;
			char d = a[i];
			int quotient = p / 4;
			int modulus = p % 4;
			if (d == '0') {
				zeroCount++;
			} else {
				if (zeroCount > 0) {
					ret += ch[0];
				}
				zeroCount = 0;
				ret += ch[d - '0'] + radices[modulus];
			}
			if (modulus == 0 && zeroCount < 4) {
				ret += bigRadices[quotient];
			}
		}
		if (ret.length() > 0)
			ret += "元";
		if (jiao == 0 && fen == 0) {
			if (ret.length() == 0)
				ret = "零元整";
			else
				ret += "整";
		} else {
			if (jiao > 0) {
				ret += ch[(int) jiao] + "角";
			}
			if (fen > 0) {
				if (ret.length() > 0 && jiao == 0)
					ret += "零";
				ret += ch[(int) fen] + "分";
			}
		}
		return ret;
	}

	/**
	 * 将数字转化为小写人民币，含千分位符
	 * 
	 * @param number
	 *            金额数字，单位：分
	 * @return 转换后的小写人民币
	 */
	public static String lowerRMB(long number) {
		String str = Long.toString(number);
		if (str.length() == 1) {
			return "0.0" + str;
		} else if (str.length() == 2) {
			return "0." + str;
		} else {
			String f = str.substring(str.length() - 2, str.length());
			char[] a = str.substring(0, str.length() - 2).toCharArray();
			String ret = "";
			for (int i = 0; i < a.length; i++) {
				if ((a.length - i) % 3 == 0 && i > 0)
					ret += ",";
				ret += a[i];
			}
			return ret + "." + f;
		}
	}

	/**
	 * 分离字符串，分隔符为不可见的空白字符（即打印的时候不可见的区域），连续的空白视为一个空白符，首尾的空白区域将被忽略
	 * 
	 * @param src
	 *            源字串
	 * @param out
	 *            分离后的字串列表
	 */
	public static void splitBlank(String src, List<String> out) {
		int f = -1;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c <= ' ') {
				if (f >= 0) {
					out.add(src.substring(f, i));
					f = -1;
				}
			} else {
				if (f == -1)
					f = i;
			}
		}
		if (f >= 0)
			out.add(src.substring(f));
	}

	/**
	 * 分离字符串，分隔符为不可见的空白字符（即打印的时候不可见的区域），连续的空白视为一个空白符，首尾的空白区域将被忽略
	 * 
	 * @param src
	 *            源字串
	 * @return 分离后的字串列表
	 */
	public static String[] splitBlank(String src) {
		ArrayList<String> ls = new ArrayList<String>();
		splitBlank(src, ls);
		String[] r = new String[ls.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = ls.get(i);
		return r;
	}

	/**
	 * 返回字符串src的前len个字串（以字节为单位计数）
	 * 
	 * @param src
	 *            原始字串
	 * @param len
	 *            指定的字节长度
	 * @return 处理后的字串
	 */
	public static String checkBytesLength(String src, int len) {
		if (src == null)
			return "";
		if (src.getBytes().length < len)
			return src;
		int l = 0;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c > 255)
				l += 2;
			else
				l += 1;
			if (l == len)
				return src.substring(0, i + 1);
			else if (l > len)
				return src.substring(0, i);
		}
		return src;
	}

	/**
	 * 获取字符串的字节长度
	 * 
	 * @param str
	 *            被检查的字符串
	 * @return 字节长度
	 */
	public static int getBytesLength(String str) {
		int c = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) > 255)
				c++;
			c++;
		}
		return c;
	}

	/**
	 * 元转换为分
	 * 
	 * @param src
	 *            原始字串
	 * @return 转换后整数,以分为单位
	 */
	static public int yuanToFen(String src) {
		src = src.trim();
		if (src.length() == 0)
			return 0;
		int f = 1;
		if (src.getBytes()[0] == '-') {
			f = -1;
			src = src.substring(1);
		}
		int r = 0;
		if (src.length() > 0) {
			int pos = src.indexOf(".");
			if (pos < 0) {
				r = Integer.parseInt(src);
			} else {
				String s = src.substring(0, pos);
				src = src.substring(pos + 1);
				int len = src.length();
				if (len > 2)
					src = src.substring(0, 2);
				else if (len == 0)
					src = "00";
				else if (len == 1)
					src = src + "0";
				r = Integer.parseInt(s + src);
			}
		}
		return r * f;
	}

	/**
	 * 分转换为元
	 * 
	 * @param src
	 *            分
	 * @return 元
	 */
	static public String fenToYuan(int src) {
		String f = "";
		if (src < 0) {
			f = "-";
			src = -src;
		}
		if (src < 10)
			return f + "0.0" + Integer.toString(src);
		else if (src < 100)
			return f + "0." + Integer.toString(src);
		else {
			int i = src / 100;
			int s = src % 100;
			if (s < 10)
				return f + Integer.toString(i) + ".0" + Integer.toString(s);
			else
				return f + Integer.toString(i) + "." + Integer.toString(s);
		}
	}

	/**
	 * 分转换为元
	 * 
	 * @param src
	 *            分
	 * @return 元
	 */
	static public String fenToYuan(long src) {
		String f = "";
		if (src < 0) {
			f = "-";
			src = -src;
		}
		if (src < 10)
			return f + "0.0" + Long.toString(src);
		else if (src < 100)
			return f + "0." + Long.toString(src);
		else {
			long i = src / 100;
			long s = src % 100;
			if (s < 10)
				return f + Long.toString(i) + ".0" + Long.toString(s);
			else
				return f + Long.toString(i) + "." + Long.toString(s);
		}
	}

	/**
	 * 分转换为元
	 * 
	 * @param srcStr
	 *            分
	 * @return 元
	 */
	static public String fenToYuan(String srcStr) {
		int src = 0;
		srcStr = srcStr.trim();
		if (srcStr.length() > 0)
			src = Integer.parseInt(srcStr);
		String f = "";
		if (src < 0) {
			f = "-";
			src = -src;
		}
		if (src < 10)
			return f + "0.0" + Integer.toString(src);
		else if (src < 100)
			return f + "0." + Integer.toString(src);
		else {
			int i = src / 100;
			int s = src % 100;
			if (s < 10)
				return f + Integer.toString(i) + ".0" + Integer.toString(s);
			else
				return f + Integer.toString(i) + "." + Integer.toString(s);
		}
	}

	/**
	 * 格式化数字
	 * 
	 * @param s
	 *            要格式化的数字字串
	 * @param srcDots
	 *            原始小数点的位数
	 * @param destDots
	 *            转换后小数点的位数
	 * @return 格式化后的数字字串
	 */
	static public String formatNumber(String s, int srcDots, int destDots) {
		if (srcDots < 0)
			srcDots = 0;
		if (destDots < 0)
			destDots = 0;
		s = s.trim();
		String ne = s.startsWith("-") ? "-" : "";
		if (ne.length() > 0)
			s = s.substring(1);
		int index = s.indexOf(".");
		String is = s, fs = "";
		if (index > -1) {
			is = s.substring(0, index);
			fs = s.substring(index + 1);
		}
		int len = srcDots - fs.length();
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				fs += "0";
			}
		} else
			fs = fs.substring(0, srcDots);
		is += fs;
		len = destDots - is.length() + 1;
		for (int i = 0; i < len; i++)
			is = "0" + is;
		if (destDots > 0) {
			len = destDots - is.length() + 1;
			len = is.length() - destDots;
			is = is.substring(0, len) + "." + is.substring(len);
		}
		len = is.length();
		for (index = 0; index < len - 1; index++) {
			char ch = is.charAt(index);
			if (ch != '0' || (ch == '0' && is.charAt(index + 1) != '0'))
				break;
		}
		if (index > 0)
			is = is.substring(index);
		try {
			if (Double.parseDouble(is) == 0)
				ne = "";
		} catch (Throwable e) {
		}
		return ne + is;
	}

	/**
	 * 格式化人民币数据
	 * 
	 * @param s
	 *            原始数据
	 * @param srcIsFen
	 *            原始数据格式.true - 分;false - 元
	 * @return 转换后的字串,以元为单位
	 */
	static public String formatMoney(String s, boolean srcIsFen) {
		s = s.trim();
		if (s.length() == 0)
			return "0.00";
		String f = "";
		if (s.getBytes()[0] == '-') {
			f = "-";
			s = s.substring(1);
		}
		if (srcIsFen) {
			while (s.length() < 3)
				s = "0" + s;
			return f + s.substring(0, s.length() - 2) + "." + s.substring(s.length() - 2);
		} else {
			int pos = s.indexOf(".");
			if (pos < 0) {
				return f + s + ".00";
			}
			String s1 = s.substring(pos + 1, s.length());
			while (s1.length() < 2)
				s1 += "0";
			if (s1.length() > 2)
				s1 = s1.substring(0, 2);
			s = s.substring(0, pos);
			if (s.length() == 0)
				s = "0";
			return f + s + "." + s1;
		}
	}

	/**
	 * 格式当前时间
	 * 
	 * @param format
	 *            时间格式
	 * @return 时间字串
	 */
	public static String formatCurrentDateTime(String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(new Date());
	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 *            时间
	 * @param format
	 *            时间格式
	 * @return 时间字串
	 */
	public static String formatDateTime(long time, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(new Date(time));
	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 *            时间
	 * @param format
	 *            时间格式
	 * @return 时间字串
	 */
	public static String formatDateTime(Date time, String format) {
		if (time == null)
			return "";
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(time);
	}

	/**
	 * 提取时间
	 * 
	 * @param time
	 *            时间字串
	 * @param format
	 *            时间格式
	 * @return 时间字串
	 * @throws ParseException
	 */
	public static Date parseDateTime(String time, String format) throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.parse(time);
	}

	/**
	 * 提取时间
	 * 
	 * @param time
	 *            时间字串
	 * @return 时间字串
	 * 
	 */
	public static Date parseDateTime(String time) {
		SimpleDateFormat f;
		if (time.length() == 10) {
			f = new SimpleDateFormat("yyyy-MM-dd");
		} else if (time.length() == 8) {
			if (time.contains(":"))
				f = new SimpleDateFormat("HH:mm:ss");
			else
				f = new SimpleDateFormat("yyyyMMdd");
		} else if (time.length() == 14)
			f = new SimpleDateFormat("yyyyMMddHHmmss");
		else
			f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return f.parse(time);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 将日期字串格式化成Date对象，如果转换失败，则返回def
	 * 
	 * @param time
	 *            时间字串
	 * @param format
	 *            格式化字串
	 * @param def
	 *            默认返回
	 * @return 格式化的Date对象
	 */
	public static Date parseDateTimeDef(String time, String format, Date def) {
		try {
			SimpleDateFormat f = new SimpleDateFormat(format);
			return f.parse(time);
		} catch (Throwable e) {
			return def;
		}
	}

	/**
	 * 剪切右边的不可见字符
	 * 
	 * @param str
	 *            被剪切的字符串
	 * @return 剪切后的字符串
	 */
	public static String trimRight(String str) {
		int len = str.length();
		for (int i = len - 1; i >= 0; i--) {
			if (str.charAt(i) > ' ')
				return str.substring(0, i + 1);
		}
		return "";
	}

	/**
	 * 剪切左边的不可见字符
	 * 
	 * @param str
	 *            被剪切的字符串
	 * @return 剪切后的字符串
	 */
	public static String trimLeft(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (str.charAt(i) > ' ')
				return str.substring(i);
		}
		return "";
	}

	/**
	 * 检查一个字符是否是可见字符
	 * 
	 * @param c
	 *            要检查的字符
	 * @return true - 是可见字符<br>
	 *         false - 非可见字符
	 */
	static public boolean isVisibleChar(byte c) {
		switch (c) {
		case '.':
		case ',':
		case '>':
		case '<':
		case '?':
		case '/':
		case '\'':
		case ';':
		case ':':
		case '\"':
		case '\\':
		case '|':
		case ']':
		case '[':
		case '}':
		case '{':
		case '-':
		case '_':
		case '+':
		case '=':
		case ')':
		case '(':
		case '*':
		case '&':
		case '^':
		case '%':
		case '$':
		case '#':
		case '@':
		case '!':
		case '`':
		case '~':
		case ' ':
			return true;
		default:
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
				return true;
			else if (c < 0)
				return true;
			else
				return false;
		}
	}

	/**
	 * 将一段字节数据，输出成带行标记的十六制字串，便于分析字节数据
	 * 
	 * @param data
	 *            要格式化的字节数组
	 * @return 处理后的十六进制字串
	 */
	public static String[] formatHex(byte[] data) {
		if (data == null || data.length == 0)
			return new String[0];
		int len = data.length;
		int rows = len / 16 + (len % 16 > 0 ? 1 : 0);
		String[] ret = new String[rows];
		for (int i = 0; i < rows; i++) {
			String str = "";
			byte bb[] = null;
			if (len >= (i + 1) * 16)
				bb = new byte[16];
			else
				bb = new byte[len - i * 16];
			for (int j = 0; j < bb.length; j++) {
				byte b = data[i * 16 + j];
				str += byteToHex(b) + " ";
				if (j == i * 16 + 8)
					str += " ";
				if (!isVisibleChar(b))
					bb[j] = '.';
				else
					bb[j] = b;
			}
			String a = Integer.toHexString(i * 16);
			while (a.length() < 8)
				a = "0" + a;
			while (str.length() < 49)
				str += " ";
			ret[i] = a + "h: " + str + "; " + new String(bb);
		}
		return ret;
	}

	/**
	 * 字节转换为16进制字符串
	 * 
	 * @param ch
	 *            要转换的字节
	 * @return 16进制字符串
	 */
	static public String intToHex(int number) {
		return Integer.toHexString(number);
	}

	/**
	 * 字节转换为16进制字符串
	 * 
	 * @param ch
	 *            要转换的字节
	 * @return 16进制字符串
	 */
	static public String byteToHex(byte ch) {
		String str[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		return str[ch >> 4 & 0xF] + str[ch & 0xF];
	}

	/**
	 * 二进制字节数组转换为16进制字符串
	 * 
	 * @param ch
	 *            二进制字节数组
	 * @return 16进制字串
	 */
	static public String bytesToHex(byte[] ch) {
		String ret = "";
		for (int i = 0; i < ch.length; i++)
			ret += byteToHex(ch[i]);
		return ret;
	}

	/**
	 * 二进制字节数组转换为16进制字符串
	 * 
	 * @param ch
	 *            二进制字节数组
	 * @return 16进制字串
	 */
	static public String bytesToHex(byte[] ch, int offset, int length) {
		String ret = "";
		for (int i = 0; i < length; i++)
			ret += byteToHex(ch[i + offset]);
		return ret;
	}

	/**
	 * 16进制字串转换为字节
	 * 
	 * @param str
	 *            16进制字串
	 * @return 转换后的字节
	 */
	static public byte hexToByte(String str) {
		if (str.length() < 2)
			return 0;
		str = str.toUpperCase();
		byte[] bs = str.getBytes();
		byte l = 0, h = 0;
		l = bs[1];
		h = bs[0];
		if (l >= '0' && l <= '9')
			l -= '0';
		else if (l >= 'A' && l <= 'F') {
			l -= 'A';
			l += 10;
		} else
			l = 0;
		if (h >= '0' && h <= '9')
			h -= '0';
		else if (h >= 'A' && h <= 'F') {
			h -= 'A';
			h += 10;
		} else
			h = 0;
		h = (byte) (h << 4);
		return (byte) (h | l);
	}

	/**
	 * 16进制字节符串转换为二进制字节数组
	 * 
	 * @param str
	 *            16进制字串
	 * @return 二进制字节数组
	 */
	static public byte[] hexToBytes(String str) {
		int len = str.length() / 2;
		str = str.toUpperCase();
		byte[] bs = str.getBytes();
		byte[] ret = new byte[len];
		for (int i = 0; i < len * 2; i += 2) {
			byte l = 0, h = 0;
			l = bs[i + 1];
			h = bs[i];
			if (l >= '0' && l <= '9')
				l -= '0';
			else if (l >= 'A' && l <= 'F') {
				l -= 'A';
				l += 10;
			} else
				l = 0;
			if (h >= '0' && h <= '9')
				h -= '0';
			else if (h >= 'A' && h <= 'F') {
				h -= 'A';
				h += 10;
			} else
				h = 0;
			h = (byte) (h << 4);
			ret[i / 2] = (byte) (h | l);
		}
		return ret;
	}

	/**
	 * bcd码转换为10进制字符串
	 * 
	 * @param bcd
	 *            bcd字节数组
	 * @param len
	 *            要转换的长度
	 * @return 转换后的字符串
	 */
	static public String bcdToDec(byte[] bcd, int offset, int len) {
		if (len <= 0 || len > bcd.length)
			len = bcd.length;
		String ret = "";
		for (int i = 0; i < len; i++) {
			String str = byteToHex(bcd[offset + i]);
			ret += str;
		}
		return ret;
	}

	/**
	 * 10进制字串转换为bcd码
	 * 
	 * @param s
	 *            10进制字串
	 * @param len
	 *            转换长度
	 * @return 转换后的字节数组
	 * @throws IOException
	 *             转换失败后抛出
	 */
	static public byte[] decToBcd(String s, int len) throws IOException {
		if ((s.length() % 2) != 0)
			s = "0" + s;
		byte[] bs = s.getBytes();
		if (len <= 0)
			len = bs.length;
		if (len > bs.length)
			len = bs.length;
		len = len / 2;
		byte[] ret = new byte[len];
		for (int i = 0; i < len; i++) {
			byte c = (byte) (bs[i * 2] - '0');
			byte c1 = (byte) (bs[i * 2 + 1] - '0');
			if (c < 0 || c > 9 || c1 > 9 || c1 < 0)
				throw new IOException("不是有效的数字!");
			ret[i] = (byte) (c << 4 | c1);
		}
		return ret;
	}

	/**
	 * 将带下划线的字串，转换为变量字串，如：user_name ==> userName
	 * 
	 * @param name
	 *            要转换的带下划线的字串
	 * @return 转换后字串
	 */
	static public String toVarName(String name) {
		StringBuffer sb = new StringBuffer();
		String upper = name.toUpperCase();
		name = name.toLowerCase();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '_') {
				c = upper.charAt(++i);
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 将带下划线的字串，转换为类字串，如：user_name ==> UserName
	 * 
	 * @param name
	 *            要转换的带下划线的字串
	 * @return 转换后的类字串
	 */
	static public String toClassName(String name) {
		return firstWordCap(toVarName(name));
	}

	/**
	 * 从文件中装入字串
	 * 
	 * @param file
	 *            文件路径
	 * @return 字串内容
	 * @throws IOException
	 *             如果文件不存在或发生读取异常
	 */
	static public String loadFromFile(String file) throws IOException {
		StringBuffer sb = new StringBuffer();
		FileReader reader = new FileReader(file);
		try {
			int c = reader.read();
			while (c > 0) {
				sb.append((char) c);
				c = reader.read();
			}
		} finally {
			reader.close();
		}
		return sb.toString();
	}

	static public String toString(Object v) {
		if (v == null)
			return "";
		else
			return v.toString();
	}

	static public List<String> splitToStringList(String s, String p) {
		List<String> ls = new ArrayList<String>();
		split(s, p, ls);
		return ls;
	}
}
