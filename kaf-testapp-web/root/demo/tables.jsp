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
	<div id='frame' style='padding: 10px;'></div>
	<div id='ps'></div>
	<script type="text/javascript">
		var items = [ {
			"id" : 1,
			"company_category_desp" : "游戏公司"
		}, {
			"id" : 2,
			"company_category_desp" : "贸易公司"
		}, {
			"id" : 3,
			"company_category_desp" : "AB公司"
		} ];
		new UITableControl({
			'parent' : 'frame',
			'requestParams' : {
				'maxResults' : 1,
				'textfield' : 'company_category_desp',
				'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory'
			},
			'desp' : '公司分类',
			'prefix' : 'filehost',
			'columns' : [ {
				width : 50,
				field : 'id',
				row_class : 'tcenter tfield',
				head_class : 'tcenter head_text',
				checkbox : true
			}, {
				width : 80,
				field : 'id',
				row_class : 'tcenter tfield',
				head_class : 'tcenter head_text',
				caption : '公司分类ID'
			}, {
				width : 'auto',
				field : 'company_category_desp',
				row_class : 'tcenter tfield',
				head_class : 'tcenter head_text',
				caption : '公司分类描述'
			}, {
				width : 80,
				field : 'options',
				row_class : 'tcenter tfield',
				head_class : 'tcenter head_text',
				caption : '操作'
			} ],
			'input' : 'table',
			'value' : [ 2 ],
			'multiselect' : true,
			'events' : {
				'createField' : function(o) {
					var c = o['column'], d = o['item']['options']['data'], div;
					if (d == undefined)
						return;
					switch (c['field']) {
					case 'options': {
						o['element'] = new Element('div', {
							'class' : c['row_class']
						});
						div = new Element('div', {
							'class' : 'tfield'
						}).inject(o['element']);
						new Element('a', {
							'text' : '编辑',
							'href' : 'javascript:void(0);',
							'events' : {
								'mousedown' : function(e) {
									e.stop();
								},
								'mouseup' : function(e) {
									e.stop();
								},
								'click' : function(e) {
									e.stop();
									//_this.createOrEdit(d['id']);
								}
							}
						}).inject(div);
					}
						break;
					}
				}
			}
		});
	</script>
</body>
</html>