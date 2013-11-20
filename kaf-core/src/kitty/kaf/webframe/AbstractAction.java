package kitty.kaf.webframe;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletResponse;

import kitty.kaf.session.RequestSession;
import kitty.kaf.trade.pack.HttpRequest;
import kitty.kaf.util.DateTime;

abstract public class AbstractAction implements FacesAction {
	private HttpRequest request;
	private HttpServletResponse response;
	private boolean formSubmit;

	public RequestSession<?> getSession() throws NoSuchFieldException {
		return (RequestSession<?>) request.getAttribute("mysession");
	}

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public boolean isFormSubmit() {
		return formSubmit;
	}

	public void setFormSubmit(boolean formSubmit) {
		this.formSubmit = formSubmit;
	}

	public String getParameter(String name) throws NoSuchFieldException {
		return request.getParameter(name);
	}

	public String getParameterDef(String name, String def) {
		return request.getParameterDef(name, def);
	}

	public Boolean getParameterBoolean(String name) throws NoSuchFieldException {
		return request.getParameterBoolean(name);
	}

	public Boolean getParameterBooleanDef(String name, Boolean def) {
		return request.getParameterBooleanDef(name, def);
	}

	public Byte getParameterByte(String name) throws NoSuchFieldException {
		return request.getParameterByte(name);
	}

	public Byte getParameterByteDef(String name, Byte def) {
		return request.getParameterByteDef(name, def);
	}

	public Short getParameterShort(String name) throws NoSuchFieldException {
		return request.getParameterShort(name);
	}

	public Short getParameterShortDef(String name, Short def) {
		return request.getParameterShortDef(name, def);
	}

	public Integer getParameterInt(String name) throws NoSuchFieldException {
		return request.getParameterInt(name);
	}

	public Integer getParameterIntDef(String name, Integer def) {
		return request.getParameterIntDef(name, def);
	}

	public Float getParameterFloat(String name) throws NoSuchFieldException {
		return request.getParameterFloat(name);
	}

	public Float getParameterFloatDef(String name, Float def) {
		return request.getParameterFloatDef(name, def);
	}

	public Double getParameterDouble(String name) throws NoSuchFieldException {
		return request.getParameterDouble(name);
	}

	public Double getParameterDoubleDef(String name, Double def) {
		return request.getParameterDoubleDef(name, def);
	}

	public Long getParameterLong(String name) throws NoSuchFieldException {
		return request.getParameterLong(name);
	}

	public Date getParameterDate(String name, String format)
			throws NoSuchFieldException {
		return DateTime.parseDate(request.getParameter(name), format);
	}

	public Long getParameterLongDef(String name, Long def) {
		return request.getParameterLongDef(name, def);
	}

	public Object getAttribute(String name) throws NoSuchFieldException {
		return request.getAttribute(name);
	}

	public Object getAttributeDef(String name, Object value)
			throws NoSuchFieldException {
		return request.getAttributeDef(name, value);
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
