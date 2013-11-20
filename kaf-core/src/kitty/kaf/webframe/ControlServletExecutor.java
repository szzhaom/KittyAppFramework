package kitty.kaf.webframe;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kitty.kaf.helper.StringHelper;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.logging.RequestDataSource;
import kitty.kaf.trade.pack.HttpRequest;
import kitty.kaf.webframe.ControlServletExecutorConfig.NavigateRule;
import kitty.kaf.webframe.ControlServletExecutorConfig.ToRule;

public abstract class ControlServletExecutor {
	static KafLogger logger = KafLogger
			.getLogger(ControlServletExecutor.class);
	private HttpServlet servlet;

	public HttpServlet getServlet() {
		return servlet;
	}

	void setServlet(HttpServlet servlet) {
		this.servlet = servlet;
	}

	public abstract ControlServletExecutorConfig getConfig();

	public void forward(HttpServletRequest request,
			HttpServletResponse response, String url, boolean redirect)
			throws IOException, ServletException {
		if (redirect) {
			if (url.startsWith("/")) {
				url = request.getContextPath() + url;
			}
			if (url.endsWith(".jsp")) {
				url = url.substring(0, url.length() - 4) + ".go";
			} else if (url.indexOf(".jsp?") > 0) {
				url = url.replace(".jsp", ".go");
			}
			response.sendRedirect(url);
		} else {
			RequestDispatcher rd = servlet.getServletContext()
					.getRequestDispatcher(url);
			if (rd == null) {
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"创建转发对象失败");
				return;
			}
			rd.forward(request, response);
		}
	}

	public void post(HttpServletRequest hrequest, HttpServletResponse response)
			throws ServletException, IOException {
		HttpRequest request = new HttpRequest(hrequest, "utf-8", false);
		request.setAttribute("fromRSfaces", true);
		try {
			String ctxPath = hrequest.getContextPath();
			String url = hrequest.getRequestURI();
			if (ctxPath != null) {
				if (url.startsWith(ctxPath)) {
					url = url.substring(ctxPath.length());
				}
			}
			int index = url.lastIndexOf(".go");
			if (index > -1) {
				url = url.substring(0, index) + ".jsp";
			}
			NavigateRule rule = null;
			String fromView = request.getParameterDef("RSfacesfromid", null);
			if (logger.isDebugEnabled()) {
				logger.debug(new RequestDataSource(hrequest));
			}
			boolean isFormSubmit = false;
			if (fromView != null && !fromView.isEmpty()) {// fromview
				url = new String(StringHelper.hexToBytes(fromView));
				if (ctxPath != null) {
					if (url.startsWith(ctxPath)) {
						url = url.substring(ctxPath.length());
					}
				}
				isFormSubmit = true;
			}
			rule = getConfig().rules.get(url);
			if (handleRequest(rule, url, hrequest, response)) {
				return;
			}
			if (rule != null && rule.saveQueryStringToAttribute) {
				Enumeration<?> en = request.getParameterNames();
				while (en.hasMoreElements()) {
					String n = (String) en.nextElement();
					String p = request.getParameter(n);
					request.setAttribute(n, p);
				}
			}
			if (rule != null) {
				if ((isFormSubmit || rule.createActionAlways)
						&& rule.action != null && rule.actionClass != null) {
					FacesAction action = (FacesAction) Class.forName(
							rule.actionClass).newInstance();
					request.setAttribute(rule.action, action);
					request.setAttribute("facessubmited", isFormSubmit);
					ActionForward forward;
					try {
						action.setFormSubmit(isFormSubmit);
						action.setRequest(request);
						action.setResponse(response);
						// action.setSession(session);
						forward = action.execute();
					} catch (FacesError e) {
						if (logger.isDebugEnabled())
							logger.error("faces error:", e);
						forward = new ActionForward(ActionForwardType.ACTION,
								e.forward);
						request.setAttribute("faceserror", e);
					} catch (Throwable e) { // 默认指向error
						if (logger.isDebugEnabled())
							logger.error("error:", e);
						forward = new ActionForward(ActionForwardType.ACTION,
								"error");
						request.setAttribute("faceserror", new FacesError(null,
								e.getMessage()));
					}
					if (forward != null) {
						if (forward.getForwardType() == ActionForwardType.REDIRECT_URL) {
							forward(hrequest, response,
									forward.getForwardValue(), true);
							return;
						}
						ToRule tourl = rule.toRules.get(forward
								.getForwardValue());
						if (tourl == null) {
							String u = url;
							while (!u.isEmpty()
									&& (index = u.lastIndexOf("/")) >= 0) {
								u = u.substring(0, index);
								rule = getConfig().rules.get(u + "/*");
								if (rule != null) {
									tourl = rule.toRules.get(forward
											.getForwardValue());
									if (tourl != null) {
										break;
									}
								}
							}
						}
						if (tourl == null) {
							throw new Exception("找不到结果为["
									+ forward.getForwardValue() + "]的目标页面！");
						}
						forward(hrequest, response, tourl.url, tourl.redirect);
						return;
					}
				}
				url = rule.from;
			}
			forward(hrequest, response, url, false);
		} catch (Throwable e) { // 如果出错，则直接定位到错误页面
			logger.error(hrequest.getRequestURI() + "处理失败:", e);
			if (!getConfig().urlFileExists("error.jsp")) {
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求处理失败："
								+ e.getMessage());
			} else {
				request.setAttribute("faceserror",
						new FacesError(null, e.getMessage()));
				hrequest.getRequestDispatcher("/error.jsp").forward(hrequest,
						response);
			}
		}
	}

	/**
	 * 接管请求，对某些请求实现独有的请求处理流程
	 * 
	 * @param servlet
	 *            当前的servlet对象
	 * @param request
	 *            http请求
	 * @param response
	 *            http响应
	 * @return true - 表示当前请求已经被接管，默认处理将来被取消<br>
	 *         false - 表示需要按默认方式处理
	 */
	public boolean handleRequest(NavigateRule rule, String url,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		return false;
	}

}
