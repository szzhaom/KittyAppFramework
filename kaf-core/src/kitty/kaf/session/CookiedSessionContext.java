package kitty.kaf.session;

import java.io.Serializable;

import javax.servlet.ServletContext;

import kitty.kaf.pools.memcached.MemcachedClient;

public class CookiedSessionContext implements Serializable {
	private static final long serialVersionUID = 1L;
	ServletContext servletContext;
	String sessionId, dataId;
	MemcachedClient mc;
	Class<RequestSession<?>> sessionClazz;

	public ServletContext getServletContext() {
		return servletContext;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getDataId() {
		return dataId;
	}

	public MemcachedClient getMemcachedClient() {
		return mc;
	}

	public Class<RequestSession<?>> getSessionClazz() {
		return sessionClazz;
	}
}
