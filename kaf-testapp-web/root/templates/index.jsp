<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<!DOCTYPE html>
<html>
<head>
<title>KAF测试应用</title>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache,no-store,max-age=0" />
<meta http-equiv="expires" content="1" />
<c:link href="/css/index.css"></c:link>
<c:link href="/css/kaf-basic.css"></c:link>
<c:script src="/scripts/core/mootools-core-1.4.5.js"
	outputContextPath="true"></c:script>
<c:script src="/scripts/core/kaf-core.js"></c:script>
<c:script src="/scripts/index.js"></c:script>
</head>
<body>
	<div> 
		<div id='header'>
			<ol id='headermenu'>
				<li class='item'><span class='cts'>KAF</span></li>
				<li class='item'><c:a styleClass='cta' href='/index.go'>管理平台</c:a></li>
				<div class='clear'></div>
			</ol>
			<ol id='header_ui'>
				<li class='item'><span class='user_info'>${mysession.user.userName}</span></li>
				<li class='item' id='webmenu'><c:a styleClass='cta'
						href='/loginout.go' value='退出登录'></c:a></li>
			</ol>
			<div class='clear'></div>
		</div>
		<div id='mainframe'></div>
		<div id='footer'>
			<span>© ?-2013</span>
		</div>
		<script type='text/javascript'>
			var jsCssFileMgr = new JsCssFileManager();
			var mainPageControl = new UIPageControl({
				'parent' : 'mainframe',
				'isleftright' : true,
				'jsCssFileMgr' : jsCssFileMgr,
				'createParams' : {
					'class' : 'main_frame'
				},
				'buttonParams' : {
					'createParams' : {
						'class' : 'pagebutton'
					}
				},
				'buttonPanelParams' : {
					'class' : 'leftpanel'
				},
				'contentPanelParams' : {
					'class' : 'maincontent'
				},
				'pageParams' : {
					'class' : 'mainpage'
				},
				'spaceButtonParams' : {
					'class' : 'pagebuttonspace'
				},
				'items' : ${template.menu_items}
			});
		</script>
	</div>
</body>
</html>
