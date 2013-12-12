package kitty.kaf.dao.tools.cg.template;

import kitty.kaf.dao.tools.cg.CodeGenerator;

import org.w3c.dom.Element;

public class JspTemplate extends Template {
	String location;

	public JspTemplate(CodeGenerator generator, Element el) {
		super(generator, el);
		this.location = el.getAttribute("location");
	}

	public String getLocation() {
		return location;
	}

}
