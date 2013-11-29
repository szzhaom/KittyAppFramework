package kitty.kaf.fileupload;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import kitty.kaf.logging.KafLogger;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 
 * @author 赵明
 */
abstract public class FileUploadFilter implements Filter {

	final static KafLogger logger = KafLogger.getLogger(FileUploadFilter.class);
	protected int maxFileSize = 10 * 1024 * 1024;

	abstract protected ServletRequest createRequestWrapper(HttpServletRequest request);

	public void init(FilterConfig filterConfig) throws ServletException {
		String m = filterConfig.getInitParameter("maxfilesize");
		if (m != null) {
			maxFileSize = Integer.valueOf(m) * 1024 * 1024;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest hRequest = (HttpServletRequest) request;

		boolean isMultipart = ServletFileUpload.isMultipartContent(hRequest);
		try {
			if (isMultipart == false) {
				chain.doFilter(request, response);
			} else {
				ServletRequest wrapper = createRequestWrapper(hRequest);
				chain.doFilter(wrapper, response);
			}
		} catch (Throwable e) {
			if (logger.isDebugEnabled()) {
				logger.error("erorr:", e);
			}
		}
	}

	public void destroy() {
	}
}
