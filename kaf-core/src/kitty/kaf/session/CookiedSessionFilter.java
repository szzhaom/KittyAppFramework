package kitty.kaf.session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kitty.kaf.logging.Logger;
import kitty.kaf.pools.memcached.MemcachedClient;

public class CookiedSessionFilter implements Filter {
	static Logger logger = Logger
			.getLogger(CookiedSessionFilter.class);
	static ConcurrentHashMap<String, CookiedSessionContext> contextMap = new ConcurrentHashMap<String, CookiedSessionContext>();

	static public CookiedSessionContext filter(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		Cookie cookies[] = request.getCookies();
		Cookie sCookie = null;
		CookiedSessionContext context = contextMap
				.get(request.getContextPath());
		String sid = "";
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				sCookie = cookies[i];
				if (sCookie.getName().equals(context.sessionId)) {
					sid = sCookie.getValue();
					break;
				}
			}
		}
		if (sid == null || sid.length() == 0) {
			sid = java.util.UUID.randomUUID().toString();
			Cookie mycookies = new Cookie(context.sessionId, sid);
			mycookies.setMaxAge(Integer.MAX_VALUE);
			mycookies.setPath(request.getContextPath());
			if (mycookies.getPath() == null || mycookies.getPath().isEmpty())
				mycookies.setPath("/");
			else if (!mycookies.getPath().startsWith("/"))
				mycookies.setPath("/" + mycookies.getPath());
			response.addCookie(mycookies);
		}
		try {
			RequestSession<?> session = context.sessionClazz.newInstance();
			session.setId(sid);
			session.setRequest(request);
			session.setResponse(response);
			session.setContext(context);
			session.load();
			request.setAttribute("mysession", session);
		} catch (Throwable e) {
			throw new IOException(e);
		}
		return context;
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		CookiedSessionContext context = filter(request, response);
		filterChain.doFilter(new CookiedHttpServletRequestWrapper(context,
				request), servletResponse);
	}

	@SuppressWarnings("unchecked")
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext context = filterConfig.getServletContext();
		CookiedSessionContext c = new CookiedSessionContext();
		contextMap.put(context.getContextPath(), c);
		String p = context.getContextPath();
		if (p.startsWith("/"))
			p = p.substring(1);
		c.servletContext = context;
		c.sessionId = p.toUpperCase() + "SID";
		c.dataId = "_" + p.toUpperCase() + "_D";
		c.mc = MemcachedClient.newInstance(null,
				filterConfig.getInitParameter("memcached-configname"));
		try {
			c.sessionClazz = (Class<RequestSession<?>>) Class
					.forName(filterConfig.getInitParameter("session-class"));
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		}
	}

	static public CookiedSessionContext getConfigedContext(String contextPath) {
		return contextMap.get(contextPath);
	}

	@Override
	public void destroy() {
	}

}
