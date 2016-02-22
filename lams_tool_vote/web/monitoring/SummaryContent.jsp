<%@ include file="/common/taglibs.jsp"%>
<c:set scope="request" var="lams"><lams:LAMSURL/></c:set>
<c:set scope="request" var="tool"><lams:WebAppURL/></c:set>

<link type="text/css" href="${lams}/css/jquery-ui-smoothness-theme.css" rel="stylesheet">
<link type="text/css" href="${lams}/css/jquery-ui.timepicker.css" rel="stylesheet">
<link type="text/css" href="${lams}/css/chart.css" rel="stylesheet">

<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery-ui.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery-ui.timepicker.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery.blockUI.js"></script>  
<script type="text/javascript" src="${lams}includes/javascript/d3.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/chart.js"></script>

<script type="text/javascript">
	$(window).load(function(){
		<c:forEach var="currentDto" items="${voteGeneralMonitoringDTO.sessionDTOs}">
			<c:set var="chartURL" value="${tool}chartGenerator.do?currentSessionId=${currentDto.sessionId}&toolContentID=${toolContentID}" />
			drawChart('pie', 'chartDiv${currentDto.sessionId}', '${chartURL}');
		</c:forEach>
	});

	//pass settings to monitorToolSummaryAdvanced.js
	var submissionDeadlineSettings = {
		lams: '${lams}',
		submissionDeadline: '${submissionDeadline}',
		setSubmissionDeadlineUrl: '<c:url value="/monitoring.do?dispatch=setSubmissionDeadline"/>',
		toolContentID: '${toolContentID}',
		messageNotification: '<fmt:message key="monitor.summary.notification" />',
		messageRestrictionSet: '<fmt:message key="monitor.summary.date.restriction.set" />',
		messageRestrictionRemoved: '<fmt:message key="monitor.summary.date.restriction.removed" />'
	};
</script>
<script type="text/javascript" src="${lams}/includes/javascript/monitorToolSummaryAdvanced.js" ></script>

<h1>
	<c:out value="${voteGeneralMonitoringDTO.activityTitle}" escapeXml="true"/>
</h1>

<div class="instructions small-space-top">
	<c:out value="${voteGeneralMonitoringDTO.activityInstructions}" escapeXml="false"/>
</div>
<br/>

<c:if test="${useSelectLeaderToolOuput}">
	<div class="info space-bottom">
		<fmt:message key="label.info.use.select.leader.outputs" />
	</div>
</c:if>

<jsp:include page="/monitoring/AllSessionsSummary.jsp" />
		
<%@include file="AdvanceOptions.jsp"%>

<%@include file="daterestriction.jsp"%>
