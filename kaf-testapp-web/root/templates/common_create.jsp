<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://www.kitty.cn/jsp/core-faces"%>
<%@taglib prefix="i" uri="http://www.kitty.cn/jsp/inf-faces"%>
<%
	${template_init}
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
	${template_fields}
	<script>
		varPrefixL1.editPanel.validate = function() {
			return ValidatorHelper.validate($('createpf'));
		};
	</script>
</c:div>