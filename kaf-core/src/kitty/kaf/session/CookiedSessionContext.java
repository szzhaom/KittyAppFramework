package kitty.kaf.session;

import java.io.Serializable;

import javax.servlet.ServletContext;

import kitty.kaf.cache.CacheClient;

public class CookiedSessionContext implements Serializable {
	private static final long serialVersionUID = 1L;
	ServletContext servletContext;
	String sessionId, dataId;
	CacheClient cahceClient;
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

	public CacheClient getCacheClient() {
		return cahceClient;
	}

	public Class<RequestSession<?>> getSessionClazz() {
		return sessionClazz;
	}
}
