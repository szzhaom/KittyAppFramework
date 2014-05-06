package kitty.kaf.fileupload;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.logging.Logger;

/**
 * 
 * @author 赵明
 */
abstract public class FileUploadFilter implements Filter {

	final static Logger logger = Logger.getLogger(FileUploadFilter.class);
	protected long maxFileSize = 10 * 1024 * 1024;

	abstract protected ServletRequest createRequestWrapper(HttpServletRequest request);

	public void init(FilterConfig filterConfig) throws ServletException {
		String m = filterConfig.getInitParameter("maxfilesize");
		if (m != null) {
			maxFileSize = Long.valueOf(m);
		}
	}

	public boolean isMultipartContent(HttpServletRequest request) {
		return request.getContentType() != null && request.getContentType().contains("multipart/form-data; ");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest hRequest = (HttpServletRequest) request;

		boolean isMultipart = isMultipartContent(hRequest);
		try {
			if (isMultipart == false) {
				chain.doFilter(request, response);
			} else {
				ServletRequest wrapper = createRequestWrapper(hRequest);
				chain.doFilter(wrapper, response);
			}
		} catch (Throwable e) {
			int i = 0;
			while (i < 10 && e != e.getCause() && e.getCause() != null) {
				e = e.getCause();
				i++;
			}
			if (logger.isDebugEnabled() && e != null && !(e instanceof CoreException)) {
				logger.error("操作失败：", e);
			}
			try {
				JSONObject o = new JSONObject();
				JSONObject r = new JSONObject();
				o.put("result", r);
				r.put("success", false);
				r.put("message", e.getMessage());

				response.setCharacterEncoding("utf-8");
				response.setContentType("text/plain; charset=utf-8");
				response.getOutputStream().write(o.toString().getBytes("utf-8"));
			} catch (Throwable ex) {
			}
		}
	}

	public void destroy() {
	}
}
