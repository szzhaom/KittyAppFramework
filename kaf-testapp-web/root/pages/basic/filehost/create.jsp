<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%
	kitty.testapp.inf.ds.file.FileHostHelper.insertOrEditPageProcess(request, response);
%>
<c:script scriptScope="varPrefixL2"></c:script>
<c:div rendered="${error!=null}" value="${error}" styleClass="el_error">
	<script>
		varPrefixL1.editPanel.showButton('save', false);
	</script>
</c:div>
<c:div id="createpf" styleClass='content basic_form'
	rendered="${error==null}">
	<input type='hidden' id='old_file_host_id' value='${data.id}'></input>
	
	<c:div styleClass='row' rendered='true'>
		<label for="file_host_id" class="label">文件主机ID：</label>
		<i:input type="text" id="file_host_id" errorPrompt='请输入1到65535之间的数字' normalPrompt='&nbsp;'
			value="${data.fileHostId}" maxLength='5' minLength='1'
			minValue='1' maxValue='65535' readOnly='${data.id!=null}'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_file_host_id' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="file_host_desp" class="label">文件主机描述：</label>
		<i:input type="text" id="file_host_desp" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.fileHostDesp}" maxLength='50' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_file_host_desp' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="ftp_host" class="label">FTP主机名：</label>
		<i:input type="text" id="ftp_host" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.ftpHost}" maxLength='50' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_ftp_host' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="ftp_port" class="label">FTP端口：</label>
		<i:input type="text" id="ftp_port" errorPrompt='请输入1到1000000000之间的数字' normalPrompt='&nbsp;'
			value="${data.ftpPort}" maxLength='10' minLength='1'
			minValue='1' maxValue='1000000000' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_ftp_port' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="ftp_user" class="label">FTP登录用户：</label>
		<i:input type="text" id="ftp_user" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.ftpUser}" maxLength='50' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_ftp_user' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="ftp_pwd" class="label">FTP登录密码：</label>
		<i:input type="password" id="ftp_pwd" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.ftpPwd}" maxLength='50' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_ftp_pwd' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="web_root" class="label">http访问根目录：</label>
		<i:input type="text" id="web_root" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.webRoot}" maxLength='255' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_web_root' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<script>
		varPrefixL1.editPanel.validate = function() {
			return ValidatorHelper.validate($('createpf'));
		};
	</script>
</c:div>