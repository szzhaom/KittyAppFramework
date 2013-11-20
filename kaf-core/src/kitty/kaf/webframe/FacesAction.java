package kitty.kaf.webframe;

import javax.servlet.http.HttpServletResponse;

import kitty.kaf.trade.pack.HttpRequest;

public interface FacesAction {

	/**
	 * 获取当前的Request对象
	 * 
	 * @return
	 */
	HttpRequest getRequest();

	/**
	 * 获取当前的Response对象
	 */
	HttpServletResponse getResponse();

	/**
	 * 指示当前请求，是否从表单提交
	 */
	boolean isFormSubmit();

	void setFormSubmit(boolean value);

	void setRequest(HttpRequest request);

	void setResponse(HttpServletResponse response);

	ActionForward execute() throws Throwable;
}
