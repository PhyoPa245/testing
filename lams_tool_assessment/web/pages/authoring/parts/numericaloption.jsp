<input type="hidden" name="optionSequenceId${status.index}" value="${option.sequenceId}">
<input type="hidden" name="optionUid${status.index}" value="${option.uid}">

<table class="table-row-spacing" style="margin-left: -4px;">
	<tr>
		<td style="width: 100px;" class="greyed-out-label">
			<fmt:message key="label.authoring.basic.option.answer"/>&nbsp;${status.index+1}
		</td>
		<td>
			<div class="form-inline">    	
				<input type="text" name="optionFloat${status.index}" value="${option.optionFloat}" 
						id="optionFloat${status.index}" class="number form-control short-input-text input-xs" 
						title="<fmt:message key='label.authoring.choice.enter.float'/>">
			</div>
		</td>
	</tr>

	<tr class="settings-on-hover-hidden">
		<td class="greyed-out-label">
			<fmt:message key="label.authoring.basic.option.accepted.error"></fmt:message>
		</td>
		<td>
			<div class="form-inline">	
				<input type="text" name="optionAcceptedError${status.index}" value="${option.acceptedError}"
						id="optionAcceptedError${status.index}" class="number form-control short-input-text input-xs" 
						title="<fmt:message key='label.authoring.choice.enter.float'/>">
			</div>
		</td>
	</tr>
</table>

<div class="settings-on-hover-hidden">
	<%@ include file="gradeselector.jsp"%>
	
	<div class="voffset5-bottom">
	   	<c:set var="FEEDBACK_LABEL"><fmt:message key="label.authoring.basic.option.feedback"/></c:set>
	   	<lams:CKEditor id="optionFeedback${status.index}" value="${option.feedback}" 
	     	placeholder="${FEEDBACK_LABEL}" contentFolderID="${contentFolderID}" height="40px"/>
	</div>
</div>
