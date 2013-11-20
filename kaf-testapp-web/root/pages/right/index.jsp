<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%@page language="java" pageEncoding="utf-8"%>
<c:div id="pcontent"
	rendered="${mysession.user.right.rightManageEnabled}">
	<div id='pcs_pf'></div>
	<div id='pcs_cf'></div>
	<script>
		var mypc = new PageControl({
			'container' : 'pcs_pf', // 控件的按钮部分元素ID
			'content_container' : 'pcs_cf', // 控件的页面内容部分元素ID
			'space_class' : 'sp',
			'items' : [ {
				'selected' : true,
				'button' : {
					'html' : '角色管理'
				},
				'url' : '/pages/right/role/index.go'
			}, {
				'selected' : false,
				'button' : {
					'html' : '用户管理'
				},
				'url' : '/pages/right/user/index.go'
			} ]
		});
	</script>
</c:div>
<c:div styleClass="el_error"
	rendered="${!mysession.user.right.rightManageEnabled}">对不起，您没有权限管理的权限</c:div>
