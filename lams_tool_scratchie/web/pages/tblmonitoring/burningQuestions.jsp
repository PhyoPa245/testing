<%@ include file="/common/taglibs.jsp"%>
<script>
	$(document).ready(function(){
		//handler for expand/collapse all button
		$("#toggle-burning-questions-button").click(function() {
			var isExpanded = eval($(this).data("expanded"));
				
			//fire the actual buttons so burning questions can be closed/expanded
			if (isExpanded) {
				$(".burning-question-title:not('.collapsed')").each(function() {
					this.click();
				});
								
			} else {
				$(".burning-question-title.collapsed").each(function() {
					this.click();
				});					
			}

			//change button label
			var newButtonLabel = isExpanded ? "<fmt:message key='label.expand.all' />" : "<fmt:message key='label.collapse.all' />";
			$(".hidden-xs", $(this)).text(newButtonLabel);

			//change button icon
			if (isExpanded) {
				$(".fa", $(this)).removeClass("fa-minus-circle").addClass("fa-plus-circle");
			} else {
				$(".fa", $(this)).removeClass("fa-plus-circle").addClass("fa-minus-circle");
			}

			//change button's data-expanded attribute
			$(this).data("expanded", !isExpanded);
		});
	});
</script>

<!-- Header -->
<div class="row no-gutter">
	<div class="col-xs-12">
        <button type="button" id="toggle-burning-questions-button" class="btn btn-sm btn-default pull-right voffset20" data-expanded="false">
           	<i class="fa fa-plus-circle"></i> 
           	<span class="hidden-xs">
           		<fmt:message key='label.expand.all' />
           	</span>
        </button>
            
		<h3>
			<fmt:message key="label.burning.questions"/>
		</h3>
	</div>
</div>
<!-- End header -->              

<!-- Tables -->
<div class="row no-gutter voffset10">
<div class="col-xs-12 col-md-12 col-lg-12">

	<c:forEach var="burningQuestionItemDto" items="${burningQuestionItemDtos}" varStatus="i">
		<c:set var="burningQsCount" value="${fn:length(burningQuestionItemDto.burningQuestionDtos)}"/>
		<c:set var="item" value="${burningQuestionItemDto.scratchieItem}"/>
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h4 class="panel-title panel-collapse">
					<c:choose>
						<c:when test="${burningQsCount != 0}">
							<a data-toggle="collapse" data-itemuid="${item.uid}" class="collapsed burning-question-title">
								Q${i.index+1}) <c:out value="${item.title}" escapeXml="false"/>
							</a>
						</c:when>
						<c:otherwise>
							Q${i.index+1}) <c:out value="${item.title}" escapeXml="false"/> 
						</c:otherwise>
					</c:choose>
                    <c:if test="${burningQsCount > 0}">
					   <span class="badge pull-right" style="margin-right: 4px">${burningQsCount}</span>
                    </c:if>    
				</h4>
			</div>
		
			<c:if test="${burningQsCount > 0}">
				<div id="collapse-${item.uid}" class="panel-collapse collapse">
				<div class="panel-body">
				
					<span class="burning-question-description">
						<c:out value="${item.description}" escapeXml="false"/> 
					</span>
					
					<div class="table-responsive">
						<table class="table table-striped table-bordered table-hover">
							<tbody>
								<c:forEach var="burningQuestionDto" items="${burningQuestionItemDto.burningQuestionDtos}">
									<tr>
										<td class="text-nowrap">
											<c:out value="${burningQuestionDto.sessionName}" escapeXml="false"/>
										</td>
										<td>
											${burningQuestionDto.escapedBurningQuestion}
										</td>
										<td class="text-nowrap">
											<span class="badge">${burningQuestionDto.likeCount}</span> &nbsp; <i class="fa fa-thumbs-o-up" style="color:darkblue"></i>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				</div>
			</c:if>
			
		</div>
	</c:forEach>    
  
</div>
</div>
