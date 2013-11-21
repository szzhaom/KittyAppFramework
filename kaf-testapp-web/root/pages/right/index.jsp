<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%@page language="java" pageEncoding="utf-8"%>
<c:div id="pcontent"
	rendered="${mysession.user.right.rightManageEnabled}">
	<div id='pcs_pf'></div>
	<div id='pcs_cf'></div>
	<script>
		var mypc = new UIPageControl({
			'parent' : 'pcontent',
			'items' : [ {
				'selected' : true,
				'button' : {
					'labelParams' : {
						'html' : '角色管理'
					}
				},
				'url' : '/pages/right/role/index.go'
			}, {
				'selected' : false,
				'button' : {
					'labelParams' : {
						'html' : '用户管理'
					}
				},
				'url' : '/pages/right/user/index.go'
			} ]
		});
	</script>
</c:div>
<c:div styleClass="el_error"
	rendered="${!mysession.user.right.rightManageEnabled}">对不起，您没有权限管理的权限</c:div>
