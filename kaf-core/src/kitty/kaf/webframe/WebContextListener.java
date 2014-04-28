package kitty.kaf.webframe;

import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import kitty.kaf.KafUtil;

public class WebContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Enumeration<String> ks = KafUtil.getAttributes().keys();
		while (ks.hasMoreElements()) {
			String k = ks.nextElement();
			String v = KafUtil.getAttributes().get(k);
			arg0.getServletContext().setAttribute(k, v);
		}
	}

}
