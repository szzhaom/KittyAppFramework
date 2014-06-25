package kitty.kaf.session;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kitty.kaf.helper.SecurityHelper;
import kitty.kaf.helper.StringHelper;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.IdObject;
import kitty.kaf.language.Language;
import kitty.kaf.language.LanguageHelper;

public abstract class AbstractRequestSession<E extends SessionUser> extends IdObject<String> implements
		RequestSession<E> {
	private static final long serialVersionUID = 1L;
	protected CookiedSessionContext context;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected long cookieUserId, userId;
	protected String cookieLoginName = "";
	protected byte[] cookieloginKey;
	private final byte[] LOGIN_KEY = "$Kit95p.we;]dfg85c;a'fty".getBytes();
	protected E user;
	private String languageCountry;

	Language language;

	@Override
	public Language getLanguage() {
		if (language == null) {
			language = LanguageHelper.getDefault();
		}
		return language;
	}

	public AbstractRequestSession() {
		super();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public byte[] getLoginKey() {
		return LOGIN_KEY;
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
				&& kitty.kaf.util.DateTime.secondsBetween(System.currentTimeMillis(), createTime) > getSessionTimeout();
	}

	public String getCookieNamePrefix() {
		return getContext().getDataId();
	}

	@Override
	public void load() throws InterruptedException, IOException {
		context.cahceClient.load("sid_" + getId(), this);
		long createTime = 0;
		cookieUserId = -1;
		try {
			for (Cookie o : getRequest().getCookies()) {
				if (o.getName().equals(getCookieNamePrefix())) {
					// dataCookie = o;
					String p[] = StringHelper.splitToStringArray(o.getValue(), "-");
					cookieUserId = Long.valueOf(p[0]);
					cookieloginKey = StringHelper.hexToBytes(p[1]);
					createTime = Long.valueOf(p[2]);
				} else if (o.getName().equals(getCookieNamePrefix() + "_n")) {
					cookieLoginName = o.getValue();
				} else if (o.getName().equals(getCookieNamePrefix() + "_lc")) {
					languageCountry = o.getValue();
					language = LanguageHelper.get(languageCountry);
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
							new String(SecurityHelper.des3Decrypt(getLoginKey(), cookieloginKey)), "|");
					if (a[0].equals(Long.toString(cookieUserId))) {// &&
																	// a[1].equals(getId()))
																	// {
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
			Cookie cookie = new Cookie(getCookieNamePrefix() + "_n", cookieLoginName);
			cookie.setMaxAge(Integer.MAX_VALUE);
			setCookiePath(cookie);
			getResponse().addCookie(cookie);
		} catch (Throwable e) {
		}
	}

	/**
	 * 设置语言国家代码
	 * 
	 * @param languageCountry
	 *            新的语言国家代码
	 */
	public void saveLanguageCountryCookie(String languageCountry) {
		try {
			this.languageCountry = languageCountry;
			Cookie cookie = new Cookie(getCookieNamePrefix() + "_lc", languageCountry);
			cookie.setMaxAge(Integer.MAX_VALUE);
			setCookiePath(cookie);
			getResponse().addCookie(cookie);
		} catch (Throwable e) {
		}
	}

	public void saveUserToCookie(SessionUser user) {
		if (user != null) {
			this.cookieUserId = user.getUserId();
			this.cookieLoginName = user.getLoginName();
			try {
				byte[] b = SecurityHelper.des3Encrypt(getLoginKey(), (user.getUserId() + "|" + getId()).getBytes());
				String k = user.getUserId() + "-" + StringHelper.bytesToHex(b) + "-" + System.currentTimeMillis();
				Cookie cookie = new Cookie(getCookieNamePrefix(), k);
				cookie.setMaxAge(Integer.MAX_VALUE);
				setCookiePath(cookie);
				getResponse().addCookie(cookie);
				cookie = new Cookie(getCookieNamePrefix() + "_n", cookieLoginName);
				cookie.setMaxAge(Integer.MAX_VALUE);
				setCookiePath(cookie);
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

	public void setCookiePath(Cookie cookie) {
		cookie.setPath(request.getContextPath());
		if (cookie.getPath() == null || cookie.getPath().isEmpty())
			cookie.setPath("/");
		else if (!cookie.getPath().startsWith("/"))
			cookie.setPath("/" + cookie.getPath());
	}

	public void loginOut() throws InterruptedException, IOException {
		this.user = null;
		userId = -1;
		save();
		Cookie cookie = new Cookie(getCookieNamePrefix(), null);
		cookie.setMaxAge(0);
		setCookiePath(cookie);
		getResponse().addCookie(cookie);
	}

	public boolean isLogined() {
		return userId > 0;
	}

	@Override
	public void save() throws InterruptedException, IOException {
		context.cahceClient.set("sid_" + getId(), this, null);
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

	public List<?> getLanguageList() {
		return LanguageHelper.getLanguageList();
	}
}
