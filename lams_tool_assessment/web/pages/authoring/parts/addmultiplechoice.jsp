<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="questionType" value="1"	scope="request" />

<lams:html>
<lams:head>
	<%@ include file="/common/authoringQuestionHeader.jsp"%>
    <script>
		$(document).ready(function(){
		    $("#assessmentQuestionForm").validate({
		    	ignore: 'hidden',
	    		rules: {
	    			title: "required",
	    			defaultGrade: {
	    			    required: true,
	    			    digits: true
	    			},
	    			penaltyFactor: {
	    			    required: true,
	    			    number: true
	    			},
	    			hasOptionFilled: {
	    				required: function(element) {
			    			prepareOptionEditorsForAjaxSubmit();
	    		        	return $("textarea[name^=optionString]:filled").length < 1;
		    		    }			    		    
    			    },
    			    hasOneHundredGrade: {
	    				required: function(element) {
	    					var hasOneHundredGrade = false;
	    					$("input[name^='optionGrade']").each(function() {
	    						hasOneHundredGrade = hasOneHundredGrade || (eval(this.value) == 1);
	    					});
    			    		return !hasOneHundredGrade && !eval($("#multipleAnswersAllowed").val());
		    		    }		    		    
    			    }
	    		},
	    		messages: {
	    			title: "<fmt:message key='label.authoring.choice.field.required'/>",
	    			defaultGrade: {
	    				required: "<fmt:message key='label.authoring.choice.field.required'/>",
	    				digits: "<fmt:message key='label.authoring.choice.enter.integer'/>"
	    			},
	    			penaltyFactor: {
	    				required: "<fmt:message key='label.authoring.choice.field.required'/>",
	    				number: "<fmt:message key='label.authoring.choice.enter.float'/>"
	    			},
	    			hasOptionFilled: {
	    				required: "<fmt:message key='label.authoring.numerical.error.answer'/>"
	    			},
	    			hasOneHundredGrade: {
	    				required: "<fmt:message key='error.form.validation.hundred.score'/>"
	    			}
	    		},
	    	    invalidHandler: formInvalidHandler,
	    		debug: true,
	    		errorClass: "alert alert-danger",
   			    submitHandler: function(form) {
   			    	prepareOptionEditorsForAjaxSubmit();
	    			$("#optionList").val($("#optionForm").serialize(true));
	    			$("#question").val(CKEDITOR.instances.question.getData());
	    			$("#generalFeedback").val(CKEDITOR.instances.generalFeedback.getData());
	    			$("#feedbackOnCorrect").val(CKEDITOR.instances.feedbackOnCorrect.getData());
	    			$("#feedbackOnPartiallyCorrect").val(CKEDITOR.instances.feedbackOnPartiallyCorrect.getData());
	    			$("#feedbackOnIncorrect").val(CKEDITOR.instances.feedbackOnIncorrect.getData());
		    			
	    	    	var options = { 
	    	    		target:  parent.jQuery('#questionListArea'), 
	    		   		success: afterRatingSubmit  // post-submit callback
	    		    }; 				
		    		    				
	    			$('#assessmentQuestionForm').ajaxSubmit(options);
	    		}
		  	});
		    	
			$("#multipleAnswersAllowed").on('change', function() {
				$("#incorrect-answer-nullifies-mark-area").toggle(eval($(this).val()));
			}).trigger("change");

			// Only one of prefixAnswersWithLetters or shuffle at a time
 			$("#prefixAnswersWithLetters").on('change', function() {
				if ( this.checked ) {
					if ($("#shuffle").prop('checked')) {
						$("#shuffle").prop('checked', false);
					}
					$("#shuffle").prop('disabled', true);
					$("#shuffleText").addClass('text-muted');
				} else {
					$("#shuffle").prop('disabled', false);
					$("#shuffleText").removeClass('text-muted');
				}
			}).trigger("change");

			$("#shuffle").on('change', function() {
				if ( this.checked ) {
					if ($("#prefixAnswersWithLetters").prop('checked')) {
						$("#prefixAnswersWithLetters").prop('checked', false);
					}
					$("#prefixAnswersWithLetters").prop('disabled', true);
					$("#prefixAnswersWithLettersText").addClass('text-muted');
				} else {
					$("#prefixAnswersWithLetters").prop('disabled', false);
					$("#prefixAnswersWithLettersText").removeClass('text-muted');
				}
			}).trigger("change");
		});
	</script>
