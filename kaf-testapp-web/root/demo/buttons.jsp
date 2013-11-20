<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>按钮控件</title>
<c:link href="/css/kaf-basic.css"></c:link>
<c:script src="/scripts/core/mootools-core-1.4.5.js"
	outputContextPath="true"></c:script>
<c:script src="/scripts/core/kaf-core.js"></c:script>
</head>
<body>
	<div id='frame'></div>
	<script type="text/javascript">
		var g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '无线按钮'
			}
		}).addClass('w8');
		new UIButton({
			'parent' : g.panel,
			'type' : 'radio',
			'group' : 1,
			'status' : {
				check : true
			},
			'labelParams' : {
				'text' : '按钮1'
			}
		});
		new UIButton({
			'parent' : g.panel,
			'group' : 1,
			'type' : 'radio',
			'labelParams' : {
				'text' : '按钮2'
			}
		});
		new UIButton({
			'parent' : g.panel,
			'group' : 1,
			'type' : 'radio',
			'labelParams' : {
				'text' : '按钮3'
			}
		});
		new UIButton({
			'parent' : g.panel,
			'group' : 1,
			'type' : 'radio',
			'labelParams' : {
				'text' : '按钮4'
			}
		});
		g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '复选按钮'
			}
		}).addClass('w8');
		new UIButton({
			'parent' : g.panel,
			'type' : 'check',
			'status' : {
				check : true
			},
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
			'parent' : document.body,
			'labelParams' : {
				'text' : '普通按钮'
			}
		}).addClass('w5');
		new UIButton({
			'parent' : g.panel,
			'labelParams' : {
				'text' : '确定'
			},
			'events' : {
				'statuschanged' : function(s) {
					if (s == 'down' && this.getStatus(s))
						alert('ok');
				}
			}
		});
		new UIButton({
			'parent' : g.panel,
			'labelParams' : {
				'text' : 'disabled'
			},
			'status' : {
				'disabled' : true
			}
		});
		g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '普通按钮（icon)'
			}
		}).addClass('w5');
		new UIButton({
			'parent' : g.panel,
			'labelParams' : {
				'text' : '确定'
			},
			'createParams' : {
				'class' : 'inline_block k_icon_button k_button'
			},
			'iconParams' : {
				src : contextPath + '/images/icons/search.png'
			},
			'events' : {
				'click' : function() {
					alert('ok');
				}
			}
		});
	</script>
</body>
</html>