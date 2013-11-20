package kitty.testapp.inf.web;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kitty.kaf.helper.StringHelper;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.webframe.ControlServletExecutor;
import kitty.kaf.webframe.ControlServletExecutorConfig;
import kitty.kaf.webframe.ControlServletExecutorConfig.NavigateRule;

public class WebControlServletExecutor extends ControlServletExecutor {

	static KafLogger logger = KafLogger
			.getLogger(WebControlServletExecutor.class);
	static ControlServletExecutorConfig config;

	public static void unload() {
		config.clear();
	}

	public static void load(ServletContext servletContext)
			throws MalformedURLException {
		String path = servletContext.getResource("/").toString();
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		config = new ControlServletExecutorConfig(path, "web-faces.xml");
	}

	public WebControlServletExecutor() {

	}

	@Override
	public boolean handleRequest(NavigateRule rule, String url,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		boolean needLogin = rule != null && rule.needLogin;
		if (needLogin) {
			WebSession session = (WebSession) WebSession
					.getCurrentSession(request);
			if (!session.isLogined()) {
				request.setAttribute("redirecturl", url);
				forward(request,
						response,
						"/login.jsp?rurl="
								+ StringHelper.bytesToHex(url.getBytes()), true);
				return true;
			} else if (url.equals("/logout.go")) {
				try {
					session.loginOut();
				} catch (Throwable e) {
				}
				forward(request, response, "/login.jsp", true);
				return true;
			}
		}
		return false;
	}

	@Override
	public ControlServletExecutorConfig getConfig() {
		return config;
	}

}
