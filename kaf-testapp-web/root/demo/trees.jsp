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
	<script type="text/javascript">
		var items = [ {
			'text' : '选择项1',
			'value' : 1
		}, {
			'text' : '选择项2',
			'value' : 2,
			items : [ {
				'text' : 'aaa',
				value : 7
			}, {
				'text' : 'bbb',
				value : 8
			}, {
				'text' : 'ccc',
				value : 9,
				items : [ {
					'text' : 'aaa',
					value : 10
				}, {
					'text' : 'bbb',
					value : 11,
					'needload' : true
				}, {
					'text' : 'ccc',
					value : 12
				} ]
			} ]
		}, {
			'text' : '选择项3',
			'value' : 3
		}, {
			'text' : '选择项4',
			'value' : 4
		}, {
			'text' : '选择项5',
			'value' : 5
		}, {
			'text' : '选择项6',
			'value' : 6
		} ];
		new UITreeControl({
			'parent' : 'frame',
			'listItemParams' : {
				'createParams' : {
					'class' : 'k_treeitem'
				},
				'classPrefix' : 'k_treeitem'
			},
			'requestParams' : {
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'items' : items,
			'input' : 'tree',
			'value' : [ 2 ],
			'multiselect' : false
		});
	</script>
</body>
</html>