package kitty.kaf.trade.web;

import javax.servlet.http.HttpServletResponse;

import kitty.kaf.trade.pack.HttpRequest;

import org.w3c.dom.Document;

/**
 * XML执行器接口。本接口用于<code>XMLTradeManager</code>执行一个XML交易。
 * 
 * @author 赵明
 * @see kaf.web.servlets#XMLTradeManager
 * @version 5.0
 * 	
 */
public interface WebExecutor {
	/**
	 * 初始化执行器，只会在WEB容器装入时调用一次
	 * 
	 * @param doc
	 *            配置文档对象，用于装入个性的配置参数
	 */
	public void init(Document doc);

	/**
	 * 清理执行器，只会有WEB容器卸载时调用一次
	 */
	public void uninit();

	/**
	 * 执行交易。
	 */
	public void execute(HttpRequest request, HttpServletResponse response)
			throws Throwable;
}
