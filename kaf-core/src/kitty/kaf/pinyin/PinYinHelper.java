package kitty.kaf.pinyin;

import kitty.kaf.exceptions.CoreException;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音处理
 * 
 * @author 赵明
 * @since 5.0
 * 
 */
public class PinYinHelper {
	public static void main(String[] args) {
		System.out.println(getHanyuPinyinFirstChar("中国联通"));
	}

	/**
	 * 获取汉语拼音字母
	 */
	public static String getHanyuPinyin(String strCN) {
		if (null == strCN) {
			return null;
		}
		StringBuffer spell = new StringBuffer();
		try {
			char[] charOfCN = strCN.toCharArray();
			HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
			defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			for (int i = 0; i < charOfCN.length; i++) {
				// 是否为中文字符
				if (charOfCN[i] > 128) {
					String[] spellArray = PinyinHelper
							.toHanyuPinyinStringArray(charOfCN[i],
									defaultFormat);
					if (null != spellArray) {
						spell.append(spellArray[0]);
					} else {
						spell.append(charOfCN[i]);
					}
				} else {
					spell.append(charOfCN[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			throw new CoreException(e);
		}
		return spell.toString();
	}

	/**
	 * 获取汉语拼音首字母
	 */
	public static String getHanyuPinyinFirstChar(String strCN) {
		if (null == strCN) {
			return null;
		}

		StringBuffer spell = new StringBuffer();
		try {
			char[] charOfCN = strCN.toCharArray();
			HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
			defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			for (int i = 0; i < charOfCN.length; i++) {
				// 是否为中文字符
				if (charOfCN[i] > 128) {
					String[] spellArray = PinyinHelper
							.toHanyuPinyinStringArray(charOfCN[i],
									defaultFormat);
					if (null != spellArray) {
						spell.append(spellArray[0].charAt(0));
					} else {
						spell.append(charOfCN[i]);
					}
				} else {
					spell.append(charOfCN[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			throw new CoreException(e);
		}
		return spell.toString();
	}
}
