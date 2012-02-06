<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-lams" prefix="lams"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<lams:html>
<lams:head>
	<c:url value="/lessonConditions.do" var="addLessonDependencyUrl">
		<c:param name="method" value="addLessonDependency" />
		<c:param name="lsId" value="${lsId}" />
	</c:url>
	
	<lams:css/>
	<style type="text/css">
		td.lessonList {
			padding: 0px 0px 10px 10px;
		}
		
		td.emptyList {
			font-style: italic;
		}
	</style>
	
	<script type="text/javascript" src="includes/javascript/jquery-latest.pack.js"></script>
	<script type="text/javascript">
		var lessonId="${lsId}";
		var edit="${edit}";
		
		function removePrecedingLesson(precedingLessonId){
			document.location.href="<lams:LAMSURL/>lessonConditions.do?method=removeLessonDependency&lsId=" + lessonId
						  + "&precedingLessonId=" + precedingLessonId;
		}

		$(document).ready(function(){
			if (edit == 'true'){
				// refresh Index page after editable conditions thickbox is closed
				$('#TB_window',window.parent.document).attr('TB_refreshParentOnClose','true');
			}
		});
	</script>
</lams:head>
    
<body class="stripes">
<div id="page">
<div id="content">
<table>

	<tr>
		<td colspan="2">
			<h4>
				<fmt:message key="label.conditions.box.title">
					<fmt:param>${title}</fmt:param>
				</fmt:message>
			</h4>
		</td>
	</tr>
	<c:choose>
		<c:when test="${empty precedingLessons}">
			<tr>
				<td colspan="2" class="lessonList emptyList">
					<fmt:message key="label.conditions.box.no.dependency" />
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<c:forEach var="precedingLesson" items="${precedingLessons}">
				<tr>
					<td class="lessonList" style="width: 70%">
						<c:out value="${precedingLesson.name}" />
					</td>
					<td>
						<c:if test="${edit}">
							<img src="<lams:LAMSURL/>images/icons/cross.png"
								 style="cursor: pointer;"
								 title="<fmt:message key="label.conditions.box.remove.dependency" />"
								 onclick="javascript:removePrecedingLesson(${precedingLesson.id})" />
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${edit}">
		<tr>
			<td colspan="2">
				<h4>
					<fmt:message key="label.conditions.box.add.dependency" />
				</h4>
			</td>
		</tr>
		
		<c:choose>
			<c:when test="${empty availableLessons}">
				<tr>
					<td colspan="2" class="lessonList emptyList">
						<fmt:message key="label.conditions.box.no.dependency" />
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<form action="${addLessonDependencyUrl}" method="post">
				<tr>
					<td class="lessonList" style="width: 70%">
						<select name="precedingLessonId">
							<c:forEach var="availableLesson" items="${availableLessons}">
								<option value="${availableLesson.id}">${availableLesson.name}</option>
							</c:forEach>
						</select>
					</td>
					<td>
						<input class="button" type="submit" value="<fmt:message key="index.addlesson"/>" />
					</td>
				</tr>	
				</form>
			</c:otherwise>
		</c:choose>
	</c:if>
</table>
</div>
</div>
</body>
</lams:html>