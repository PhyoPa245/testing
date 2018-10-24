<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>


<%@ taglib uri="tags-lams" prefix="lams" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %> 

<!DOCTYPE html>
<lams:html>
<lams:head>
	<base href="<lams:LAMSURL/>"/>
	<title><fmt:message key="heading.general.error" /></title>
	<lams:css/>
	<script language="JavaScript" type="text/JavaScript" src="<lams:WebAppURL />includes/javascript/changeStyle.js"></script>
	<script language="JavaScript" type="text/javascript" src="includes/javascript/getSysInfo.js"></script>
	<script language="JavaScript" type="text/javascript" src="includes/javascript/openUrls.js"></script>
	<link rel="icon" href="<lams:LAMSURL/>/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="<lams:LAMSURL/>/favicon.ico" type="image/x-icon" />
</lams:head>
    
<body class="stripes">
<lams:Page type="learner" title="">
<table width="90%" border="0" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
	<tr> 
		<td> 
			<div align="center" class="heading">
				<p><fmt:message key="error.general.1"/></p>
				<p class="body"><fmt:message key="error.general.2"/></p>
				<p class="body"><fmt:message key="error.general.3"/></p>
			</div>
		</td>
	</tr>
</table>
<div id="footer"></div>
</lams:Page>
</body>
</lams:html>



