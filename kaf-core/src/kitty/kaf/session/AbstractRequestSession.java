package kitty.kaf.session;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kitty.kaf.helper.SecurityHelper;
import kitty.kaf.helper.StringHelper;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.IdObject;

public abstract class AbstractRequestSession<E extends SessionUser> extends
		IdObject<String> implements RequestSession<E> {
	private static final long serialVersionUID = 1L;
	protected CookiedSessionContext context;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected long cookieUserId, userId;
	protected String cookieLoginName = "";
	protected byte[] cookieloginKey;
	protected final byte[] LOGIN_KEY = "$Kit95p.we;]dfg85c;a'fty".getBytes();
	protected E user;

	public AbstractRequestSession() {
		super();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setIdString(String id) {
		setId(id);
	}

	@Override
	public String getIdString() {
		return getId();
	}

	public String getCookieLoginName() {
		if (cookieLoginName == null)
			return "";
		else
			return cookieLoginName;
	}

	protected E loadAndCheckUser(long userId) {
		E user = loadUser(userId);
		if (user == null)
			this.userId = -1;
		return user;
	}

	public E getUser() {
		if (user == null) {
			if (userId > 0) {
				user = loadAndCheckUser(userId);
			}
		}
		return user;
	}

	public void setContext(CookiedSessionContext context) {
		this.context = context;
	}

	public long getSessionTimeout() {
		return -1;
	}

	public boolean isTimeout(long createTime) {
		return getSessionTimeout() > 0
				&& kitty.kaf.util.DateTime.secondsBetween(
						System.currentTimeMillis(), createTime) > getSessionTimeout();
	}

	@Override
	public void load() throws InterruptedException, IOException {
		context.mc.load("sid_" + getId(), this);
		long createTime = 0;
		cookieUserId = -1;
		try {
			for (Cookie o : getRequest().getCookies()) {
				if (o.getName().equals(getContext().getDataId())) {
					// dataCookie = o;
					String p[] = StringHelper.splitToStringArray(o.getValue(),
							"-");
					cookieUserId = Long.valueOf(p[0]);
					cookieloginKey = StringHelper.hexToBytes(p[1]);
					createTime = Long.valueOf(p[2]);
				} else if (o.getName().equals(getContext().getDataId() + "_n")) {
					cookieLoginName = o.getValue();
				}
			}
			if (isTimeout(createTime)) {
				cookieUserId = -1;
			}
		} catch (Throwable e) {
			cookieUserId = -1;
		}
		if (userId <= 0) { // 假如Session已过期或未保存用户
			if (cookieUserId > 0) { // 从Cookie中取已登录的用户
				try {
					String[] a = StringHelper.splitToStringArray(
							new String(SecurityHelper.des3Decrypt(LOGIN_KEY,
									cookieloginKey)), "|");
					if (a[0].equals(Long.toString(cookieUserId))
							&& a[1].equals(getId())) {
						userId = cookieUserId;
						user = loadAndCheckUser(cookieUserId);
						if (user != null) { // 视为登录成功
							save();
							return;
						}
					}
				} catch (Throwable e) {
				}
			}
		} else {
			if (cookieUserId <= 0) {
				user = loadAndCheckUser(cookieUserId);
				saveUserToCookie(user);
			}
		}
	}

	/**
	 * 根据用户ID装入用户数据
	 * 
	 * @param userId
	 */
	abstract protected E loadUser(long userId);

	public void saveLoginNameCookie(String name) {
		try {
			this.cookieLoginName = name;
			Cookie cookie = new Cookie(getContext().getDataId() + "_n",
					cookieLoginName);
			cookie.setMaxAge(Integer.MAX_VALUE);
			cookie.setPath(getRequest().getContextPath());
			if (cookie.getPath() == null || cookie.getPath().isEmpty())
				cookie.setPath("/");
			else if (!cookie.getPath().startsWith("/"))
				cookie.setPath("/" + cookie.getPath());
			getResponse().addCookie(cookie);
		} catch (Throwable e) {
		}
	}

	public void saveUserToCookie(SessionUser user) {
		if (user != null) {
			this.cookieUserId = user.getUserId();
			this.cookieLoginName = user.getLoginName();
			try {
				byte[] b = SecurityHelper.des3Encrypt(LOGIN_KEY,
						(user.getUserId() + "|" + getId()).getBytes());
				String k = user.getUserId() + "-" + StringHelper.bytesToHex(b)
						+ "-" + System.currentTimeMillis();
				Cookie cookie = new Cookie(getContext().getDataId(), k);
				cookie.setMaxAge(Integer.MAX_VALUE);
				cookie.setPath(getRequest().getContextPath());
				if (cookie.getPath() == null || cookie.getPath().isEmpty())
					cookie.setPath("/");
				else if (!cookie.getPath().startsWith("/"))
					cookie.setPath("/" + cookie.getPath());
				getResponse().addCookie(cookie);
				cookie = new Cookie(getContext().getDataId() + "_n",
						cookieLoginName);
				cookie.setMaxAge(Integer.MAX_VALUE);
				cookie.setPath(getRequest().getContextPath());
				if (cookie.getPath() == null || cookie.getPath().isEmpty())
					cookie.setPath("/");
				else if (!cookie.getPath().startsWith("/"))
					cookie.setPath("/" + cookie.getPath());
				getResponse().addCookie(cookie);
			} catch (Throwable e) {
			}
		}
	}

	public void loginSuccess(E user) throws InterruptedException, IOException {
		if (user != null) {
			this.user = user;
			this.userId = user.getUserId();
			save();
			saveUserToCookie(user);
		}
	}

	public void loginOut() throws InterruptedException, IOException {
		this.user = null;
		userId = -1;
		save();
		Cookie cookie = new Cookie(getContext().getDataId(), null);
		cookie.setMaxAge(0);
		cookie.setPath(getRequest().getContextPath());
		if (cookie.getPath() == null || cookie.getPath().isEmpty())
			cookie.setPath("/");
		else if (!cookie.getPath().startsWith("/"))
			cookie.setPath("/" + cookie.getPath());
		getResponse().addCookie(cookie);
	}

	public boolean isLogined() {
		return userId > 0;
	}

	@Override
	public void save() throws InterruptedException, IOException {
		context.mc.set("sid_" + getId(), this, null);
	}

	public CookiedSessionContext getContext() {
		return context;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public static RequestSession<?> getCurrentSession(HttpServletRequest request) {
		return (RequestSession<?>) request.getAttribute("mysession");
	}

	@Override
	public void readFromStream(DataRead stream) throws IOException {
		userId = stream.readLong();
	}

	@Override
	public void writeToStream(DataWrite stream) throws IOException {
		stream.writeLong(userId);
	}

}
