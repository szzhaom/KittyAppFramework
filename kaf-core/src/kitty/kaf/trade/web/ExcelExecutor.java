package kitty.kaf.trade.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import kitty.kaf.trade.pack.HttpRequest;

/**
 * Excel生成器接口。
 * 
 * @author 赵明
 * @see kaf.web.servlets#XMLTradeManager
 * @version 4.1
 * 
 */
abstract public class ExcelExecutor implements WebExecutor {

	abstract protected void doExecute(HttpRequest request, WritableWorkbook book)
			throws IOException, NoSuchFieldException;

	@Override
	public void execute(HttpRequest request, HttpServletResponse response)
			throws Throwable {
		response.setCharacterEncoding("utf-8");
		request.getRequest().setCharacterEncoding("utf-8");
		String fileName = request.getParameter("filename");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ URLEncoder.encode(fileName, "utf-8"));
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		doExecute(request, Workbook.createWorkbook(o));
		byte[] b = o.toByteArray();
		response.setContentLength(b.length);
		response.setContentType("application/x-msdownload");
		response.setHeader("Connection", "close");
		response.setDateHeader("Date", System.currentTimeMillis());
		response.setHeader("Accept-Ranges", "bytes");
		response.getOutputStream().write(b);
		response.getOutputStream().flush();
	}

}
