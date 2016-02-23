<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<lams:html>

	<lams:head>
		<%@ include file="/common/header.jsp"%>

		<title> <fmt:message key="label.view.reflection"/> </title>


		<link type="text/css" href="${lams}/css/jquery-ui-smoothness-theme.css" rel="stylesheet"/>
		<link type="text/css" href="${lams}/css/jquery-ui.timepicker.css" rel="stylesheet"/>
		<link type="text/css" href="${lams}css/jquery.jRating.css" rel="stylesheet"/>
		<link rel="stylesheet" href="${lams}css/jquery.tablesorter.theme-blue.css"/>
		<link rel="stylesheet" href="${lams}css/jquery.tablesorter.pager.css"/>

		<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery.tablesorter.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery.tablesorter-pager.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery.tablesorter-widgets.js"></script> 
		
	
		<script type="text/javascript">
	
			$(document).ready(function(){
	    
			$(".tablesorter").tablesorter({
				theme: 'blue',
			    sortInitialOrder: 'desc',
	            sortList: [[0]],
	            widgets: [ "resizable", "filter" ],
	            headers: { 1: { filter: false, sorter: false} }, 
	            widgetOptions: {
	            	resizable: true,
	            	// include column filters 
	                filter_columnFilters: true, 
	                filter_placeholder: { search : '<fmt:message key="label.search"/>' }, 
	                filter_searchDelay: 700 
	            }
			});
		
		$(".tablesorter").each(function() {
			$(this).tablesorterPager({
				savePages: false,
				ajaxUrl : "<lams:WebAppURL/>monitoring.do?dispatch=getReflectionsJSON&page={page}&size={size}&{sortList:column}&{filterList:fcol}&toolSessionID=" + $(this).attr('data-session-id'),
				ajaxProcessing: function (data, table) {
					if (data && data.hasOwnProperty('rows')) {
			    		var rows = [],
			            json = {};
			    		
						for (i = 0; i < data.rows.length; i++){
							var userData = data.rows[i];

							rows += '<tr>';
							rows += '<td>';
							rows += userData["username"];
							rows += '</td>';

							rows += '<td>';
							if ( userData["notebook"] ) {
								rows += userData["notebook"];
							} else {
								rows += '-';
							}
							rows += '</td>';

							rows += '</tr>';
						}
			            
						json.total = data.total_rows;
						json.rows = $(rows);
						return json;
			            
			    	}
				},					
			    container: $(this).next(".pager"),
			    output: '{startRow} to {endRow} ({totalRows})',
			    // css class names of pager arrows
			    cssNext: '.tablesorter-next', // next page arrow
				cssPrev: '.tablesorter-prev', // previous page arrow
				cssFirst: '.tablesorter-first', // go to first page arrow
				cssLast: '.tablesorter-last', // go to last page arrow
				cssGoto: '.gotoPage', // select dropdown to allow choosing a page
				cssPageDisplay: '.pagedisplay', // location of where the "output" is displayed
				cssPageSize: '.pagesize', // page size selector - select dropdown that sets the "size" option
				// class added to arrows when at the extremes (i.e. prev/first arrows are "disabled" when on the first page)
				cssDisabled: 'disabled' // Note there is no period "." in front of this class name
			})
		});
  	})
</script>	
	</lams:head>
<body class="stripes">

	<div id="content">
	
		<h1><fmt:message key="label.notebook.entries" /></h1>

		<div class="tablesorter-holder">
		<table class="tablesorter" data-session-id="${param.toolSessionID}">
			<thead>
				<tr>
					<th><fmt:message key="label.user"/></th>
					<th><fmt:message key="label.reflection" /></th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		
		<!-- pager -->
		<div class="pager">
			<form>
				<img class="tablesorter-first"/>
				<img class="tablesorter-prev"/>
				<span class="pagedisplay"></span> <!-- this can be any element, including an input -->
				<img class="tablesorter-next"/>
				<img class="tablesorter-last"/>
				<select class="pagesize">
					<option selected="selected" value="10">10</option>
					<option value="20">20</option>
					<option value="30">30</option>
					<option value="40">40</option>
					<option value="50">50</option>
					<option value="100">100</option>
				</select>
			</form>
		</div>
		</div>
				
	</div>
	<div id="footer"></div>
</body>
</lams:html>
