<c:forEach var="item" items="${sessionMap.itemList}" varStatus="status">
	<div class="lead">
        <a name="${item.title}" style="text-decoration:none;color:black"><c:out value="${item.title}" escapeXml="true" /></a>
	</div>
	<div class="panel-body-sm">
		<c:out value="${item.description}" escapeXml="false" />
	</div>

	<table id="scratches" class="table table-hover">
		<c:forEach var="answer" items="${item.answers}" varStatus="status">
			<tr id="tr${answer.uid}">
				<td style="width: 40px;">
					<c:choose>
						<c:when test="${answer.scratched && answer.correct}">
							<img src="<html:rewrite page='/includes/images/scratchie-correct.png'/>" class="scartchie-image"
								 id="image-${item.uid}-${answer.uid}">
						</c:when>
						<c:when test="${answer.scratched && !answer.correct}">
							<img src="<html:rewrite page='/includes/images/scratchie-wrong.png'/>" class="scartchie-image"
								 id="image-${item.uid}-${answer.uid}">
						</c:when>
						<c:when test="${sessionMap.userFinished || item.unraveled || !isUserLeader || (mode == 'teacher') || showResults}">
							<img src="<html:rewrite page='/includes/images/answer-${status.index + 1}.png'/>" class="scartchie-image"
								 id="image-${item.uid}-${answer.uid}">
						</c:when>
						<c:otherwise>
							<a href="#nogo" onclick="scratchItem(${item.uid}, ${answer.uid}); return false;"
								id="imageLink-${item.uid}-${answer.uid}"> <img
								src="<html:rewrite page='/includes/images/answer-${status.index + 1}.png'/>" class="scartchie-image"
								id="image-${item.uid}-${answer.uid}" />
							</a>
						</c:otherwise>
					</c:choose> 
					
					<c:if test="${(showResults || mode == 'teacher') && (answer.attemptOrder != -1)}">
						<div style="text-align: center; margin-top: 2px;">
							<fmt:message key="label.choice.number">
								<fmt:param>${answer.attemptOrder}</fmt:param>
							</fmt:message>
						</div>
					</c:if>
				</td>

				<td 
					<c:if test="${fn:length(answer.confidenceLevelDtos) > 0}">class="answer-with-confidence-level-portrait"</c:if>
				>
					<c:out value="${answer.description}" escapeXml="false" />
					
					<c:if test="${scratchie.confidenceLevelsActivityUiid != null}">
						<div id="user-confidence-levels">
							<c:forEach var="confidenceLevelDto" items="${answer.confidenceLevelDtos}" varStatus="status">
							
								<div class="c100 p${confidenceLevelDto.level}0 small">
									<span>
										<c:choose>
										<c:when test="${confidenceLevelDto.portraitUuid == null}">
											<div class="portrait-generic-sm portrait-color-${confidenceLevelDto.userId % 7}"></div>
										</c:when>
										<c:otherwise>
			    								<img class="portrait-sm portrait-round" src="${lams}download/?uuid=${confidenceLevelDto.portraitUuid}&preferDownload=false&version=4" alt="">
										</c:otherwise>
										</c:choose>
									</span>
									<div class="slice">
										<div class="bar"></div>
										<div class="fill"></div>
									</div>
									<div class="confidence-level-percentage">
										${confidenceLevelDto.level}0%
									</div>
								</div>
	    
							</c:forEach>
						</div>
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</table>
	
	<%-- show burning questions --%>
	<c:if test="${!showResults && scratchie.burningQuestionsEnabled && (isUserLeader || (mode == 'teacher'))}">
		<div class="form-group burning-question-container">
			<!-- LDEV-4532: href is needed for the collapsing to work on an ipad but do not make it the same as the data-target here or the screen will jump around. -->
			<a data-toggle="collapse" data-target="#burning-question-item${item.uid}" href="#bqi${item.uid}"
					<c:if test="${empty item.burningQuestion}">class="collapsed"</c:if>>
				<span class="if-collapsed"><i class="fa fa-xs fa-plus-square-o roffset5" aria-hidden="true"></i></span>
  				<span class="if-not-collapsed"><i class="fa fa-xs fa-minus-square-o roffset5" aria-hidden="true"></i></span>
				<fmt:message key="label.burning.question" />
			</a>
			
			<div id="burning-question-item${item.uid}" class="collapse <c:if test="${not empty item.burningQuestion}">in</c:if>">
				<textarea rows="5" name="burningQuestion${item.uid}" class="form-control"
					<c:if test="${mode == 'teacher'}">disabled="disabled"</c:if>
				>${item.burningQuestion}</textarea>
			</div>
		</div>
	</c:if>
	
	<%-- show link to burning questions (only for results page) --%>
	<c:if test="${showResults && mode != 'teacher' && scratchie.burningQuestionsEnabled}">
		<div class="scroll-down-to-bq">
			<a href='#gbox_burningQuestions${item.uid}' data-item-uid="${item.uid}" class='pull-right' title="<fmt:message key="label.scroll.down.to.burning.question"/>">
				<i class="fa fa-xs fa-angle-double-down roffset5"></i>
			</a>
		</div>
	</c:if>

</c:forEach>
