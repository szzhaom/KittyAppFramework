package kitty.kaf.dao.tools;

import org.w3c.dom.Element;

/**
 * 表的枚举数据项配置
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class EnumItemDef {
	public String name, desp, value;

	public EnumItemDef(Element el) {
		name = el.getAttribute("name");
		desp = el.getAttribute("desp");
		value = el.getAttribute("value");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
