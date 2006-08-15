<%@ include file="/common/taglibs.jsp"%>

<!--  JsJaC Library -->
<script type="text/javascript"
	src="${tool}includes/javascript/jsjac/sha1.js"></script>
<script type="text/javascript"
	src="${tool}includes/javascript/jsjac/xmlextras.js"></script>
<script type="text/javascript"
	src="${tool}includes/javascript/jsjac/JSJaCConnection.js"></script>
<script type="text/javascript"
	src="${tool}includes/javascript/jsjac/JSJaCPacket.js"></script>
<script type="text/javascript"
	src="${tool}includes/javascript/jsjac/JSJaCHttpPollingConnection.js"></script>
<script type="text/javascript"
	src="${tool}includes/javascript/jsjac/JSJaCHttpBindingConnection.js"></script>
<!--  <script language="JavaScript" type="text/javascript" src="Debugger.js"></script> -->

<!--  Chat Config -->
<script type="text/javascript">
	var HTTPBASE = "${tool}JHB/";
	var XMPPDOMAIN = "${XMPPDOMAIN}";
	var USERNAME = "${chatUserDTO.userID}";
	var PASSWORD = "${chatUserDTO.userID}";
	var CONFERENCEROOM = "${CONFERENCEROOM}";
	var NICK = "${chatUserDTO.jabberNickname}";
	var RESOURCE = "lams_chatclient";
	var MODE = "${MODE}";
	var USER_UID = "${chatUserDTO.uid}";	
	var LEARNER_FINISHED = "${chatUserDTO.finishedActivity}";
	var LOCK_ON_FINISHED = "${chatDTO.lockOnFinish}";
	var REFLECT_ON_ACTIVITY = "${chatDTO.reflectOnActivity}";	
</script>

<!--  Chat Client -->
<script src="${tool}pages/learning/chat_app.js" type="text/javascript"></script>

<h1 class="no-tabs-below">
	<c:out value="${chatDTO.title}" escapeXml="false" />
</h1>
<div id="header-no-tabs-learner"></div>
<div id="content-learner">
	<div id="chat_content">
		<div id="chat_pane">
			<form name="sendForm" action="" onSubmit="return sendMsg(this);">
				<div>
					<p>
						<c:out value="${chatDTO.instructions}" escapeXml="false" />
					</p>

					<div id="roster"></div>
					<div id="iResp">
						<fmt:message>message.loading</fmt:message>
					</div>

					<br />

					<h4 style="margin-left: 12px;">
						<fmt:message>label.sendMessageTo</fmt:message>
						<span id="sendToEveryone"><fmt:message>label.everyone</fmt:message>
						</span><span id="sendToUser" style="display: none"></span>
					</h4>


					<div style="margin-left: 12px;">
						<textarea name="msg" onKeyPress="return checkEnter(event);"
							id="msgArea" rows="2" cols="20"></textarea>
							
						<input id="sendButton" class="button" type="submit"
							value='<fmt:message>button.send</fmt:message>' />
					</div>
				</div>
			</form>

			<br/>
			<c:if test="${MODE == 'learner' || MODE == 'author'}">
				<c:choose>
					<c:when test="${!chatUserDTO.finishedActivity}">
							<html:form action="/learning" method="post">
								<c:set var="dispatch" value="finishActivity" />
								<c:set var="buttonLabel" value="button.finish" />
								<c:if test="${chatDTO.reflectOnActivity}">
									<c:set var="dispatch" value="openNotebook" />
									<c:set var="buttonLabel" value="button.reflect" />
								</c:if>
								<html:hidden property="dispatch" value="${dispatch}" />
								<html:hidden property="chatUserUID" value="${chatUserDTO.uid}" />
								<html:submit styleClass="button right-buttons">
									<fmt:message>${buttonLabel}</fmt:message>
								</html:submit>
							</html:form>
							<br/>
					</c:when>

					<c:otherwise>
						<c:if test="${chatDTO.reflectOnActivity }">
							<div>
								<h4 style="margin-left: 12px;">
									${chatDTO.reflectInstructions}
								</h4>
								<p>
									${chatUserDTO.notebookEntry}
								</p>
							</div>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
	</div>
</div>
<div id="footer-learner"></div>
