package kitty.kaf.dao.tools.cg;

import org.w3c.dom.Element;

/**
 * 模板定义
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public class TemplateDef {
	String name;
	String type;
	String location;
	String template;

	public TemplateDef(Element el) {
		name = el.getAttribute("name");
		type = el.getAttribute("type");
		location = el.getAttribute("location");
		template = el.getTextContent();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
