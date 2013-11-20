package kitty.kaf.session;

import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import kitty.kaf.logging.KafLogger;
import kitty.kaf.pools.memcached.MemcachedException;


/**
 * 本会话对象只是一个空的，什么也不做
 * 
 * @author 赵明
 * @version 1.0
 */
@SuppressWarnings({ "deprecation" })
public class CookiedHttpSessionWrapper implements HttpSession {
	final static KafLogger logger = KafLogger
			.getLogger(CookiedHttpSessionWrapper.class);
	CookiedSessionContext context;

	public CookiedHttpSessionWrapper(CookiedSessionContext context)
			throws MemcachedException {
		this.context = context;
	}

	@Override
	public Object getAttribute(String arg0) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public void invalidate() {
	}

	@Override
	public void removeAttribute(String arg0) {
	}

	public void clearAttributes(Collection<?> keepAttrs) {
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
	}

	@Override
	public long getCreationTime() {
		return 0;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		return context.servletContext;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getValue(String arg0) {
		return null;
	}

	@Override
	public String[] getValueNames() {
		return null;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public void putValue(String arg0, Object arg1) {
	}

	@Override
	public void removeValue(String arg0) {
	}

	@Override
	public void setMaxInactiveInterval(int arg0) {
	}
}
