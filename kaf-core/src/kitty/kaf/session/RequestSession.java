package kitty.kaf.session;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kitty.kaf.io.Idable;
import kitty.kaf.io.Readable;
import kitty.kaf.io.Writable;
import kitty.kaf.language.Language;

/**
 * 基于Request的Session对象
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public interface RequestSession<E extends SessionUser> extends Idable<String>, Readable, Writable {
	public HttpServletRequest getRequest();

	public void setRequest(HttpServletRequest v);

	public void setContext(CookiedSessionContext context);

	public HttpServletResponse getResponse();

	public void setResponse(HttpServletResponse response);

	/**
	 * 获取当前登录的用户
	 */
	public E getUser();

	/**
	 * 判断当前用户是否登录
	 * 
	 */
	public boolean isLogined();

	/**
	 * 保存改变
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void save() throws InterruptedException, IOException;

	/**
	 * 装入Session
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void load() throws InterruptedException, IOException;

	/**
	 * 获取当前Session的语言
	 */
	public Language getLanguage();
}
