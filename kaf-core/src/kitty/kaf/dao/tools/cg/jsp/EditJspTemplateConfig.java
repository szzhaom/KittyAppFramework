package kitty.kaf.dao.tools.cg.jsp;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EditJspTemplateConfig extends JspTemplateConfig {
	String path;
	List<JspEditField> editFields = new ArrayList<JspEditField>();

	public EditJspTemplateConfig(TableJspConfig config, Element el) {
		super(config, el);
		path = el.getAttribute("path");
		NodeList ls = el.getElementsByTagName("edit_field");
		for (int i = 0; i < ls.getLength(); i++) {
			editFields.add(new JspEditField(config, (Element) ls.item(i)));
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
