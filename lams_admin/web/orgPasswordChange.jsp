<%@ include file="/taglibs.jsp"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration"%>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys"%>
<%@ page import="org.apache.struts.action.ActionMessages"%>
<c:set var="minNumChars"><%=Configuration.get(ConfigurationKeys.PASSWORD_POLICY_MINIMUM_CHARACTERS)%></c:set>
<c:set var="mustHaveUppercase"><%=Configuration.get(ConfigurationKeys.PASSWORD_POLICY_UPPERCASE)%></c:set>
<c:set var="mustHaveNumerics"><%=Configuration.get(ConfigurationKeys.PASSWORD_POLICY_NUMERICS)%></c:set>
<c:set var="mustHaveLowercase"><%=Configuration.get(ConfigurationKeys.PASSWORD_POLICY_LOWERCASE)%></c:set>
<c:set var="mustHaveSymbols"><%=Configuration.get(ConfigurationKeys.PASSWORD_POLICY_SYMBOLS)%></c:set>

<lams:css />
<link type="text/css" href="<lams:LAMSURL/>css/free.ui.jqgrid.min.css" rel="stylesheet" />
<style type="text/css">
	.changeContainer .checkbox {
		display: inline-block;
	}
	
	.changeContainer .pass {
		display: inline-block;
		margin-left: 20px;
		width: 260px;
	}
	
	.changeContainer .fa {
		cursor: pointer;
	}
	
	h3, h4 {
		text-align: center;
	}
	
	#changeTable > tbody > tr > td{
		padding-left: 50px;
		padding-top: 20px;
	}
	
	#changeTable > tbody > tr > td:first-child {
		border-right: thin solid black;
		padding-right: 50px;
	}
	
	.jqgh_cbox {
		visibility: hidden;
	}
	
	.ui-jqgrid-btable tr[role="row"] {
		cursor: pointer;	
	}
	
	.ui-jqgrid-btable tr.success > td {
		background-color: transparent !important;
	}
