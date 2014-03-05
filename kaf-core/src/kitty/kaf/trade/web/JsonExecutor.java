package kitty.kaf.trade.web;

import javax.servlet.http.HttpServletResponse;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.logging.Logger;
import kitty.kaf.logging.RequestLoggerDataSource;
import kitty.kaf.trade.pack.HttpRequest;

abstract public class JsonExecutor implements WebExecutor {
	private final static Logger logger = Logger.getLogger(JsonExecutor.class);

	@Override
	public void execute(HttpRequest request, HttpServletResponse response) throws Throwable {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug(new RequestLoggerDataSource(request.getRequest()));
			}
			JSONObject o = new JSONObject();
			JSONObject r = new JSONObject();
			o.put("result", r);
			try {
				doExecute(request, response, o, r);
				r.put("success", true);
				r.put("message", "操作成功");
			} catch (CoreException e) {
				r.put("success", false);
				r.put("message", e.getMessage());
				logger.error("操作失败：", e);
			} catch (Throwable e) {
				int i = 0;
				while (i < 10 && e != e.getCause() && e.getCause() != null) {
					e = e.getCause();
					i++;
				}
				if (e != null && !(e instanceof CoreException)) {
					logger.error("操作失败：", e);
				}
				r.put("success", false);
				r.put("message", e.toString());
			}

			response.setCharacterEncoding("utf-8");
			response.setContentType("text/plain; charset=utf-8");
			response.getOutputStream().write(o.toString().getBytes("utf-8"));
		} catch (Throwable e) {
			logger.error("error", e);
		}
	}

	abstract protected void doExecute(HttpRequest request, HttpServletResponse response, JSONObject o, JSONObject r)
			throws Throwable;

}
