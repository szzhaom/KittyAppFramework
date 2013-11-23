<%@page language="java" pageEncoding="utf-8"
	import="rongshi.happygame.inf.web.*"%>

<%
	((WebSession) WebSession.getCurrentSession(request)).loginOut();
	response.sendRedirect(request.getContextPath() + "/login.go");
%>