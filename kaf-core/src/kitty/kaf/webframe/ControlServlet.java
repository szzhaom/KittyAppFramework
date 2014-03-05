package kitty.kaf.webframe;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kitty.kaf.logging.Logger;

public class ControlServlet extends HttpServlet {

	static Logger logger = Logger.getLogger(ControlServlet.class);
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<ServletContext, Class<ControlServletExecutor>> map = new ConcurrentHashMap<ServletContext, Class<ControlServletExecutor>>();

	public ControlServlet() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			map.put(getServletContext(), (Class<ControlServletExecutor>) Class
					.forName(config.getInitParameter("executor-class")));
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest hrequest,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			ControlServletExecutor executor = map.get(getServletContext())
					.newInstance();
			executor.setServlet(this);
			executor.post(hrequest, response);
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}
}
