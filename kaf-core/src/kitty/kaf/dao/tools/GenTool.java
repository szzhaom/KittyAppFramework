package kitty.kaf.dao.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.GafUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class GenTool {

	public static void main(String[] args) {
		String file = "gen-config.xml";
		if (args.length > 1)
			file = args[1];
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					GafUtil.getHome() + "/code_generator/" + file), "utf-8");
			BufferedReader reader = new BufferedReader(in);
			InputSource input = new InputSource(reader);
			Document doc = builder.parse(input);
			NodeList list = doc.getElementsByTagName("config");
			Element root = (Element) list.item(0);
			NodeList ls = root.getElementsByTagName("module");
			for (int i = 0; i < ls.getLength(); i++) {
				Element el = (Element) ls.item(i);
				new Database(GafUtil.getHome() + "/code_generator/"
						+ el.getAttribute("file"), "default");
			}
		} catch (Throwable e) {
		}
	}

}
