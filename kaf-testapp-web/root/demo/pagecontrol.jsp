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
	<script type="text/javascript">
		var page = new Element('div');
		var start = new UICalendar({
			'parent' : page,
			'input' : 'startdate',
			'timeselectenabled' : true,
			'startdate' : '1900-01-01',
			'enddate' : '2200-01-01'
		});
		var end = new UICalendar({
			'parent' : page,
			'input' : 'enddate',
			'timeselectenabled' : true,
			'startdate' : '1900-01-01',
			'enddate' : '2200-01-01'
		});
		end.setStartCalendar(start);
		start.setEndCalendar(end);
		var start1 = new UICalendarBox({
			'parent' : page,
			'input' : 'startdate1',
			'calendar' : {
				'timeselectenabled' : true,
				'startdate' : '1900-01-01',
				'enddate' : '2200-01-01'
			}
		});
		var end1 = new UICalendarBox({
			'parent' : page,
			'input' : 'enddate1',
			'calendar' : {
				'timeselectenabled' : true,
				'startdate' : '1900-01-01',
				'enddate' : '2200-01-01'
			}
		});
		end1.setStartCalendar(start1);
		start1.setEndCalendar(end1);

		var mypc = new UIPageControl({
			'parent' : document.body,
			'createParams' : {
				'class' : 'k_pagecontrol'
			},
			'buttonParams' : {
				'class' : 'inline_block  pagebutton'
			},
			'spaceButtonParams' : {
				'class' : 'inline_block  pagebuttonspace'
			},
			'space_class' : 'sp',
			'minheight' : 120,
			'items' : [ {
				'selected' : true,
				'button' : {
					'labelParams' : {
						'html' : '连接页面'
					}
				},
				'url' : '/demo/page_buttons.go'
			}, {
				'button' : {
					'labelParams' : {
						'html' : '本地'
					}
				},
				content : page
			} ]
		});
	</script>
</body>
</html>