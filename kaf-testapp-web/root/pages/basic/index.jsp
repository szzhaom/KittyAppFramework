<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%@page language="java" pageEncoding="utf-8"%>
<c:div id="pcontent"
	rendered="${mysession.user.right.basicManageEnabled}">
	<script>
		var mypc = new UIPageControl({
			'parent' : 'pcontent',
			'items' : [ {
				selected : true,
				'button' : {
					'labelParams' : {
						'html' : '文件主机'
					}
				},
				'url' : '/pages/basic/filehost/index.go'
			}, {
				'button' : {
					'labelParams' : {
						'html' : '文件分类'
					}
				},
				'url' : '/pages/basic/filecategory/index.go'
			} ]
		});
	</script>
</c:div>
<c:div styleClass="el_error"
	rendered="${!mysession.user.right.basicManageEnabled}">对不起，您没有基础管理的权限</c:div>
