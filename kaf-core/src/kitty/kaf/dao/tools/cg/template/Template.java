package kitty.kaf.dao.tools.cg.template;

import kitty.kaf.dao.tools.cg.CodeGenerator;

import org.w3c.dom.Element;

public class Template {
	String name;
	String content;
	CodeGenerator generator;

	public Template(CodeGenerator generator, Element el) {
		this.generator = generator;
		this.name = el.getAttribute("name");
		this.content = el.getTextContent();
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

	public CodeGenerator getGenerator() {
		return generator;
	}
}
