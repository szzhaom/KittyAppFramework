package kitty.kaf.logging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.KafUtil;
import kitty.kaf.helper.StringHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class RequestParamDef {
	String name;
	String type;

	public String getLogValue(String v) {
		if (type.equals("password"))
			return "******";
		else
			return v;
	}

	RequestParamDef(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
}

class RequestParamDefList {
	ConcurrentHashMap<String, RequestParamDef> params = new ConcurrentHashMap<String, RequestParamDef>();

	public void addParams(String name, String type) {
		String[] ls = StringHelper.splitToStringArray(name, "|");
		for (String n : ls) {
			if (!n.trim().isEmpty())
				params.put(n.trim(), new RequestParamDef(n.trim(), type));
		}
	}
}

class WebContextDef {
	ConcurrentHashMap<String, RequestParamDefList> requests = new ConcurrentHashMap<String, RequestParamDefList>();

	public WebContextDef(ServletContext context, String configFile) {
		try {
			String path = KafUtil.getConfigPath();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(path + configFile);
			NodeList ls = doc.getElementsByTagName("requestlogsource");
			for (int i = 0; i < ls.getLength(); i++) {
				Element el2 = (Element) ls.item(i);
				NodeList ls2 = el2.getElementsByTagName("request");
				for (int k = 0; k < ls2.getLength(); k++) {
					Element el = (Element) ls2.item(k);
					String uri = el.getAttribute("uri");
					RequestParamDefList defs = requests.get(uri);
					if (defs == null) {
						defs = new RequestParamDefList();
						requests.put(uri, defs);
					}
					NodeList ls1 = el.getElementsByTagName("param");
					for (int j = 0; j < ls1.getLength(); j++) {
						Element el1 = (Element) ls1.item(j);
						defs.addParams(el1.getAttribute("name"), el1.getAttribute("type"));
					}
				}
			}
		} catch (Throwable e) {
			RequestLoggerDataSource.logger.error("load request data source failure:", e);
		}
	}
}

public class RequestLoggerDataSource implements LoggerDataSource {
	private HttpServletRequest request;
	private WebContextDef def;
	static final Logger logger = Logger.getLogger(RequestLoggerDataSource.class);
	static ConcurrentHashMap<ServletContext, WebContextDef> contexts = new ConcurrentHashMap<ServletContext, WebContextDef>();

	static synchronized public void addContext(ServletContext context, String configFile) {
		WebContextDef def = contexts.get(context);
		if (def == null) {
			def = new WebContextDef(context, configFile);
			contexts.put(context, def);
		}
	}

	static synchronized public void removeContext(ServletContext context) {
		contexts.remove(context);
	}

	public RequestLoggerDataSource(HttpServletRequest request) {
		super();
		this.request = request;
		def = contexts.get(request.getSession().getServletContext());
	}

	@Override
	public Object getLogData() {
		if (def == null) {
			return request.getRequestURI() + "?" + request.getQueryString();
		} else {
			String url = request.getRequestURI();
			RequestParamDefList allList = def.requests.get("/*");
			Enumeration<?> em = request.getParameterNames();
			ArrayList<String> ls = new ArrayList<String>();
			ls.add("request: {");
			ls.add("\turl:" + url);
			while (em.hasMoreElements()) {
				String name = (String) em.nextElement();
				RequestParamDefList list = def.requests.get(url);
				if (list == null)
					list = allList;
				RequestParamDef def = null;
				String v = request.getParameter(name);
				if (list != null)
					def = list.params.get(name);
				if (def != null)
					v = def.getLogValue(v);
				ls.add("\t" + name + ":" + v);
			}
			ls.add("}");
			return ls;
		}
	}

}
