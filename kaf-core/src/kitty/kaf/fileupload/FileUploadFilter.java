/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author zhaom
 */
public class FileUploadFilter implements Filter {

    final static KafLogger logger = KafLogger.getLogger(FileUploadFilter.class);
    int maxFileSize = 10 * 1024 * 1024;

    public void init(FilterConfig filterConfig) throws ServletException {
        String m = filterConfig.getInitParameter("maxfilesize");
        if (m != null) {
            maxFileSize = Integer.valueOf(m) * 1024 * 1024;
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest hRequest = (HttpServletRequest) request;

        boolean isMultipart = (hRequest.getHeader("content-type") != null
                && hRequest.getHeader("content-type").indexOf("multipart/form-data") != -1);
        try {
            if (isMultipart == false) {
                chain.doFilter(request, response);
            } else {
                FileUploadRequestWrapper wrapper = new FileUploadRequestWrapper(maxFileSize,
                        hRequest);
                chain.doFilter(wrapper, response);
            }
        } catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.error(e);
            }
        }
    }

    public void destroy() {
    }
}
