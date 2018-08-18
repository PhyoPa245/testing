<!DOCTYPE html>

<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<%-- param has higher level for request attribute --%>
<c:if test="${not empty param.sessionMapID}">
	<c:set var="sessionMapID" value="${param.sessionMapID}" />
</c:if>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />

<c:set var="mode" value="${sessionMap.mode}" />
<c:set var="toolSessionID" value="${sessionMap.toolSessionID}" />
<c:set var="scratchie" value="${sessionMap.scratchie}" />
<c:set var="isUserLeader" value="${sessionMap.isUserLeader}" />	

<lams:html>
<lams:head>
	<title><fmt:message key="label.learning.title" /></title>
	<%@ include file="/common/header.jsp"%>
	<link rel="stylesheet" type="text/css" href="${lams}css/jquery.countdown.css" />
	<link rel="stylesheet" type="text/css" href="${lams}css/jquery.jgrowl.css" />
	<link rel="stylesheet" type="text/css" href="${lams}css/circle.css" />
	<link rel="stylesheet" type="text/css" href="<html:rewrite page='/includes/css/scratchie-learning.css'/>" />
	<style type="text/css">
		.burning-question-container {
			padding: 0 15px;
		}
		#user-confidence-levels {
			margin-left: 20px;
		}
		.answer-with-confidence-level-portrait {
			display: flex;
   			align-items: center;
		}
		.c100.small {
			font-size: 41px;
			margin-right: 5px;
		}
		.confidence-level-percentage {
			font-size: 13px;
		    margin-top: 43px;
		    text-align: center;
		}
		.lead {
			margin-top: 30px;
		}
		[data-toggle="collapse"].collapsed .if-not-collapsed {
		  display: none;
		}
		[data-toggle="collapse"]:not(.collapsed) .if-collapsed {
		  display: none;
		}
	</style>

	<script type="text/javascript" src="${lams}includes/javascript/jquery-ui.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.plugin.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.countdown.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.blockUI.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.jgrowl.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.form.js"></script>
	<script type="text/javascript">
		function scratchImage(itemUid, answerUid, isCorrect) {
			// first show animation, then put static image
			var imageSuffix = isCorrect ? 'correct' : 'wrong';
	    		$('#image-' + itemUid + '-' + answerUid).load(function(){
	    			var image = $(this).off("load");
	    			// show static image after animation
	    			setTimeout(
	    	    			function(){
	    					image.attr("src", "<html:rewrite page='/includes/images/scratchie-" + imageSuffix + ".png'/>");
	    				}, 
	    				1300
	    			);
	    		}).attr("src", "<html:rewrite page='/includes/images/scratchie-" + imageSuffix + "-animation.gif'/>");
		}

		function scratchItem(itemUid, answerUid){
	        $.ajax({
	            url: '<c:url value="/learning/recordItemScratched.do"/>',
	            data: 'sessionMapID=${sessionMapID}&answerUid=' + answerUid,
	            dataType: 'json',
	            type: 'post',
	            success: function (json) {
		            	if (json == null) {
		            		return false;
		            	}
		            	
		            	scratchImage(itemUid, answerUid, json.answerCorrect);
		            	
		            	if (json.answerCorrect) {
		            		//disable scratching
		            		$("[id^=imageLink-" + itemUid + "]").removeAttr('onclick'); 
		            		$("[id^=imageLink-" + itemUid + "]").css('cursor','default');
		            		$("[id^=image-" + itemUid + "]").not("img[src*='scratchie-correct-animation.gif']").not("img[src*='scratchie-correct.gif']").fadeTo(1300, 0.3);

		            	} else {
		            		var id = '-' + itemUid + '-' + answerUid;
		            		$('#imageLink' + id).removeAttr('onclick');
		            		$('#imageLink' + id).css('cursor','default');
		            	}
	            }
	       	});
		}

		//boolean to indicate whether ok dialog is still ON so that autosave can't be run
		var isWaitingForConfirmation = ${isTimeLimitEnabled && isTimeLimitNotLaunched};

		//time limit feature
		<c:if test="${isTimeLimitEnabled}">
			$(document).ready(function(){
				
				//show timelimit-start-dialog in order to start countdown
				if (${isTimeLimitNotLaunched}) {
					
					//show confirmation dialog
					$.blockUI({ 
						message: $('#timelimit-start-dialog'), 
						css: { width: '325px', height: '120px'}, 
						overlayCSS: { opacity: '.98'} 
					});
						
					//once OK button pressed start countdown
				    $('#timelimit-start-ok').click(function() {
				    	
			        		//store date when user has started activity with time limit
				        $.ajax({
				        		async: true,
				            url: '<c:url value="/learning/launchTimeLimit.do"/>',
				            data: 'sessionMapID=${sessionMapID}',
				            type: 'post'
				       	});
			        	
				       	$.unblockUI();
				       	displayCountdown();
				       	isWaitingForConfirmation = false;
				    });
					
				} else {
					displayCountdown();
				}

			});
			
			function displayCountdown(){
				var countdown = '<div id="countdown"></div>' 
				$.blockUI({
					message: countdown, 
					showOverlay: false,
					focusInput: false,
					css: { 
						top: '40px',
						left: '',
						right: '0%',
				        opacity: '.8', 
				        width: '230px',
				        cursor: 'default',
				        border: 'none'
			        }   
				});
				
				$('#countdown').countdown({
					until: '+${secondsLeft}S',  
					format: 'hMS',
					compact: true,
					onTick: function(periods) {
						//check for 30 seconds
						if ((periods[4] == 0) && (periods[5] == 0) && (periods[6] <= 30)) {
							$('#countdown').css('color', '#FF3333');
						}		
					},
					onExpiry: function(periods) {
				        $.blockUI({ message: '<h1 id="timelimit-expired"><i class="fa fa-refresh fa-spin fa-fw"></i> <fmt:message key="label.time.is.over" /></h1>' }); 
				        
				        setTimeout(
							function() {
				        			finish(true);
				        		}, 
				        		4000
				        ); 
					},
					description: "<div id='countdown-label'><fmt:message key='label.countdown.time.left' /></div>"
				});
			}
		</c:if>

		//autosave feature
		<c:if test="${isUserLeader && (mode != 'teacher')}">
			
			var autosaveInterval = "60000"; // 60 seconds interval
			window.setInterval(
				function(){
					if (isWaitingForConfirmation) return;
					
					//ajax form submit
					$('#burning-questions').ajaxSubmit({
						url: "<c:url value='/learning/autosaveBurningQuestions.do'/>?sessionMapID=${sessionMapID}&date=" + new Date().getTime(),
		                success: function() {
			                	$.jGrowl(
			                		"<i class='fa fa-lg fa-floppy-o'></i> <fmt:message key="label.burning.questions.autosaved" />",
			                		{ life: 2000, closeTemplate: '' }
			                	);
		                }
					});
	        		}, 
	        		autosaveInterval
	        );
		</c:if>

		function finish(isTimelimitExpired) {
			var method = $("#method").val();
			
			var proceed = true;
			//ask for leave confirmation only if time limit is not expired
			if (!isTimelimitExpired) {
				var numberOfAvailableScratches = $("[id^=imageLink-][onclick]").length;
				proceed = (numberOfAvailableScratches > 0) ? confirm("<fmt:message key="label.one.or.more.questions.not.completed"></fmt:message>") : true;	
			}
			
			if (proceed) {
				document.getElementById("finishButton").disabled = true;

		        	var myForm = $('#burning-questions');
		        	myForm.attr("action", '<c:url value="/learning/' + method + '.do?sessionMapID=${sessionMapID}"/>&date=' + new Date().getTime());
		        	myForm.submit();
			}
			
			return false;
		}
    </script>
