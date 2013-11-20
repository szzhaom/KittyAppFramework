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
			'text' : 'aaaa',
			'value' : 1
		}, {
			'text' : 'aabb',
			'value' : 2
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
		new UIComboBox({
			'parent' : document.body,
			'input' : 'list0',
			'list' : {
				'items' : items
			}
		});
		new UIComboBox({
			'parent' : document.body,
			'input' : 'list1',
			'autocomplete' : true,
			'list' : {
				'requestParams' : {
					'textfield' : 'company_category_desp',
					'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
				},
				initload : true
			}
		});
		new UIComboBox({
			'parent' : document.body,
			'input' : 'list2',
			'list' : {
				'multiselect' : true,
				'value' : [ 1, 2, 3 ],
				'items' : items
			},
			'inputParams' : {
				'readonly' : 'readonly'
			}
		});
		new UIChosenBox({
			'parent' : document.body,
			'input' : 'list3',
			'value' : [ {
				'value' : 1,
				'text' : '选择项1'
			}, {
				'value' : 2,
				'text' : '选择项2'
			} ],
			'autocomplete' : true,
			'list' : {
				'multiselect' : true,
				'initload' : true,
				'requestParams' : {
					'textfield' : 'company_category_desp',
					'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
				}
			}
		});
		new UIChosenBox({
			'parent' : document.body,
			'input' : 'list4',
			'value' : [ {
				'value' : 1,
				'text' : '选择项1'
			}, {
				'value' : 2,
				'text' : '选择项2'
			} ],
			'autocomplete' : false,
			'list' : {
				'multiselect' : true,
				'initload' : true,
				'searchInputParams' : {},
				'nextButtonParams' : {},
				'requestParams' : {
					'textfield' : 'company_category_desp',
					'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
				}
			}
		});
	</script>
</body>
</html>