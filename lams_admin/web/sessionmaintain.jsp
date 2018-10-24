<!DOCTYPE html>

<%@ include file="/taglibs.jsp"%>

<lams:html>
<lams:head>
	<c:set var="title"><fmt:message key="sysadmin.maintain.session"/></c:set>
	<title>${title}</title>

	<lams:css/>
	<link rel="stylesheet" href="<lams:LAMSURL/>admin/css/admin.css" type="text/css" media="screen">
	<link rel="stylesheet" href="<lams:LAMSURL/>css/jquery-ui-smoothness-theme.css" type="text/css" media="screen">
	<script language="JavaScript" type="text/JavaScript" src="<lams:LAMSURL/>/includes/javascript/changeStyle.js"></script>
	<link rel="shortcut icon" href="<lams:LAMSURL/>/favicon.ico" type="image/x-icon" />
</lams:head>
    
<body class="stripes">
	<lams:Page type="admin" title="${title}">
		<p><a href="<lams:LAMSURL/>admin/sysadminstart.do" class="btn btn-default"><fmt:message key="sysadmin.maintain" /></a></p>

		<table class="table table-striped">
			<tr>
				<th><fmt:message key="sysadmin.maintain.session.login" /></th>
				<th><fmt:message key="sysadmin.maintain.session.name" /></th>
				<th><fmt:message key="sysadmin.maintain.session.id" /></th>
				<th></th>
			</tr>
			<c:forEach items="${sessions}" var="ses">
			<tr>
				<td><c:out value="${ses.key}" /></td>
				<td><c:out value="${ses.value[0]} ${ses.value[1]}" /></td>
				<td><c:out value="${ses.value[2]}" /></td>
				<td>
					<a href="<lams:LAMSURL/>admin/sessionmaintain/delete.do?login=${ses.key}" class="btn btn-default">
				<fmt:message key="sysadmin.maintain.session.delete" />
					</a>
				</td>
			</tr>
			</c:forEach>
		</table>
	</lams:Page>

</body>
</lams:html>


