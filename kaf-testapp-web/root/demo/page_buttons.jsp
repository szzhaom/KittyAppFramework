<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<div id='buttonparent'></div>
<script type="text/javascript">
	var g = new UIGroup({
		'parent' : 'buttonparent',
		'labelParams' : {
			'text' : '无线按钮'
		}
	}).addClass('w8');
	new UIButton({
		'parent' : g.panel,
		'type' : 'radio',
		'labelParams' : {
			'text' : '按钮1'
		}
	});
	new UIButton({
		'parent' : g.panel,
		'type' : 'radio',
		'labelParams' : {
			'text' : '按钮2'
		}
	});
	new UIButton({
		'parent' : g.panel,
		'type' : 'radio',
		'labelParams' : {
			'text' : '按钮3'
		}
	});
	new UIButton({
		'parent' : g.panel,
		'type' : 'radio',
		'labelParams' : {
			'text' : '按钮4'
		}
	});
	g = new UIGroup({
		'parent' : 'buttonparent',
		'labelParams' : {
			'text' : '复选按钮'
		}
	}).addClass('w8');
	new UIButton({
		'parent' : g.panel,
		'type' : 'check',
		'labelParams' : {
			'text' : '按钮1'
		}
	});
	new UIButton({
		'parent' : g.panel,
		'type' : 'check',
		'labelParams' : {
			'text' : '按钮2'
		}
	});
	new UIButton({
		'parent' : g.panel,
		'type' : 'check',
		'labelParams' : {
			'text' : '按钮3'
		}
	});
	g = new UIGroup({
		'parent' : 'buttonparent',
		'labelParams' : {
			'text' : '普通按钮'
		}
	}).addClass('w5');
	new UIButton({
		'parent' : g.panel,
		'labelParams' : {
			'text' : '确定'
		}
	});
</script>
