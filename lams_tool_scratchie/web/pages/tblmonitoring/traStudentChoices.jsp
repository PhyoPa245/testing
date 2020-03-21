<%@ include file="/common/taglibs.jsp"%>
<% pageContext.setAttribute("newLineChar", "\r\n"); %>
<style>
	/* show horizontal scroller for iPads */
	body {
	    -webkit-overflow-scrolling: touch;
	}

	/*----  fixed first column ----*/
	table {
	  position: relative;
	}
	
	thead th {
	  position: -webkit-sticky; /* for Safari */
	  position: sticky;
	  top: 0;
	  background: #FFF;
	}
	
	thead th:first-child {
	  left: 0;
	  z-index: 1;
	}
	
	tbody th {
	  position: -webkit-sticky; /* for Safari */
	  position: sticky;
	  left: 0;
	  background: #FFF;
	}
</style>

<script>
	function exportExcel(){
		//dynamically create a form and submit it
		var exportExcelUrl = "<lams:LAMSURL/>tool/lascrt11/tblmonitoring/exportExcel.do?toolContentID=${toolContentID}&reqID=" + (new Date()).getTime();
		var form = $('<form method="post" action="' + exportExcelUrl + '"></form>');
	    var hiddenInput = $('<input type="hidden" name="<csrf:tokenname/>" value="<csrf:tokenvalue/>"></input>');
	    form.append(hiddenInput);
	    $(document.body).append(form);
	    form.submit();
	};
</script>

<!-- Header -->
<div class="row no-gutter">
	<div class="col-12 col-lg-12 col-xl-8">
		<h3>
			<fmt:message key="label.tra.questions.marks"/>
		</h3>
	</div>
</div>
<!-- End header -->

<!-- Notifications -->  
<div class="row">
	<div class="col-lg-6 col-xl-4 ">
	</div>
	
	<div class="col-12 col-lg-6 col-xl-4 offset-xl-2">
		<a href="#nogo" type="button" class="btn btn-sm btn-default buttons_column"
				onclick="javascript:loadTab('tra'); return false;">
			<i class="fa fa-undo"></i>
			<fmt:message key="label.hide.students.choices"/>
		</a>
		<a href="#nogo" onclick="javascript:printTable(); return false;" type="button" class="btn btn-sm btn-default buttons_column">
			<i class="fa fa-print"></i>
			<fmt:message key="label.print"/>
		</a>
		<a href="#nogo" onclick="javascript:exportExcel(); return false;" type="button" class="btn btn-sm btn-default buttons_column">
			<i class="fa fa-file"></i>
			<fmt:message key="label.export.excel"/>
		</a>
	</div>
</div>
<br>
<!-- End notifications -->

<!-- Table --> 
<div class="row no-gutter">
<div class="col-12 col-lg-12 col-xl-12">
<div class="panel">
<div class="panel-body">
<div class="table-responsive">
	<table id="questions-data" class="table table-striped table-bordered table-hover table-condensed">
		<thead>
			<tr role="row">
			
				<th></th>
				<c:forEach var="item" items="${items}" varStatus="i">
					<th class="text-center">
						<a data-toggle="modal" href="#question${i.index}Modal">
							<fmt:message key="label.authoring.basic.question.text"/> ${i.index + 1}
						</a>
					</th>
				</c:forEach>
				<th class="text-center">
					<fmt:message key="label.total"/>
				</th>
				<th class="text-center">
					<fmt:message key="label.total"/> %
				</th>
				
			</tr>
		</thead>
		<tbody>
		
			<tr>
				<th>
					<b>
						<fmt:message key="label.correct.answer"/>
					</b>
				</th>
				
				<c:forEach var="correctAnswerLetter"  items="${correctAnswerLetters}" varStatus="status">
					<td class="text-center">
						${correctAnswerLetter}
					</td>
				</c:forEach>
				
				<td class="text-center"></td>
				<td class="text-center"></td>
			</tr>
			
			<tr>
				<th colspan="0" style="font-weight: bold;">
					<fmt:message key="label.teams.notuppercase"/>
				</th> 
			</tr>
			
			<c:forEach var="sessionDto" items="${sessionDtos}" varStatus="i">
				<tr>
					<th class="text-center">
						${sessionDto.sessionName}
					</th>
					
					<c:choose>
						<c:when test="${empty sessionDto.itemDtos}">
							<c:forEach begin="1" end="${fn:length(items) + 2}">
								<td></td>
							</c:forEach>
						</c:when>
						
						<c:otherwise>
							<c:forEach var="itemDto" items="${sessionDto.itemDtos}">
								<td class="text-center">
									<c:forEach var="optionDto" items="${itemDto.optionDtos}">
										<c:if test="${optionDto.answer != ''}">
											<span class="user-response <c:if test="${optionDto.correct}">successful-response</c:if> <c:if test="${!optionDto.correct}">wrong-response</c:if>">
												<c:out value="${optionDto.answer}" escapeXml="${!optionDto.mcqType}"/>
											</span>
										</c:if>
									</c:forEach>
								</td>
							</c:forEach>
							
							<td class="text-center">
								${sessionDto.mark}
							</td>
							
							<td class="text-center">
								<fmt:formatNumber type="number" minFractionDigits="0" maxFractionDigits="2" value="${sessionDto.totalPercentage}" /> %
							</td>
						</c:otherwise>
					</c:choose>
					
				</tr>
			</c:forEach>                                               
		</tbody>
	</table>
	</div>
</div>
</div>
</div>          
</div>

<!-- Question detail modal -->
<c:forEach var="item" items="${items}" varStatus="i">
	<div class="modal fade" id="question${i.index}Modal">
	<div class="modal-dialog">
	<div class="modal-content">
	<div class="modal-body">
	
		<div class="panel panel-default">
			<div class="panel-heading">
				<h4 class="panel-title"><span class="float-left space-right">Q${i.index+1})</span> ${item.qbQuestion.name}</h4>
			</div>
			<div class="panel-body">
			
				<div>
					${item.qbQuestion.description}
				</div>
			
				<div class="table-responsive voffset10">
					<table class="table table-striped table-hover">
						<tbody>
						
							<c:forEach var="qbOption" items="${item.qbQuestion.qbOptions}" varStatus="j">
								<c:set var="cssClass"><c:if test='${qbOption.correct}'>bg-success</c:if></c:set>
								
								<tr>
									<td width="5px" class="${cssClass}">
										${ALPHABET[j.index]}.
									</td>									
									<td class="${cssClass}">
										<c:choose>
											<c:when test="${item.qbQuestion.type == 1}">
												<c:out value="${qbOption.name}" escapeXml="false"/>
											</c:when>
											<c:otherwise>
												${fn:replace(qbOption.name, newLineChar, ', ')}
											</c:otherwise>
										</c:choose>
									</td>
								</tr>					
							</c:forEach>
							
						</tbody>
					</table>
				</div>
			</div> 
		</div>
	            
		<div class="modal-footer">	
			<a href="#" data-dismiss="modal" class="btn btn-default">
				<fmt:message key="button.close"/>
			</a>
		</div>
	
	</div>
	</div>
	</div>
	</div>
</c:forEach>
<!-- End question detail modal -->
