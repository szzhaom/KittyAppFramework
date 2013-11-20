package kitty.kaf.webframe.tags;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;

public class LoopTag extends LoopTagSupport {
	private static final long serialVersionUID = -72203863459169381L;
	Object items;
	Iterator<?> iterator;
	int index;
	Object emptyValue;

	@Override
	public int doStartTag() throws JspException {
		iterator = null;
		if (emptyValue != null) {
			prepare();
			if (iterator != null && !iterator.hasNext()) {
				try {
					pageContext.getOut().write(emptyValue.toString());
				} catch (IOException e) {
					throw new JspException(e);
				}
			}
		}
		return super.doStartTag();
	}

	@Override
	protected boolean hasNext() throws JspTagException {
		return iterator != null && iterator.hasNext();
	}

	@Override
	protected Object next() throws JspTagException {
		index++;
		pageContext.setAttribute(super.itemId + "_index", index);
		return iterator.next();
	}

	@Override
	protected void prepare() throws JspTagException {
		if (iterator == null) {
			if (items instanceof Collection<?>)
				iterator = ((Collection<?>) items).iterator();
			else if (items instanceof Map<?, ?>)
				iterator = ((Map<?, ?>) items).values().iterator();
			else if (items instanceof Iterator<?>)
				iterator = (Iterator<?>) items;
			else if (items instanceof Object[]) {
				iterator = Arrays.asList(items).iterator();
			}
		}
		index = -1;
	}

	@Override
	public void release() {
		items = null;
		iterator = null;
		super.release();
	}

	public Object getItems() {
		return items;
	}

	public void setItems(Object items) {
		this.items = items;
	}

	public Object getEmptyValue() {
		return emptyValue;
	}

	public void setEmptyValue(Object emptyValue) {
		this.emptyValue = emptyValue;
	}
}