</lams:head>
<body>
	<div class="panel-default add-file">
		<div class="panel-heading panel-title">
			<fmt:message key="label.authoring.basic.type.multiple.choice" />
		</div>
			
		<div class="panel-body">
			
			<form:form action="saveOrUpdateQuestion.do" method="post" modelAttribute="assessmentQuestionForm" id="assessmentQuestionForm">
				<c:set var="sessionMap" value="${sessionScope[assessmentQuestionForm.sessionMapID]}" />
				<c:set var="isAuthoringRestricted" value="${sessionMap.isAuthoringRestricted}" />
				<form:hidden path="sessionMapID" />
				<input type="hidden" name="questionType" id="questionType" value="${questionType}" />
				<input type="hidden" name="optionList" id="optionList" />
				<form:hidden path="sequenceId" />
				<form:hidden path="contentFolderID" id="contentFolderID"/>
				
				<button type="button" id="question-settings-link" class="btn btn-default btn-sm">
					<fmt:message key="label.settings" />
				</button>

				<div class="question-tab">
					<lams:errors/>
					 <div class="error">
				    	<lams:Alert id="errorMessages" type="danger" close="false" >
							<span></span>
						</lams:Alert>	
				    </div>
				
					<div id="title-container" class="form-group">
						<c:set var="TITLE_LABEL"><fmt:message key="label.enter.question.title"/> </c:set>
					    <form:input path="title" id="title" cssClass="borderless-text-input" tabindex="1" maxlength="255"  
					    	placeholder="${TITLE_LABEL}"/>
					</div>
				
					<div class="form-group">
						<c:set var="QUESTION_DESCRIPTION_LABEL"><fmt:message key="label.enter.question.description"/></c:set>
						<lams:CKEditor id="question" value="${assessmentQuestionForm.question}" contentFolderID="${assessmentQuestionForm.contentFolderID}" 
							placeholder="${QUESTION_DESCRIPTION_LABEL}"	 />
					</div>
				
					<input type="text" name="hasOptionFilled" id="hasOptionFilled" class="fake-validation-input">
					<input type="text" name="hasOneHundredGrade" id="hasOneHundredGrade" class="fake-validation-input">
				</div>

				<div class="settings-tab">
					 <div class="error">
				    	<lams:Alert id="errorMessages" type="danger" close="false" >
							<span></span>
						</lams:Alert>	
				    </div>
				
					<div class="checkbox">
						<label for="answer-required">
							<form:checkbox path="answerRequired" id="answer-required"/>
							<fmt:message key="label.authoring.answer.required" />
						</label>
					</div>
					
					<c:if test="${!isAuthoringRestricted}">
						<div class="form-group row">
						    <label for="defaultGrade" class="col-sm-3">
						    	<fmt:message key="label.authoring.basic.default.question.grade" />
						    	<i class="fa fa-xs fa-asterisk text-danger" style="vertical-align: super;" title="<fmt:message key="label.required.field"/>" alt="<fmt:message key="label.required.field"/>"></i>
						    </label>
						    
						    <div class="col-sm-9">
						    	<form:input path="defaultGrade" cssClass="form-control short-input-text input-sm"/>
						    </div>
						</div>
					</c:if>
					    					
					<div class="form-group row">
					    <label for="penaltyFactor" class="col-sm-3"> 
					    	<fmt:message key="label.authoring.basic.penalty.factor" />
							<i class="fa fa-xs fa-asterisk text-danger" title="<fmt:message key="label.required.field"/>" alt="<fmt:message key="label.required.field"/>"></i>
					    </label>
					    
					    <div class="col-sm-9">
					    	<form:input path="penaltyFactor" cssClass="form-control short-input-text input-sm"/>
					    </div>
					</div>
					
					<div class="form-group row form-inline">
						<label for="multipleAnswersAllowed" class="col-sm-3">
							<fmt:message key="label.authoring.choice.one.multiple.answers" />
						</label>
						
						<div class="col-sm-9">
							<form:select path="multipleAnswersAllowed" id="multipleAnswersAllowed" cssClass="form-control input-sm">
								<form:option value="false"><fmt:message key="label.authoring.choice.one.answer" /></form:option>
								<form:option value="true"><fmt:message key="label.authoring.choice.multiple.answers" /></form:option>
							</form:select>
						</div>
					</div>
					
					<div class="checkbox" id="incorrect-answer-nullifies-mark-area">
						<label for="incorrectAnswerNullifiesMark">
							<form:checkbox path="incorrectAnswerNullifiesMark" id="incorrectAnswerNullifiesMark"/>
							<fmt:message key="label.incorrect.answer.nullifies.mark" />
						</label>
					</div>		
		
					<div class="checkbox">
						<label for="shuffle">
							<form:checkbox path="shuffle" id="shuffle"/>
							<span id="shuffleText"><fmt:message key="label.authoring.basic.shuffle.the.choices" /></span>
						</label>
					</div>
					
					<div class="checkbox" id="prefixAnswersWithLettersDiv">
						<label for="prefixAnswersWithLetters">
							<form:checkbox path="prefixAnswersWithLetters" id="prefixAnswersWithLetters"/>
							<span id="prefixAnswersWithLettersText"><fmt:message key="label.prefix.sequential.letters.for.each.answer" /></span>
						</label>
					</div>
				
					<!-- Overall feedback -->
					<!-- <h5 style="margin-top: 20px;">Feedbacks</h5> -->
	
					<div class="form-group">
						<c:set var="GENERAL_FEEDBACK_LABEL">General Feedback</c:set>
						<lams:CKEditor id="generalFeedback" value="${assessmentQuestionForm.generalFeedback}" 
							placeholder="${GENERAL_FEEDBACK_LABEL}" contentFolderID="${assessmentQuestionForm.contentFolderID}" />
					</div>
					
					<div class="form-group">
						<c:set var="FEEDBACK_ON_CORRECT_LABEL">Feedback on any correct response</c:set>
						<lams:CKEditor id="feedbackOnCorrect" value="${assessmentQuestionForm.feedbackOnCorrect}" 
							placeholder="${FEEDBACK_ON_CORRECT_LABEL}" contentFolderID="${assessmentQuestionForm.contentFolderID}"/>
					</div>
					
					<div class="form-group">
						<c:set var="FEEDBACK_ON_PARTICALLY_CORRECT_LABEL">Feedback on any partially correct response</c:set>
						<lams:CKEditor id="feedbackOnPartiallyCorrect" value="${assessmentQuestionForm.feedbackOnPartiallyCorrect}" 
							placeholder="${FEEDBACK_ON_PARTICALLY_CORRECT_LABEL}" contentFolderID="${assessmentQuestionForm.contentFolderID}"/>
					</div>
							
					<div class="form-group">
						<c:set var="FEEDBACK_ON_INCORRECT_LABEL">Feedback on any incorrect response</c:set>
						<lams:CKEditor id="feedbackOnIncorrect" value="${assessmentQuestionForm.feedbackOnIncorrect}" 
							placeholder="${FEEDBACK_ON_INCORRECT_LABEL}" contentFolderID="${assessmentQuestionForm.contentFolderID}"/>
					</div>
				</div><!-- settings tab ends -->
			</form:form>
			
			<!-- Options -->
			<div class="question-tab">	
				<form id="optionForm" name="optionForm" class="form-group">
					<%@ include file="optionlist.jsp"%>
					
					<a href="#nogo" onclick="javascript:addOption();" class="btn btn-xs btn-default button-add-item pull-right">
						<fmt:message key="label.authoring.choice.add.option" /> 
					</a>
				</form>
			</div>		

		</div>	
	</div>	
	
	<footer class="footer fixed-bottom">
		<div class="panel-heading">
        	<div class="pull-right">
			    <a href="#nogo" onclick="javascript:self.parent.tb_remove();" class="btn btn-sm btn-default loffset5">
					<fmt:message key="label.cancel" />
				</a>
				<a href="#nogo" onclick="javascript:$('#assessmentQuestionForm').submit();" class="btn btn-sm btn-default button-add-item">
					<fmt:message key="label.authoring.save.button" />
				</a>
			</div>	
      	</div>
    </footer>	
</body>
</lams:html>
