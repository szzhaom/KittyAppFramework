package kitty.kaf.trade.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.KafUtil;
import kitty.kaf.logging.Logger;
import kitty.kaf.trade.pack.HttpRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 执行Web交易指令的Servlet
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class WebServlet extends HttpServlet {
	private static final long serialVersionUID = 6132775917766784894L;
	static Logger logger = Logger.getLogger(WebServlet.class);
	static ConcurrentHashMap<String, Class<?>> executors = new ConcurrentHashMap<String, Class<?>>();
	static ConcurrentHashMap<ServletContext, ArrayList<String>> webContextExecutors = new ConcurrentHashMap<ServletContext, ArrayList<String>>();

	public WebServlet() {
		super();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	public static void init(ServletContext context, String configFile) {
		try {
			// 装入交易配置
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String path = KafUtil.getConfigPath();
			logger.debug("[configpath:" + path + "]");
			Document doc = builder.parse(path + configFile);

			NodeList list = doc.getElementsByTagName("webexecutor");
			ArrayList<String> ls = new ArrayList<String>();
			webContextExecutors.put(context, ls);
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				Class<?> clazz = Class.forName(node.getAttribute("class"));
				ls.add(node.getAttribute("name"));
				executors.put(node.getAttribute("name"), clazz);
				String file = node.getAttribute("configfile");
				Document document = null;
				if (file != null && !file.trim().isEmpty()) {
					document = builder.parse(path + file);
				}
				Object o = clazz.newInstance();
				if (o instanceof WebExecutor)
					((WebExecutor) o).init(document);
			}
		} catch (Throwable e) {
			logger.error("init error:", e);
		}
	}

	/**
	 * 移除所有执行器
	 */
	public static void uninit(ServletContext context) {
		ArrayList<String> ls = webContextExecutors.get(context);
		if (ls != null) {
			for (String n : ls) {
				try {
					Object o = executors.remove(n).newInstance();
					if (o instanceof WebExecutor)
						((WebExecutor) o).uninit();
				} catch (Throwable e) {
				}
			}
		}
	}

	/**
	 * 移除一个交易执行器
	 * 
	 * @param name
	 *            交易执行器名称
	 */
	public static void removeExecutor(String name) {
		executors.remove(name);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			logger.debug("xmlservlet: " + request.getRequestURI() + "?"
					+ request.getQueryString());
			String executorName = request.getParameter("executor");
			if (executorName == null)
				throw new IOException("缺少参数【executor】");
			Object executor = executors.get(executorName).newInstance();
			HttpRequest req = new HttpRequest(request, "utf-8", false);
			((WebExecutor) executor).execute(req, response);
		} catch (Throwable e) {
			if (logger.isDebugEnabled())
				logger.error("error:", e);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
