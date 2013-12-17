<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%@page language="java" pageEncoding="utf-8"%>
<c:div id="pcontent" rendered="${true}">
	<c:script
		scriptText="var mypc = new UIPageControl({
			'parent' : 'pcontent',
			'items' :  ${mysession.menuData.rightMenuJson}
		});"></c:script>
</c:div>
<c:div styleClass="el_error" rendered="${!true}">对不起，您没有权限管理管理的权限</c:div>
