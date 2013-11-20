package kitty.kaf.etag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ETagFilter implements Filter {
	static ConcurrentHashMap<String, String> etagMap = new ConcurrentHashMap<String, String>();

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse hResponse = (HttpServletResponse) response;
		HttpServletRequest hRequest = (HttpServletRequest) request;
		String reqEtag = hRequest.getHeader("If-None-Match");
		if (reqEtag != null && !reqEtag.isEmpty()) {
			String savedTag = etagMap.get(hRequest.getRequestURI());
			if (savedTag != null && savedTag.equals(reqEtag)) {
				hResponse.setHeader("Etag", reqEtag);
				hResponse.setDateHeader("Last-Modified",
						hRequest.getDateHeader("Last-Modified"));
				hResponse.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		MyHttpResponseWrapper resp = new MyHttpResponseWrapper(hResponse,
				buffer);
		MyRequestWrapper req = new MyRequestWrapper(hRequest);
		chain.doFilter(req, resp);
		CRC32 crc = new CRC32();
		byte[] bytes = buffer.toByteArray();
		crc.update(bytes);
		String token = "W/\"" + bytes.length + "-1338089600167\"";
		hResponse.setHeader("ETag", token);

		etagMap.put(hRequest.getRequestURI(), token);
		// hResponse.setContentLength(bytes.length);
		// ServletOutputStream sos = hResponse.getOutputStream();
		// sos.write(bytes);
		// sos.flush();
		// sos.close();
		// chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	private static class MyRequestWrapper extends HttpServletRequestWrapper {
		public MyRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getHeader(String name) {
			String ret = super.getHeader(name);
			if (name.equalsIgnoreCase("If-None-Match"))
				return "W/\"1234567890\"";
			else
				return ret;
		}

	}

	private static class MyHttpResponseWrapper extends
			HttpServletResponseWrapper {

		ByteServletOutputStream servletOutputStream;
		PrintWriter printWriter;

		public MyHttpResponseWrapper(HttpServletResponse response,
				ByteArrayOutputStream buffer) throws IOException {
			super(response);
			servletOutputStream = new ByteServletOutputStream(
					response.getOutputStream(), buffer);
		}

		public ServletOutputStream getOutputStream() throws IOException {
			return servletOutputStream;
		}

		public PrintWriter getWriter() throws IOException {
			if (printWriter == null) {
				printWriter = new PrintWriter(servletOutputStream);
			}
			return printWriter;
		}

		public void flushBuffer() throws IOException {
			servletOutputStream.flush();
			if (printWriter != null) {
				printWriter.flush();
			}
		}

		@Override
		public void addHeader(String name, String value) {
			if (!name.equalsIgnoreCase("etag"))
				super.addHeader(name, value);
		}

		@Override
		public void setHeader(String name, String value) {
			if (!name.equalsIgnoreCase("etag"))
				super.setHeader(name, value);
		}
	}

	private static class ByteServletOutputStream extends ServletOutputStream {

		ByteArrayOutputStream baos;
		OutputStream responseOutputStream;

		public ByteServletOutputStream(OutputStream responseOutputStream,
				ByteArrayOutputStream baos) {
			super();
			this.baos = baos;
			this.responseOutputStream = responseOutputStream;
		}

		public void write(int b) throws IOException {
			baos.write(b);
			responseOutputStream.write(b);
		}
	}
}
