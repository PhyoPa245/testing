<%@ include file="/taglibs.jsp"%>

<h2>
	<a href="sysadminstart.do"><fmt:message key="sysadmin.maintain" /></a>
	: <fmt:message key="admin.importv1.title" />
</h2>

<p>&nbsp;</p>

<p>
<c:out value="${msgNumCreated}"/>
</p>

<logic:notEmpty name="alreadyExists">
<p>
<fmt:message key="msg.importv1.already.exist"/>:
<table class="alternative-color" width=100% cellspacing="0">
<tr>
	<tr>
		<th><fmt:message key="admin.user.login"/></th>
		<th><fmt:message key="admin.user.first_name"/></th>
		<th><fmt:message key="admin.user.last_name"/></th>
		<th><fmt:message key="admin.user.email"/></th>
	</tr>
<logic:iterate name="alreadyExists" id="user">
	<tr>
		<td><bean:write name="user" property="login"/></td>
		<td><bean:write name="user" property="firstName"/></td>
		<td><bean:write name="user" property="lastName"/></td>
		<td><bean:write name="user" property="email"/></td>
	</tr>
</logic:iterate>
</table>
</logic:notEmpty>