</style>
<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.js"></script>
<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery-ui.js"></script>
<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.validate.js"></script>
<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/bootstrap.min.js"></script>
<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/free.jquery.jqgrid.min.js"></script>
<script type="text/javascript">
     var mustHaveUppercase = ${mustHaveUppercase},
	     mustHaveNumerics  = ${mustHaveNumerics},
	     mustHaveLowercase  = ${mustHaveLowercase},
	     mustHaveSymbols   = ${mustHaveSymbols},
	     excludedLearners = JSON.parse("<bean:write name='OrgPasswordChangeForm' property='excludedLearners' />"),
	     excludedStaff = JSON.parse("<bean:write name='OrgPasswordChangeForm' property='excludedStaff' />");

     $.validator.addMethod("pwcheck", function(value) {
	      return (!mustHaveUppercase || /[A-Z]/.test(value)) && // has uppercase letters 
			     (!mustHaveNumerics || /\d/.test(value)) && // has a digit
			     (!mustHaveLowercase || /[a-z]/.test(value)) && // has a lower case
			     (!mustHaveSymbols || /[`~!@#$%^&*\(\)_\-+={}\[\]\\|:\;\"\'\<\>,.?\/]/.test(value)); //has symbols
     });
	 $.validator.addMethod("charactersAllowed", function(value) {
		  return /^[A-Za-z0-9\d`~!@#$%^&*\(\)_\-+={}\[\]\\|:\;\"\'\<\>,.?\/]*$/
			     .test(value)
	 });

	$(function() {
		// assign grid ID to each checkbox and define what happens when it gets (un)checked
		var changeCheckboxes = $('#isStaffChange').data('grid', 'staffGrid')
							  .add($('#isLearnerChange').data('grid', 'learnerGrid'))
							  .change(function(){
								  		var checkbox = $(this);
										// prevent both checkboxes from being unchecked
										if (!changeCheckboxes.is(':checked')) {
											checkbox.prop('checked', true);
											return;
										}
										
										var	enabled = checkbox.prop('checked'),
											grid = $('#' + checkbox.data('grid')).closest('.ui-jqgrid');
										// disable/enable password input depending on checkbox state
										checkbox.closest('.changeContainer').find('.pass').prop('disabled', !enabled);
										// hide/show users grid
										if (enabled) {
											grid.slideDown();
										} else {
											grid.slideUp();
										}
									});

		// generate new password on click
		$('.generatePassword').click(function(){
			var container = $(this).closest('.changeContainer');
			if (!container.find('input[type="checkbox"]').prop('checked')) {
				return;
			}
			$.ajax({
				'url' : 'orgPasswordChange.do',
				'data': {
					'method' : 'generatePassword'
				}
			}).done(function(password){
				container.find('.pass').val(password);
			});
		});
		
		
		
		// Setup form validation 
		$("#OrgPasswordChangeForm").validate({
			debug : true,
			errorClass : 'help-block',
			//  validation rules
			rules : {
				learnerPassword : {
					required: false,
					minlength : <c:out value="${minNumChars}"/>,
					maxlength : 25,
					charactersAllowed : true,
					pwcheck : true
				},
				staffPassword: {
					required: false,
					minlength : <c:out value="${minNumChars}"/>,
					maxlength : 25,
					charactersAllowed : true,
					pwcheck : true
				}
				
			},

			// Specify the validation error messages
			messages : {
				learnerPassword : {
					required : "<fmt:message key='error.password.empty'/>",
					minlength : "<fmt:message key='label.password.min.length'><fmt:param value='${minNumChars}'/></fmt:message>",
					maxlength : "<fmt:message key='label.password.max.length'/>",
					charactersAllowed : "<fmt:message key='label.password.symbols.allowed'/> ` ~ ! @ # $ % ^ & * ( ) _ - + = { } [ ] \ | : ; \" ' < > , . ? /",
					pwcheck : "<fmt:message key='label.password.restrictions'/>"
				},
				staffPassword: {
					required : "<fmt:message key='error.password.empty'/>",
					minlength : "<fmt:message key='label.password.min.length'><fmt:param value='${minNumChars}'/></fmt:message>",
					maxlength : "<fmt:message key='label.password.max.length'/>",
					charactersAllowed : "<fmt:message key='label.password.symbols.allowed'/> ` ~ ! @ # $ % ^ & * ( ) _ - + = { } [ ] \ | : ; \" ' < > , . ? /",
					pwcheck : "<fmt:message key='label.password.restrictions'/>"
				}
				
			},

			submitHandler : function(form) {
				form.submit();
			}
		});

		var jqGridURL = "orgPasswordChange.do?method=getGridUsers&organisationID=<bean:write name='OrgPasswordChangeForm' property='organisationID' />&role=",
			jqGridSettings = {
				datatype		   : "json",
			    height			   : "100%",
			    // use new theme
			    guiStyle 		   : "bootstrap",
			    iconSet 		   : 'fontAwesome',
			    autowidth		   : true,
			    shrinkToFit 	   : true,
			    multiselect 	   : true,
			    multiPageSelection : true,
			    // it gets ordered by 
			    sortorder		   : "asc", 
			    sortname		   : "firstName", 
			    pager			   : true,
			    rowNum			   : 10,
				colNames : [
					'<fmt:message key="admin.org.password.change.grid.name"/>',
					'<fmt:message key="admin.org.password.change.grid.login"/>',
					'<fmt:message key="admin.org.password.change.grid.email"/>'
				],
			    colModel : [
			        {
						   'name'  : 'name', 
						   'index' : 'firstName',
						   'title' : false
					},
					{
					   'name'  : 'login', 
					   'index' : 'login',
					   'title' : false
					},
			        {
					   'name'  : 'email', 
					   'index' : 'email',
					   'title' : false
					}
			    ],
			    onSelectRow : function(id, status, event) {
				    var grid = $(this),
						excluded = grid.data('excluded'),
						index = excluded.indexOf(+id);
					// if row is deselected, add it to excluded array
					if (index < 0) {
						if (!status) {
							excluded.push(+id);
						}
					} else if (status) {
						excluded.splice(index, 1);
					}
				},
				gridComplete : function(){
					var grid = $(this),
						// get excludedLearners or excludedStaff
						excluded = grid.data('excluded');
					// go through each loaded row
					$('[role="row"]', grid).each(function(){
						var id = +$(this).attr('id'),
							selected = $(this).hasClass('success');
						// if row is not selected and is not excluded, select it
						if (!selected && !excluded.includes(id)) {
							// select without triggering onSelectRow
							grid.jqGrid('setSelection', id, false);
						}
					});
				},
			    loadError : function(xhr,st,err) {
			    	$.jgrid.info_dialog('<fmt:message key="admin.error"/>', 
	    					'<fmt:message key="admin.org.password.change.grid.error.load"/>',
	    					'<fmt:message key="label.ok"/>');
			    }
		};
		
		jqGridSettings.url = jqGridURL + 'learner'
		$("#learnerGrid").data('excluded', excludedLearners).jqGrid(jqGridSettings);
		jqGridSettings.url = jqGridURL + 'staff'
		$("#staffGrid").data('excluded', excludedStaff).jqGrid(jqGridSettings);

	});
</script>

<p>
	<a href="<lams:LAMSURL/>admin/usermanage.do?org=<bean:write name='OrgPasswordChangeForm' property='organisationID' />" class="btn btn-default">
	<fmt:message key="admin.user.manage" /></a>
</p>

<form id="OrgPasswordChangeForm" action="orgPasswordChange.do" method="post">
	<div class="panel panel-default panel-body">
		<h3><bean:write name='OrgPasswordChangeForm' property='orgName' /></h3>
		<lams:Alert type="info" id="passwordConditions" close="false">
			<fmt:message key='label.password.must.contain' />:
			<ul class="list-unstyled" style="line-height: 1.2">
				<li><span class="fa fa-check"></span> <fmt:message
						key='label.password.min.length'>
						<fmt:param value='${minNumChars}' />
					</fmt:message></li>

				<c:if test="${mustHaveUppercase}">
					<li><span class="fa fa-check"></span> <fmt:message
							key='label.password.must.ucase' /></li>
				</c:if>
				<c:if test="${mustHaveLowercase}">
					<li><span class="fa fa-check"></span> <fmt:message
							key='label.password.must.lcase' /></li>
				</c:if>

				<c:if test="${mustHaveNumerics}">
					<li><span class="fa fa-check"></span> <fmt:message
							key='label.password.must.number' /></li>
				</c:if>


				<c:if test="${mustHaveSymbols}">
					<li><span class="fa fa-check"></span> <fmt:message
							key='label.password.must.symbol' /></li>
				</c:if>
			</ul>
			
		</lams:Alert>
		<logic:messagesPresent>
			<lams:Alert type="danger" id="form-error" close="false">
				<html:messages id="error">
					<c:out value="${error}" escapeXml="false" />
					<br />
				</html:messages>
			</lams:Alert>
		</logic:messagesPresent>
		
		<div class="form-group">
			<div class="checkbox">
				<label>
					<input name="email" value="true" type="checkbox"/><fmt:message key="admin.org.password.change.email" />
				</label>
			</div>
			<div class="checkbox">
				<label>
					<input name="force" value="true" type="checkbox"/><fmt:message key="admin.org.password.change.force" />
				</label>
			</div>
		</div>
		<h4><fmt:message key="admin.org.password.change.choose" /></h4>
		<table id="changeTable">
			<tbody>
				<tr>
					<td class="changeContainer">
						<div class="checkbox">
							<label>
								<input id="isStaffChange" name="isStaffChange" value="true" type="checkbox" checked="checked"/>
								<fmt:message key="admin.org.password.change.is.staff" />
							</label>
						</div>
						<input type="text" id="staffPass" name="staffPass" class="pass form-control" maxlength="25"
						       placeholder="<fmt:message key='admin.org.password.change.custom' />"
						       title="<fmt:message key='admin.org.password.change.custom' />"
							   value="<bean:write name='OrgPasswordChangeForm' property='staffPass' />" />
						<i class="fa fa-refresh generatePassword" title="<fmt:message key='admin.org.password.change.generate' />"></i>
					</td>
					<td class="changeContainer">
						<div class="checkbox">
							<label>
								<input id="isLearnerChange" name="isLearnerChange" value="true" type="checkbox" checked="checked"/>
								<fmt:message key="admin.org.password.change.is.learners" />
							</label>
						</div>
						<input type="text" id="learnerPass" name="learnerPass" class="pass form-control" maxlength="25"
							   placeholder="<fmt:message key='admin.org.password.change.custom' />"
						       title="<fmt:message key='admin.org.password.change.custom' />"
							   value="<bean:write name='OrgPasswordChangeForm' property='learnerPass' />" />
						<i class="fa fa-refresh generatePassword" title="<fmt:message key='admin.org.password.change.generate' />"></i>
					</td>
				</tr>
				<tr>
					<td>
						<table id="staffGrid"></table>
					</td>
					<td>
						<table id="learnerGrid"></table>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</form>