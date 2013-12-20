package kitty.kaf.trade.pack;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import kitty.kaf.exceptions.CoreException;
import kitty.kaf.helper.StringHelper;

/**
 * HTTP请求封装
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class HttpRequest {
	HttpServletRequest request;
	HashMap<String, String> parameters = null;

	/**
	 * 创建一个请求对象。由于HttpServerletRequest类并不处理查询字串的字符集，设计本类解决此问题
	 * 
	 * @param request
	 *            http request object
	 * @param charsetEncoding
	 *            请求编码字符集
	 * @param setQueryParametersToAttributes
	 *            是否将URL中的查询字串设置同步复制到request的变量中
	 * @throws UnsupportedEncodingException
	 */
	public HttpRequest(HttpServletRequest request, String charsetEncoding, boolean setQueryParametersToAttributes)
			throws UnsupportedEncodingException {
		this.request = request;
		if (setQueryParametersToAttributes) {
			Enumeration<?> em = request.getParameterNames();
			while (em.hasMoreElements()) {
				String n = em.nextElement().toString();
				request.setAttribute(n, request.getParameter(n));
			}
		}
	}

	public void setParameters(String queryString) {
		String[] s = StringHelper.splitToStringArray(queryString, "&");
		if (parameters == null)
			parameters = new HashMap<String, String>();
		else
			parameters.clear();
		for (String str : s) {
			int index = str.indexOf("=");
			if (index > 0) {
				parameters.put(str.substring(0, index).trim(), str.substring(index + 1).trim());
			} else if (index < 0)
				parameters.put(str.trim(), "");
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	protected String inGetParameter(String name) {
		String r = parameters == null ? null : parameters.get(name);
		if (r != null)
			return r;
		return request.getParameter(name);
	}

	protected Object inGetAttribute(String name) {
		return request.getAttribute(name);
	}

	public String getParameter(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return ret;
	}

	public String getParameterDef(String name, String def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else
			return ret;
	}

	public Boolean getParameterBoolean(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else {
			try {
				if (ret.equals("on"))
					return true;
				else
					return Boolean.valueOf(ret);
			} catch (Throwable e) {
				throw new CoreException("参数错误：[" + name + "]必须是布尔型", e);
			}
		}
	}

	public Boolean getParameterBooleanDef(String name, Boolean def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				if (ret.equals("on"))
					return true;
				else
					return Boolean.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Byte getParameterByte(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			try {
				return Byte.valueOf(ret);
			} catch (Throwable e) {
				throw new CoreException("参数错误：[" + name + "]必须是整数", e);
			}
	}

	public Byte getParameterByteDef(String name, Byte def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Byte.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Short getParameterShort(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			try {
				return Short.valueOf(ret);
			} catch (Throwable e) {
				throw new CoreException("参数错误：[" + name + "]必须是整数", e);
			}
	}

	public Short getParameterShortDef(String name, Short def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Short.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Integer getParameterInt(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		try {
			return Integer.valueOf(ret);
		} catch (Throwable e) {
			throw new CoreException("参数错误：[" + name + "]必须是整数", e);
		}
	}

	public Integer getParameterIntDef(String name, Integer def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Integer.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Float getParameterFloat(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			try {
				return Float.valueOf(ret);
			} catch (Throwable e) {
				throw new CoreException("参数错误：[" + name + "]必须是浮点数", e);
			}
	}

	public Float getParameterFloatDef(String name, Float def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Float.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Double getParameterDouble(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			try {
				return Double.valueOf(ret);
			} catch (Throwable e) {
				throw new CoreException("参数错误：[" + name + "]必须是浮点数", e);
			}
	}

	public Double getParameterDoubleDef(String name, Double def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Double.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Date getParameterDate(String name) throws NoSuchFieldException, ParseException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else {
			try {
				if (ret.length() == 14)
					return StringHelper.parseDateTime(ret, "yyyyMMddHHmmss");
				else if (ret.length() == 8)
					return StringHelper.parseDateTime(ret, "yyyyMMdd");
				else if (ret.length() == 10)
					return StringHelper.parseDateTime(ret, "yyyy-MM-dd");
				else
					return StringHelper.parseDateTime(ret, "yyyy-MM-dd HH:mm:ss");
			} catch (Throwable e) {
				throw new CoreException("参数错误：[" + name + "]必须是日期或时间", e);
			}
		}
	}

	public Date getParameterDateDef(String name, Date def) {
		try {
			return getParameterDate(name);
		} catch (Throwable e) {
			return def;
		}
	}

	public Long getParameterLong(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			try {
				return Long.valueOf(ret);
			} catch (Throwable e) {
				throw new CoreException("参数错误：[" + name + "]必须是整数", e);
			}
	}

	public Long getParameterLongDef(String name, Long def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Long.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Object getAttribute(String name) throws NoSuchFieldException {
		Object ret = inGetAttribute(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少变量[" + name + "]");
		else
			return ret;
	}

	public Object getAttributeDef(String name, Object value) throws NoSuchFieldException {
		Object ret = inGetAttribute(name);
		if (ret == null)
			return value;
		else
			return ret;
	}

	public void setAttribute(String name, Object value) {
		request.setAttribute(name, value);
	}

	public Enumeration<?> getParameterNames() {
		return request.getParameterNames();
	}

	public Enumeration<?> getAttributeNames() {
		return request.getAttributeNames();
	}
}
