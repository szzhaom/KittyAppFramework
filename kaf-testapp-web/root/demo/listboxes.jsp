<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>JS DEMO</title>
<c:link href="/css/kaf-basic.css"></c:link>
<c:script src="/scripts/core/mootools-core-1.4.5.js"
	outputContextPath="true"></c:script>
<c:script src="/scripts/core/kaf-core.js"></c:script>
</head>
<body>
	<script type="text/javascript">
		var items = [ {
			'text' : '选择项1',
			'value' : 1
		}, {
			'text' : '选择项2',
			'value' : 2,
			'img' : '/images/icons/search.png'
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
		new UIListControl({
			'parent' : document.body,
			'items' : items,
			'input' : 'list1',
			'listItemParams' : {
				'createParams' : {
					'class' : 'k_icon_listitem k_listitem'
				},
				'iconParams' : {}
			},
			'value' : [ 2 ]
		});
		new UIListControl({
			'parent' : document.body,
			'items' : items,
			'input' : 'list2',
			'multiselect' : true,
			'value' : [ 1, 2, 3 ]
		});
		new UIListControl({
			'parent' : document.body,
			'searchInputParams' : {},
			'requestParams' : {
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'createParams' : {},
			'nextButtonParams' : {},
			'input' : 'list3',
			'multiselect' : true,
			'value' : [ 1, 2, 3 ]
		});
	</script>
</body>
</html>