</lams:head>
<body class="stripes">

	<lams:Page type="learner" title="${scratchie.title}">

		<lams:LeaderDisplay username="${sessionMap.groupLeaderName}" userId="${sessionMap.groupLeaderUserId}"/>

		<div class="panel">
			<c:out value="${scratchie.instructions}" escapeXml="false" />
		</div>

		<c:if test="${not empty sessionMap.submissionDeadline}">
			<lams:Alert id="submissionDeadline" close="true" type="info">
				<fmt:message key="authoring.info.teacher.set.restriction">
					<fmt:param>
						<lams:Date value="${sessionMap.submissionDeadline}" />
					</fmt:param>
				</fmt:message>
			</lams:Alert>
		</c:if>
		
		<c:if test="${mode != 'teacher'}">
			<div id="timelimit-start-dialog"> 
		        <h4>
		        	<fmt:message key='label.are.you.ready' />
		        </h4>
		        <html:button property="ok" styleId="timelimit-start-ok" styleClass="button">
					<fmt:message key='label.ok' />
				</html:button>
			</div>
		</c:if>

		<%@ include file="/common/messages.jsp"%>

		<div id="questionListArea">
			<%@ include file="questionlist.jsp"%>
		</div>

		<div id="footer"></div>
	</lams:Page>
</body>
</lams:html>
