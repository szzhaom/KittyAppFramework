<%@page import="kitty.kaf.session.CookiedSessionFilter"%>
<%@page pageEncoding="UTF-8"%>
<%
	CookiedSessionFilter.filter(request, response);
%>
<jsp:forward page="index.go" />
