package kitty.testapp.inf.web.actions.right;

import kitty.kaf.helper.StringHelper;
import kitty.kaf.webframe.AbstractAction;
import kitty.kaf.webframe.ActionForward;
import kitty.kaf.webframe.ActionForwardType;
import kitty.testapp.inf.ds.right.UserHelper;
import kitty.testapp.inf.web.WebSession;

/**
 * 登录
 * 
 * @author 赵明
 * 
 */
public class LoginAction extends AbstractAction {
	private String loginName;
	private String redirectUrl;

	@Override
	public ActionForward execute() throws Throwable {
		if (isFormSubmit()) {
			loginName = getParameter("loginname");
			String url = getParameterDef("rurl", null);
			WebSession session = (WebSession) getSession();
			String code = session.getParameters().remove("vercode");
			if (code == null || !code.equalsIgnoreCase(getParameter("vercode"))) {
				session.saveLoginNameCookie(loginName);
				throw new Exception("无效的验证码");
			}
			UserHelper.login(session, loginName, getParameter("password"));
			if (url == null || url.isEmpty())
				url = "/index.go";
			else
				url = new String(StringHelper.hexToBytes(url));
			return new ActionForward(ActionForwardType.REDIRECT_URL, url);
		} else
			redirectUrl = (String) getParameterDef("rurl", null);

		return null;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}
