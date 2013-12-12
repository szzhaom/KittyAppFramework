package kitty.kaf.dao.tools.cg.template;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.dao.tools.cg.CodeGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TemplateConfig {
	CodeGenerator generator;
	HashMap<String, Template> queryOptionActionTemplates = new HashMap<String, Template>();
	HashMap<String, Template> editFieldTemplates = new HashMap<String, Template>();
	HashMap<String, JspTemplate> jspFileTemplates = new HashMap<String, JspTemplate>();

	public TemplateConfig(CodeGenerator generator, String configFile) {
		super();
		this.generator = generator;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStreamReader in = new InputStreamReader(new FileInputStream(configFile), "utf-8");
			BufferedReader reader = new BufferedReader(in);
			InputSource input = new InputSource(reader);
			Document doc = builder.parse(input);
			NodeList list = doc.getElementsByTagName("config");
			Element root = (Element) list.item(0);
			NodeList ls = root.getElementsByTagName("jsp-templates");
			for (int i = 0; i < ls.getLength(); i++) {
				Element e = (Element) ls.item(i);
				NodeList ls1 = e.getElementsByTagName("query-option-action-templates");
				for (int j = 0; j < ls1.getLength(); j++) {
					Element el = (Element) ls1.item(j);
					NodeList ls2 = el.getElementsByTagName("template");
					for (int k = 0; k < ls2.getLength(); k++) {
						Template t = new Template(generator, (Element) ls2.item(k));
						queryOptionActionTemplates.put(t.name, t);
					}
				}
				ls1 = e.getElementsByTagName("edit-field-templates");
				for (int j = 0; j < ls1.getLength(); j++) {
					Element el = (Element) ls1.item(j);
					NodeList ls2 = el.getElementsByTagName("template");
					for (int k = 0; k < ls2.getLength(); k++) {
						Template t = new Template(generator, (Element) ls2.item(k));
						editFieldTemplates.put(t.name, t);
					}
				}
				ls1 = e.getElementsByTagName("jsp-file-templates");
				for (int j = 0; j < ls1.getLength(); j++) {
					Element el = (Element) ls1.item(j);
					NodeList ls2 = el.getElementsByTagName("template");
					for (int k = 0; k < ls2.getLength(); k++) {
						JspTemplate t = new JspTemplate(generator, (Element) ls2.item(k));
						jspFileTemplates.put(t.name, t);
					}
				}
			}
		} catch (Throwable e) {
		}
	}

	public CodeGenerator getGenerator() {
		return generator;
	}

	public HashMap<String, Template> getQueryOptionActionTemplates() {
		return queryOptionActionTemplates;
	}

	public HashMap<String, Template> getEditFieldTemplates() {
		return editFieldTemplates;
	}

	public HashMap<String, JspTemplate> getJspFileTemplates() {
		return jspFileTemplates;
	}
}
