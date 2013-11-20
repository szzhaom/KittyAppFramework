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
<style type="text/css">
.abcd {
	display: inline-block;
}

.table {
	display: table;
	width: 100%;
}

.table_row {
	display: table-row;
}

.table_cell {
	display: table-cell;
	margin: 2px;
	border: 1px solid #ccc;
	text-align: center;
	vertical-align: middle;
	height: 100px;
}
</style>
</head>
<body>
	<c:div id='abcd'></c:div>
	<c:div id='abcd1'></c:div>
	<c:div id='aaa'></c:div>
	<c:div id='bbb'></c:div>
	<c:div id='button'>button</c:div>
	<c:div id='msg' styleClass='table'>
		<c:div styleClass='table_row'>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
		</c:div>
		<c:div styleClass='table_row'>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
			<c:div styleClass='table_cell'>中国人民共和国</c:div>
		</c:div>
	</c:div>
	<script type="text/javascript">
		new UICalendarBox({
			'panel' : 'bbb',
			'input' : 'input',
			'calendar' : {
				'timeselectenabled' : true,
				'startdate' : '1900-01-01',
				'enddate' : '2200-01-01'
			}
		});
		new UIChosenBox(
				{
					'panel' : 'aaa',
					'input' : 'input1',
					'list' : {
						'emptyParams' : {
							'text' : '无可选项'
						},
						'requestParams' : {
							'textfield' : 'company_category_desp',
							'url' : '/webtrade?executor=webtrade&group=basic&cmd=queryCompanyCategory&first_index=-1&max_results=12'
						},
						'value' : '1,2,3',
						'multiselect' : true
					}
				});
		new UITitleWindow({
			'content' : 'msg',
			'bounds' : {},
			'mask' : undefined,
			'titleParams' : {
				'text' : '新建窗口'
			}
		}).show();
	</script>
</body>
</html>