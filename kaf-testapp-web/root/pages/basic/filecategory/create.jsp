<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%
	kitty.testapp.inf.ds.file.FileCategoryHelper.insertOrEditPageProcess(request, response);
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
		<label for="file_category_id" class="label">文件分类ID：</label>
		<i:input type="text" id="file_category_id" add='${data.id==null}' errorPrompt='请输入1到65535之间的数字' normalPrompt='&nbsp;'
			value="${data.fileCategoryId}" maxLength='5' minLength='1'
			minValue='1' maxValue='65535' readOnly='${data.id!=null}'
			params="" checkboxes='false' multiSelect='false' url='' textFieldName=''></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_file_category_id' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="file_category_desp" class="label">文件分类描述：</label>
		<i:input type="text" id="file_category_desp" add='${data.id==null}' errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.fileCategoryDesp}" maxLength='50' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false' multiSelect='false' url='' textFieldName=''></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_file_category_desp' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="cur_file_host_id" class="label">当前的文件主机ID：</label>
		<i:input type="combo" id="cur_file_host_id" add='${data.id==null}' errorPrompt='请输入1到65535之间的数字' normalPrompt='&nbsp;'
			value="${data.curFileHostId}" maxLength='5' minLength='1'
			minValue='1' maxValue='65535' readOnly='false'
			params="${mysession.globalData.localCache.fileHostList}" checkboxes='false' multiSelect='false' url='' textFieldName=''></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_cur_file_host_id' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<script>
		varPrefixL1.editPanel.validate = function() {
			return ValidatorHelper.validate($('createpf'));
		};
	</script>
</c:div>