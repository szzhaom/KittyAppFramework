package kitty.kaf.dao.tools;

import kitty.kaf.dao.source.DaoSource;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 表空间配置
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class Tablespace {
	String sql;

	/**
	 * 从XML Element中读取一个表空间配置
	 * 
	 * @param el
	 *            表空间配置element
	 * @param dbType
	 *            数据库类型
	 */
	public Tablespace(Element el, DaoSource daoSource) {
		NodeList ls = el.getElementsByTagName("sql");
		for (int i = 0; i < ls.getLength(); i++) {
			Element el1 = (Element) ls.item(i);
			if (daoSource.getType().equals(el1.getAttribute("type"))) {
				sql = el1.getAttribute("value");
				break;
			}
		}
	}
}
