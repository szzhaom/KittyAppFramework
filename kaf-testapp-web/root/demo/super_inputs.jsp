<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>全能输入控件</title>
<c:link href="/css/kaf-basic.css"></c:link>
<c:script src="/scripts/core/mootools-core-1.4.5.js"
	outputContextPath="true"></c:script>
<c:script src="/scripts/core/kaf-core.js"></c:script>
<style type="text/css">
.inline_block {
	margin: 5px;
}
</style>
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
		var treeitems = [ {
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
		var g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '输入框1'
			},
			'createParams' : {
				'class' : 'k_group',
				'style' : 'margin:10px'
			}
		});
		$input({
			'type' : 'text',
			'placeholder' : '请输入名称',
			'id' : 'input1',
			'value' : 'abcd',
			'parent' : g.panel
		});
		$input({
			'type' : 'password',
			'id' : 'input2',
			'parent' : g.panel
		});
		$input({
			'type' : 'search',
			'id' : 'input3',
			'parent' : g.panel
		});

		var g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '列表框'
			},
			'createParams' : {
				'class' : 'k_group',
				'style' : 'margin:10px'
			}
		});
		$input({
			'type' : 'icon_list',
			'id' : 'list3',
			'parent' : g.panel,
			'searchInputParams' : {},
			'nextButtonParams' : {},
			'requestParams' : {
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'multiselect' : true,
			'value' : [ 1, 2, 3 ]
		});
		$input({
			'type' : 'list',
			'id' : 'list1',
			'parent' : g.panel,
			'items' : items,
			'multiselect' : true,
			'value' : [ 1, 2, 3 ]
		});
		$input({
			'type' : 'icon_list',
			'id' : 'list2',
			'parent' : g.panel,
			'items' : items,
			'multiselect' : true,
			'value' : [ 1, 2, 3 ]
		});

		g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '组合框'
			},
			'createParams' : {
				'class' : 'k_group',
				'style' : 'margin:10px'
			}
		});
		$input({
			'type' : 'combobox',
			'id' : 'combobox1',
			'parent' : g.panel,
			'searchInputParams' : {},
			'nextButtonParams' : {},
			'requestParams' : {
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'multiselect' : true,
			'value' : [ 1, 2, 3 ]
		});
		$input({
			'type' : 'combobox',
			'id' : 'combobox2',
			'parent' : g.panel,
			'autocomplete' : true,
			'requestParams' : {
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'value' : [ 1, 2, 3 ]
		});
		$input({
			'type' : 'chosenbox',
			'id' : 'chosenbox1',
			'parent' : g.panel,
			'multiselect' : true,
			'requestParams' : {
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'value' : [ {
				'value' : 1,
				'text' : '选择项1'
			}, {
				'value' : 2,
				'text' : '选择项2'
			} ]
		});
		$input({
			'type' : 'chosenbox',
			'id' : 'chosenbox2',
			'parent' : g.panel,
			'searchInputParams' : {},
			'nextButtonParams' : {},
			'multiselect' : true,
			'requestParams' : {
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'value' : [ {
				'value' : 1,
				'text' : '选择项1'
			}, {
				'value' : 2,
				'text' : '选择项2'
			} ]
		});

		g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '组合框'
			},
			'createParams' : {
				'class' : 'k_group',
				'style' : 'margin:10px'
			}
		});
		$input({
			'type' : 'date',
			'id' : 'date1',
			'parent' : g.panel,
			'value' : '2013-10-10',
			'maxValue' : '2015-01-01',
			'minValue' : '1970-01-01'
		});
		$input({
			'type' : 'datetime',
			'id' : 'date1',
			'parent' : g.panel,
			'value' : '2013-10-10',
			'maxValue' : '2015-01-01',
			'minValue' : '1970-01-01'
		});
		$input({
			'type' : 'datebox',
			'id' : 'date1',
			'parent' : g.panel,
			'value' : '2013-10-10',
			'maxValue' : '2015-01-01',
			'minValue' : '1970-01-01'
		});
		$input({
			'type' : 'datetimebox',
			'id' : 'datetime11',
			'parent' : g.panel,
			'value' : '2013-10-10',
			'maxValue' : '2015-01-01',
			'minValue' : '1970-01-01'
		});
		g = new UIGroup({
			'parent' : document.body,
			'labelParams' : {
				'text' : '树'
			},
			'createParams' : {
				'class' : 'k_group',
				'style' : 'margin:10px'
			}
		});
		$input({
			'type' : 'tree',
			'id' : 'tree1',
			'parent' : g.panel,
			'items' : treeitems,
			'requestParams' : {
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'value' : [ 2 ]
		});
	</script>
</body>
</html>