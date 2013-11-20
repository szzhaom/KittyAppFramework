<%@page language="java" pageEncoding="utf-8"
	import="kitty.testapp.inf.web.*"%>

<%
	((WebSession) WebSession.getCurrentSession(request)).loginOut();
	response.sendRedirect(request.getContextPath() + "/login.go");
%>