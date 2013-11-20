<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<!DOCTYPE html>
<html>
<head>
<title></title>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache,no-store,max-age=0" />
<meta http-equiv="expires" content="1" />
<c:link href="/css/common.css"></c:link>
<c:link href="/css/login.css"></c:link>
<c:script src="/scripts/core/mootools-core.js"></c:script>
<c:script>
	function setpromptstatus(o,s){
		var a,r=['green','red','gray'];
		if(s=='normal'){
			a='gray';r.erase('gray');
		}else if(s=='ok'){
			a='green';r.erase('green');
		}else{
			a='red';r.erase('red');
		}
		o.addRemoveClass(a,r);
	}
	function formsubmit(){
		var u=$('loginname'),p=$('password'),v=$('vercode');
		var up=$('loginname_prompt'),pp=$('password_prompt'),vp=$('vercode_prompt');
		var ret=true;
		if(u.get('value').length==0){
			setpromptstatus(up,'error');
			if(ret==true) u.focus();
			ret=false;
		}else
			setpromptstatus(up,'ok');
		if(p.get('value').length==0){
			setpromptstatus(pp,'error');
			if(ret==true) p.focus();
			ret=false;
		}else
			setpromptstatus(pp,'ok');
		if(v.get('value').length==0){
			setpromptstatus(vp,'error');
			if(ret==true) v.focus();
			ret=false;
		}else
			setpromptstatus(vp,'ok');
		return ret;
	}
	function window_load(){
		if($('loginname').get('value').length>0) $('password').focus();
		else $('loginname').focus();
	}
</c:script>
</head>
<body onload="window_load();">
	<i:MainFrame notRenderMainFrame="true">
		<c:form onsubmit="return formsubmit();">
			<input type='hidden' id='rurl' value='${loginAction.redirectUrl}'></input>
			<c:div id="lg">
				<c:div id="error-title" rendered="${faceserror!=null}">
					<span style="color: red; margin-left: 114px;">${faceserror.message}</span>
					<c:div styleClass='clear'></c:div>
				</c:div>
				<c:div styleClass="line">
					<label>用户名：</label>
					<input id="loginname" name="loginname" type="text"
						class='inputtext' value="${mysession.cookieLoginName}"></input>
					<span id='loginname_prompt' class='gray'>输入登录用户名</span>
					<c:div styleClass='clear'></c:div>
				</c:div>
				<c:div styleClass="line">
					<label>密码：</label>
					<input id="password" name="password" type="password"
						class='inputtext'></input>
					<span id='password_prompt' class='gray'>输入登录密码</span>
					<c:div styleClass='clear'></c:div>
				</c:div>
				<c:div styleClass="line">
					<label>验证码：</label>
					<input id="vercode" name="vercode" type="text" style="width: 60px;"
						class='inputtext'></input>
					<c:img src="/verifyCodeImage" id='verifyimg'></c:img>
					<span id='vercode_prompt' class='gray'>输入图片上的数字</span>
					<c:div styleClass='clear'></c:div>
				</c:div>
				<c:div styleClass="line">
					<label></label>
					<input id="loginname" type="submit" value="登录" class='inputbutton'></input>
					<c:div styleClass='clear'></c:div>
				</c:div>
			</c:div>
		</c:form>
	</i:MainFrame>
	<script type="text/javascript">
		$('verifyimg').addEvent('click', function() {
			this.src = contextPath + '/verifyCodeImage?date=' + new Date();
		});
	</script>
</body>
</html>
