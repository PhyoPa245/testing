<!DOCTYPE html>
	

<%@ include file="/common/taglibs.jsp"%>

<lams:html>
	<c:set var="lams">
		<lams:LAMSURL />
	</c:set>
	<c:set var="tool">
		<lams:WebAppURL />
	</c:set>
	
	<lams:head>
		<title>
			<fmt:message key="activity.title" />
		</title>
		<lams:css/>
		
		<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/tabcontroller.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/bootstrap.min.js"></script>
	
		<script type="text/javascript">
	
		</script>
	</lams:head>
	
	<body class="stripes">
		<c:set var="tool">
			<lams:WebAppURL />
		</c:set>
		
		<c:set var="appointedScribe">
			<c:out value="${scribeSessionDTO.appointedScribe}" escapeXml="true" />
		</c:set>
		
		<script type="text/javascript" src='<lams:LAMSURL/>includes/javascript/jquery.js'></script>
		<script type="text/javascript">
			//init the connection with server using server URL but with different protocol
			var scribeWebsocketInitTime = Date.now(), 
				scribeWebsocket = new WebSocket('${tool}'.replace('http', 'ws')
							+ 'learningWebsocket?toolSessionID=' + ${scribeSessionDTO.sessionID}),
				scribeWebsocketPingTimeout = null,
				scribeWebsocketPingFunc = null,
				agreementPercentageLabel = '<fmt:message key="message.voteStatistics" />',
				reportSubmitted = ${scribeSessionDTO.reportSubmitted};
				
			scribeWebsocket.onclose = function(e){
				if (e.code === 1006 &&
					Date.now() - scribeWebsocketInitTime > 1000) {
					location.reload();
				}
			};
				
			scribeWebsocketPingFunc = function(skipPing){
				if (scribeWebsocket.readyState == scribeWebsocket.CLOSING 
						|| scribeWebsocket.readyState == scribeWebsocket.CLOSED){
					if (Date.now() - scribeWebsocketInitTime < 1000) {
						return;
					}
					location.reload();
				}
				
				// check and ping every 3 minutes
				scribeWebsocketPingTimeout = setTimeout(scribeWebsocketPingFunc, 3*60*1000);
				// initial set up does not send ping
				if (!skipPing) {
					scribeWebsocket.send("ping");
				}
			};
			
			// set up timer for the first time
			scribeWebsocketPingFunc(true);
			
			// run when the server pushes new reports and vote statistics
			scribeWebsocket.onmessage = function(e) {
				// create JSON object
				var input = JSON.parse(e.data),
					agreeButton = $('#agreeButton').parent();
				
				// if the scribe or monitor force completes the activity, reload to display report page
				if (input.close) {
					window.location.href = '${tool}learning.do?toolSessionID=${scribeSessionDTO.sessionID}&mode=${MODE}';
					return;
				}
		
				// reset ping timer
				clearTimeout(scribeWebsocketPingTimeout);
				scribeWebsocketPingFunc(true);
				
				// only changed reports will be sent
				if (input.reports) {
					reportSubmitted = true;
					$.each(input.reports, function() {
						$('#reportText-' + this.uid).html(this.text);
					});
				}
				
				// can the user vote
				if (!reportSubmitted || input.approved) {
					agreeButton.hide();
				} else {
					agreeButton.show();
				}
				
				// update vote statistics
				var label = agreementPercentageLabel.replace('{0}', input.numberOfVotes)
													.replace('{1}', input.numberOfLearners);
				$('#agreementPercentageLabel').text(label);
				$('#agreementPercentage').text('(' + input.votePercentage + '%)');
				$('#agreementPercentageBar').attr('aria-valuenow', input.votePercentage)
											.css('width',input.votePercentage + '%');
			};
			
			function submitApproval() {
				var agreeButton = $('#agreeButton').parent(),
					data = {
						type : 'vote'	
					};
				scribeWebsocket.send(JSON.stringify(data));
				
				agreeButton.hide();
			}
		</script>
		
		<lams:Page type="learner" title="${scribeDTO.title}">
		
			<div class="panel">
				<c:out value="${scribeDTO.instructions}" escapeXml="false" />
			</div>
		
			<%@include file="/pages/parts/voteDisplay.jsp"%>
		
			<h4>
				<abbr class="pull-right hidden-xs"
					title="<fmt:message key="message.learnerInstructions2" ><fmt:param value="${appointedScribe}"/></fmt:message>&nbsp;<fmt:message key="message.learnerInstructions3" />"><i
					class="fa fa-question-circle text-info"></i></abbr>
				<fmt:message key="heading.report" />
			</h4>
			<p class="help-block" style="font-size: 12px"><fmt:message key="activity.title"/>: ${appointedScribe}</p>
		
				<c:forEach var="reportDTO" items="${scribeSessionDTO.reportDTOs}">
		
					<div class="row">
						<div class="col-12">
							<div class="panel panel-default">
								<div class="panel-heading panel-title">
									<c:out value="${reportDTO.headingDTO.headingText}" escapeXml="false" />
								</div>
								<div class="panel-body">
									<abbr class="pull-right hidden-xs" title="<fmt:message key="label.scribe.posted" />"><i
										class="fa fa-xs fa-question-circle text-info"></i></abbr>
		
									<c:set var="entry">
										<lams:out value="${reportDTO.entryText}" escapeHtml="true" />
									</c:set>
									<span id="reportText-${reportDTO.uid}"><c:out value="${entry}" escapeXml="false" /></span>
								</div>
							</div>
						</div>
					</div>
		
				</c:forEach>
		
				<div class="row">
					<div class="col-12" id="agreeButton">
						<button class="btn btn-success pull-left" onClick="javascript:submitApproval()">
							<fmt:message key="button.agree" />
						</button>
					</div>
				</div>
		
		
			<div id="footer"></div>
		
		</lams:Page>
			
	</body>
</lams:html>

