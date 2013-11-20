package kitty.kaf.helper;

/**
 * 基于Object处理的助手程序
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class ObjectHelper {

	/**
	 * 检查src是不是null，如果是null，则返回def，否则返回src
	 * 
	 * @param src
	 *            被检查的对象
	 * @param def
	 *            默认对象
	 * @return 处理后的返回对象
	 */
	public static Object checkNull(Object src, Object def) {
		if (src == null)
			return def;
		else
			return src;
	}
}
