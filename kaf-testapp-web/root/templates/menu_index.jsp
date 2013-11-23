<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%@page language="java" pageEncoding="utf-8"%>
<c:div id="pcontent" rendered="${${template.page.right}}">
	<script>
		var mypc = new UIPageControl({
			'parent' : 'pcontent',
			'items' :  ${template.menu_items}
		});
	</script>
</c:div>
<c:div styleClass="el_error" rendered="${!${template.page.right}}">对不起，您没有${template.menu_desp}管理的权限</c:div>
