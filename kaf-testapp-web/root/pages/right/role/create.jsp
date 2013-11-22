<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%
	kitty.testapp.inf.ds.right.RoleHelper.insertOrEditPageProcess(request, response);
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
	
	<c:div styleClass='row' rendered='${data.id!=null}'>
		<label for="role_id" class="label">角色ID：</label>
		<i:input type="text" id="role_id" add='${data.id==null}' errorPrompt='请输入1到1000000000之间的数字' normalPrompt='&nbsp;'
			value="${data.roleId}" maxLength='10' minLength='1'
			minValue='1' maxValue='1000000000' readOnly='${data.id!=null}'
			params="" checkboxes='false' multiSelect='false' url='' textFieldName=''></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_role_id' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="role_desp" class="label">角色描述：</label>
		<i:input type="text" id="role_desp" add='${data.id==null}' errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.roleDesp}" maxLength='50' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false' multiSelect='false' url='' textFieldName=''></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_role_desp' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="func_id_list" class="label">角色功能列表：</label>
		<i:input type="tree" id="func_id_list" add='${data.id==null}' errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.funcIdList}" maxLength='0' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="${mysession.user.userFuncTreeNode}" checkboxes='true' multiSelect='true' url='' textFieldName=''></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_func_id_list' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<script>
		varPrefixL1.editPanel.validate = function() {
			return ValidatorHelper.validate($('createpf'));
		};
	</script>
</c:div>