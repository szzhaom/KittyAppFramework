package kitty.testapp.inf.web;

import java.net.MalformedURLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import kitty.kaf.logging.RequestDataSource;
import kitty.kaf.trade.web.WebServlet;

public class WebContextListener implements ServletContextListener {
	public WebContextListener() {
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			// BasicTagSupport.addRightTagProcessor(arg0.getServletContext(),
			// new WebRightProcessor());
			WebControlServletExecutor.load(arg0.getServletContext());
			WebServlet.init(arg0.getServletContext(), "web-config.xml");
			RequestDataSource.addContext(arg0.getServletContext(),
					"web-config.xml");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		// BasicTagSupport.removeRightTagProcessor(arg0.getServletContext());
		WebControlServletExecutor.unload();
		WebServlet.uninit(arg0.getServletContext());
		RequestDataSource.removeContext(arg0.getServletContext());
	}

}
