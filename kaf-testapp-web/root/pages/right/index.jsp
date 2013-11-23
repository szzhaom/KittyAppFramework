<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%@page language="java" pageEncoding="utf-8"%>
<c:div id="pcontent" rendered="${true}">
	<script>
		var mypc = new UIPageControl({
			'parent' : 'pcontent',
			'items' :  [{"selected":true,"button":{"labelParams":{"html":"角色"}},"url":"/pages/right/role/index.go"},{"button":{"labelParams":{"html":"用户"}},"url":"/pages/right/user/index.go"}]
		});
	</script>
</c:div>
<c:div styleClass="el_error" rendered="${!true}">对不起，您没有权限管理管理的权限</c:div>
