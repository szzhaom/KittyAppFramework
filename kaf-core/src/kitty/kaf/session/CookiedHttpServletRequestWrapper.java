package kitty.kaf.session;

import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kitty.kaf.helper.StringHelper;
import kitty.kaf.pools.memcached.MemcachedException;

public class CookiedHttpServletRequestWrapper extends
		javax.servlet.http.HttpServletRequestWrapper {

	HttpSession session;
	CookiedSessionContext context;
	HashMap<String, String> parameters = new HashMap<String, String>();

	public CookiedHttpServletRequestWrapper(CookiedSessionContext context,
			HttpServletRequest arg0) {
		super(arg0);
		this.context = context;
		try {
			this.setCharacterEncoding("utf-8");
			String qstr = getQueryString();
			if (qstr != null) { // 处理查询字串的编码
				String[] ls = StringHelper.splitToStringArray(qstr, "&");
				for (int i = 0; i < ls.length; i++) {
					String p = ls[i];
					if (p != null && p.length() > 0) {
						String[] pp = StringHelper.splitToStringArray(p, "=");
						String value = URLDecoder.decode(pp[1], "utf-8");
						parameters.put(pp[0], value);
					}
				}
			}
		} catch (Throwable e) {
		}
	}

	public HttpSession getSession(boolean create) {
		try {
			if (create || session == null)
				session = new CookiedHttpSessionWrapper(context);
			return session;
		} catch (MemcachedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public HttpSession getSession() {
		try {
			if (session == null)
				session = new CookiedHttpSessionWrapper(context);
			return session;
		} catch (MemcachedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getParameter(String name) {
		String o = parameters.get(name);
		if (o != null)
			return o;
		return super.getParameter(name);
	}

}
