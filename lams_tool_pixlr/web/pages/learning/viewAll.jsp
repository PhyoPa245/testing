<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
            "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<lams:html>

	<lams:head>  
	
		<title>
			<fmt:message>learner.title.viewAll</fmt:message>
		</title>
		
		<link href="${tool}pages/learning/pixlr_style.css" rel="stylesheet" type="text/css">
		<lams:css/>
		
	</lams:head>
	
	<script type="text/javascript">
		var popupWindow = null;
		function openPopup(url, height, width)
		{	
			if(popupWindow && popupWindow.open && !popupWindow.closed){
				popupWindow.close();
			}
			popupWindow = window.open(url,'popupWindow','resizable,width=' +width+ ',height=' +height+ ',scrollbars');
		}
	         function submitForm(methodName){
	                var f = document.getElementById('messageForm');
	                f.submit();
	        }
	</script>
	
	<body class="stripes">
	
			<div id="content">
			
			<h1>
				${pixlrDTO.title}
			</h1>
		
			<html:form action="/learning" method="post" styleId="messageForm">
				<html:hidden property="toolSessionID" styleId="toolSessionID"/>
				<html:hidden property="mode" value="${mode}" />
				<html:hidden property="dispatch" styleId = "dispatch" value="finishActivity" />
				<html:hidden property="redoQuestion" value="true" />
					<c:choose>
						<c:when test="${!empty learnerDTOs}">
							<br />
							<hr />
							<br />
							<c:forEach var="learner" items="${learnerDTOs}">
								<table >
								<tr>
									<td width="30%">
										<a href="javascript:openPopup('${pixlrImageFolderURL}/${learner.imageFileName}', ${learner.imageHeight}, ${learner.imageWidth})">
										${learner.firstName} ${learner.lastName}
										</a>
									</td>
									<td>
										<c:choose>
											<c:when test="${!learner.imageHidden}">
												<img src="${pixlrImageFolderURL}/${learner.imageFileName}" 
													height="${learner.imageThumbnailHeight}" 
													width="${learner.imageThumbnailWidth}"
													title='<fmt:message key="tooltip.openfullsize" />'
													onclick="openPopup('${pixlrImageFolderURL}/${learner.imageFileName}', ${learner.imageHeight}, ${learner.imageWidth})"
												/>
											</c:when>
											<c:otherwise>
												<i><fmt:message key="message.imageHidden" /></i>
											</c:otherwise>
										</c:choose>
									</td>
									
								</tr>
								</table>
								
								<br />
								<hr />
							</c:forEach>
							<br />
						</c:when>
						<c:otherwise>
							<p>
							<i><fmt:message key="message.imageListEmpty" /></i>
							</p>
						</c:otherwise>
					</c:choose>
					
				<br />
				
				
				<html:submit styleClass="button" onclick="javascript:document.getElementById('dispatch').value = 'viewAllImages';">
					<fmt:message key="button.refresh" />
				</html:submit>
				&nbsp;
				<c:if test="${!pixlrDTO.lockOnFinish}">
					<html:submit styleClass="button" onclick="javascript:document.getElementById('dispatch').value = 'unspecified';">
						<fmt:message key="button.redo" />
					</html:submit>
					&nbsp;
				</c:if>
				<c:choose>
					<c:when test="${pixlrDTO.reflectOnActivity}">
						<html:submit styleClass="button" onclick="javascript:document.getElementById('dispatch').value = 'openNotebook';">
							<fmt:message key="button.continue" />
						</html:submit>
					</c:when>
					<c:otherwise>
						<html:link href="javascript:;" styleClass="button" styleId="finishButton" onclick="submitForm('finished');">
							<span class="nextActivity"><fmt:message key="button.finish" /></span>
						</html:link>
					</c:otherwise>
				</c:choose>
				
			</html:form>
				
			</div>
			<div class="footer"></div>
	</body>
</lams:html>
