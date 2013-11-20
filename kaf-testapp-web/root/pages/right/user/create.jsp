<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%
	kitty.testapp.inf.ds.right.UserHelper.insertOrEditPageProcess(request, response);
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
		<label for="user_id" class="label">用户ID：</label>
		<i:input type="text" id="user_id" errorPrompt='请输入1到100000000000000000之间的数字' normalPrompt='&nbsp;'
			value="${data.userId}" maxLength='0' minLength='1'
			minValue='' maxValue='' readOnly='${data.id!=null}'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_user_id' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="user_code" class="label">用户编码：</label>
		<i:input type="text" id="user_code" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.userCode}" maxLength='30' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_user_code' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="user_name" class="label">用户名：</label>
		<i:input type="text" id="user_name" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.userName}" maxLength='255' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_user_name' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="user_pwd" class="label">密码：</label>
		<i:input type="password" id="user_pwd" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.userPwd}" maxLength='32' minLength='32'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_user_pwd' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="gender" class="label">性别：</label>
		<i:input type="combo" id="gender" errorPrompt='请输入1到65535之间的数字' normalPrompt='&nbsp;'
			value="${data.gender.value}" maxLength='3' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="${mysession.globalData.enumValues.genderList}" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_gender' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="birthday" class="label">生日：</label>
		<i:input type="date" id="birthday" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.birthday}" maxLength='0' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="" checkboxes='false'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_birthday' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="role_id_list" class="label">用户所属角色：</label>
		<i:input type="combo" id="role_id_list" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.roleIdList}" maxLength='0' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="${mysession.user.ownerRoleList}" checkboxes='true'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_role_id_list' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<c:div styleClass='row' rendered='true'>
		<label for="owner_role_id_list" class="label">用户可分配的角色：</label>
		<i:input type="combo" id="owner_role_id_list" errorPrompt='输入错误' normalPrompt='&nbsp;'
			value="${data.ownerRoleIdList}" maxLength='0' minLength='1'
			minValue='' maxValue='' readOnly='false'
			params="${mysession.user.ownerRoleList}" checkboxes='true'></i:input>
		<c:div styleClass="prompt">
			<span id='prompt_owner_role_id_list' class='normal'>&nbsp;</span>
		</c:div>
	</c:div>
			
	<script>
		varPrefixL1.editPanel.validate = function() {
			return ValidatorHelper.validate($('createpf'));
		};
	</script>
</c:div>