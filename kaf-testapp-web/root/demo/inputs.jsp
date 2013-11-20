<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>输入控件</title>
<c:link href="/css/kaf-basic.css"></c:link>
<c:script src="/scripts/core/mootools-core-1.4.5.js"
	outputContextPath="true"></c:script>
<c:script src="/scripts/core/kaf-core.js"></c:script>
</head>
<body>
	<div id='frame'></div>
	<c:input id='asdfasdf'></c:input>
	<script type="text/javascript">
		var g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '普通输入'
			}
		}).addClass('w8');
		new UIInput({
			'parent' : g.panel,
			'inputParams' : {
				'placeholder' : '请输入名称',
				'id' : 'input',
				'name' : 'input'
			}
		});
		var g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '搜索框'
			}
		}).addClass('w8');
		new UIComboInput({
			'parent' : g.panel,
			'createParams' : {
				'class' : 'inline_block k_search_input k_input'
			},
			'inputParams' : {
				'placeholder' : '请输入名称'
			},
			'buttonParams' : {
				'labelParams' : {
					'class' : 'search_btn_l'
				}
			}
		});
	</script>
</body>
</html>