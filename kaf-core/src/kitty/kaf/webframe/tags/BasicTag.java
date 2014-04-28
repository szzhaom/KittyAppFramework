package kitty.kaf.webframe.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import kitty.kaf.logging.Logger;

/**
 * 基础的标签支持
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public abstract class BasicTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(TagSupport.class);
	private Object rendered;

	public BasicTag() {
		super();
	}

	protected String getScriptScope() {
		String ret = (String) pageContext.getAttribute("JSSCOPE");
		return ret != null ? ret + "." : "";
	}

	/**
	 * 找到标签的表单标签
	 */
	public FormTag getForm() {
		Tag p = this;
		while (p != null) {
			if (p instanceof FormTag)
				return (FormTag) p;
			p = p.getParent();
		}
		return null;
	}

	@Override
	public void release() {
		rendered = null;
		super.release();
	}

	/**
	 * 输出Url，如果url是以/开始，则自动将上下文路径加至Url前面
	 * 
	 * @param writer
	 *            JspWriter
	 * @param url
	 *            要写入的url字串
	 * @throws IOException
	 *             如果输出失败
	 */
	public void writeUrl(JspWriter writer, String url) throws IOException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		if (url != null) {
			if (url.startsWith("/"))
				writer.write(request.getContextPath() + url);
			else {
				if (url.startsWith("\\"))
					url = "/" + url.substring(1);
				writer.write(url);
			}
		}
	}

	/**
	 * 输出url属性,如果url为null，则不输出，如果url不为null，则自动将上下方路径添加至url前面输出
	 * 
	 * @param writer
	 *            JspWriter
	 * @param name
	 *            HTML元素的属性名称
	 * @param url
	 *            要写入的url字串
	 * @throws IOException
	 *             如果输出失败
	 */
	public void writeUrlAttribute(JspWriter writer, String name, String url) throws IOException {
		if (url != null) {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			if (url.startsWith("/"))
				writer.write(" " + name + "=\"" + request.getContextPath() + url + "\"");
			else {
				if (url.startsWith("\\"))
					url = "/" + url.substring(1);
				writer.write(" " + name + "=\"" + url + "\"");
			}
		}
	}

	/**
	 * 输出HTML元素属性，如果value为null,则不输出
	 * 
	 * @param writer
	 *            JspWriter
	 * @param name
	 *            HTML元素的属性名称
	 * @param value
	 *            要写入的属性值
	 * @throws IOException
	 *             如果输出失败
	 */
	public void writeAttribute(JspWriter writer, String name, Object value) throws IOException {
		if (value != null)
			writer.write(" " + name + "=\"" + value + "\"");
	}

	/**
	 * 输出文字，如果value为null，则不输出
	 * 
	 * @param writer
	 *            JspWriter
	 * @param value
	 *            要写入的文字值
	 * @throws IOException
	 *             如果输出失败
	 */
	public void writeText(JspWriter writer, Object value) throws IOException {
		if (value != null)
			writer.write(value.toString());
	}

	/**
	 * 标签输出开始
	 * 
	 * @param writer
	 *            JspWriter
	 * @throws IOException
	 *             如果输出失败
	 */
	abstract protected void doStartTag(JspWriter writer) throws IOException;

	/**
	 * 获得doStartTag的返回值，默认返回EVAL_BODY_INCLUDE
	 */
	protected int getStartTagReturnCode() {
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 标签输出结束
	 * 
	 * @param writer
	 *            JspWriter
	 * @throws IOException
	 *             如果输出失败
	 */
	abstract protected void doEndTag(JspWriter writer) throws IOException;

	/**
	 * 获得doEndTag的返回值，默认返回EVAL_PAGE
	 */
	protected int getEndTagReutrnCode() {
		return EVAL_PAGE;
	}

	/**
	 * 继承自{@link TagSupport#doStartTag()}
	 * ，做一些基础处理，并调用doStartTag(writer)实际输出标签开始内容
	 * 
	 * @see {@link TagSupport#doStartTag()}
	 */
	@Override
	final public int doStartTag() throws JspException {
		if (Boolean.FALSE.equals(getRendered()))
			return SKIP_BODY;
		Object p = pageContext.getRequest().getAttribute("fromfaces");
		if (p == null || !Boolean.TRUE.equals(p))
			throw new JspException("请不要直接调用.jsp文件");
		try {
			doStartTag(pageContext.getOut());
			return getStartTagReturnCode();
		} catch (IOException e) {
			if (logger.isDebugEnabled())
				logger.error("", e);
			throw new JspException(e.getCause());
		}
	}

	/**
	 * 继承自{@link TagSupport#doEndTag()}，做一些基础处理，并调用doEndTag(writer)实际输出标签结束内容
	 * 
	 * @see {@link TagSupport#doEndTag()}
	 */
	@Override
	final public int doEndTag() throws JspException {
		if (Boolean.FALSE.equals(getRendered())) {
			return EVAL_PAGE;
		}
		try {
			doEndTag(pageContext.getOut());
			return getEndTagReutrnCode();
		} catch (IOException e) {
			throw new JspException(e.getCause());
		}
	}

	/**
	 * 获取当前标签是否渲染，即是否输出标签的内容
	 */
	public Object getRendered() {
		return rendered;
	}

	/**
	 * 设置当前标签是否需要渲染，即是否需要输出标签的内容
	 */
	public void setRendered(Object rendered) throws JspException {
		if (rendered == null)
			this.rendered = true;
		else if (rendered instanceof Boolean)
			this.rendered = rendered;
		else
			this.rendered = "true".equalsIgnoreCase(rendered.toString());
	}

}
