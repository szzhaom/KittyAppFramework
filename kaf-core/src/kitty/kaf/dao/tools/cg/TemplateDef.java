package kitty.kaf.dao.tools.cg;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	HashMap<String, String> editFieldTemplates = new HashMap<String, String>();
	HashMap<String, String> actions = new HashMap<String, String>();

	public TemplateDef(Element el) {
		name = el.getAttribute("name");
		type = el.getAttribute("type");
		location = el.getAttribute("location");
		NodeList ls = el.getElementsByTagName("editfield");
		for (int i = 0; i < ls.getLength(); i++) {
			Element e = (Element) ls.item(i);
			editFieldTemplates.put(e.getAttribute("name"), e.getTextContent());
		}
		ls = el.getElementsByTagName("action");
		for (int i = 0; i < ls.getLength(); i++) {
			Element e = (Element) ls.item(i);
			actions.put(e.getAttribute("name"), e.getTextContent());
		}
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

	public String getEditField(String name) {
		return editFieldTemplates.get(name);
	}

	public String getAction(String name) {
		return actions.get(name);
	}

}
