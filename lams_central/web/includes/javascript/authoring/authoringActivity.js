﻿/**
 * This file contains methods for Activity manipulation on canvas.
 */

var ActivityLib = {
	
	/**
	 * Constructor for a Tool Activity.
	 */
	ToolActivity: function(id, toolID, x, y, title, supportsOutputs) {
		this.id = id;
		this.type = 'tool';
		this.toolID = toolID;
		this.title = title;
		this.supportsOutputs = supportsOutputs;
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		this.loadPropertiesDialogContent = ActivityLib.loadPropertiesDialogContent.tool;
		
		this.draw = ActivityLib.draw.tool;
		this.draw(x, y);
	},

	/**
	 * Constructor for a Grouping Activity.
	 */
	GroupingActivity : function(id, x, y, title, groupingID, groupingType, groupDivide, groupCount, learnerCount,
			equalSizes, viewLearners, groupNames) {
		this.id = id;
		this.groupingID = groupingID;
		this.type = 'grouping';
		this.title = title;
		this.groupingType = groupingType || 'random';
		this.groupDivide = groupDivide || 'groups';
		this.groupCount = groupCount || 2;
		this.learnerCount = learnerCount || 1;
		this.equalSizes = equalSizes || false;
		this.viewLearners = viewLearners || false;
		this.groupNames = groupNames || [];
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		this.loadPropertiesDialogContent = ActivityLib.loadPropertiesDialogContent.grouping;

		this.draw = ActivityLib.draw.grouping;		
		this.draw(x, y);
	},

	/**
	 * Constructor for a Gate Activity.
	 */
	GateActivity : function(id, x, y, gateType) {
		this.id = id;
		this.type = 'gate';
		this.gateType = gateType || 'permission';
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		this.loadPropertiesDialogContent = ActivityLib.loadPropertiesDialogContent.gate;

		this.draw = ActivityLib.draw.gate;
		this.draw(x, y);
	},
	
	
	/**
	 * Either branching or converge point.
	 */
	BranchingEdgeActivity : function(id, x, y, title, branchingType, branchingActivity) {
		this.type = 'branchingEdge';
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		if (branchingActivity) {
			// branchingActivity already exists, so this is the converge point
			this.isStart = false;
			branchingActivity.end = this;
		} else {
			// this is the branching point
			this.isStart = true;
			branchingActivity = new ActivityLib.BranchingActivity(id, this);
			branchingActivity.branchingType = branchingType || 'chosen';
			branchingActivity.title = title || 'Branching';
		}
		this.branchingActivity = branchingActivity;
		
		this.loadPropertiesDialogContent = ActivityLib.loadPropertiesDialogContent.branching;
		
		this.draw = ActivityLib.draw.branching;
		this.draw(x, y);
	},
	
	
	/**
	 * Represents a set of branches. It is not displayed on canvas.
	 */
	BranchingActivity : function(id, branchingEdgeStart) {
		this.id = id;
		this.start = branchingEdgeStart;
		this.branches = [];
	},
	
	
	/**
	 * Represents a subsequence of activities. It is not displayed on canvas.
	 */
	BranchActivity : function(id, title, branchingActivity, transitionFrom) {
		this.id = id;
		this.title = title;
		this.transitionFrom = transitionFrom;
		this.branchingActivity = branchingActivity;
	},
	
	
	/**
	 * Mehtods for drawing various kinds of activities.
	 */
	draw : {
		tool : function(x, y) {
			if (x == undefined || y == undefined) {
				// just redraw the activity
				x = this.items.shape.getBBox().x;
				y = this.items.shape.getBBox().y;
			}
			
			if (this.items) {
				this.items.remove();
			}
			
			// create activity SVG elements
			paper.setStart();
			var shape = paper.path(Raphael.format('M {0} {1}' + layout.defs.activity, x, y))
							 .attr({
								'fill' : this.offline ? layout.colors.offlineActivity : layout.colors.activity
							 });
			paper.image(layout.toolIcons[this.toolID], x + 47, y + 2, 30, 30);
			paper.text(x + 62, y + 40, ActivityLib.shortenActivityTitle(this.title))
				 .attr({
					 'fill' : this.offline ? layout.colors.offlineActivityText : layout.colors.activityText
				 });
			
			this.items = paper.setFinish();
			this.items.shape = shape;
			
			if (this.grouping) {
				ActivityLib.addGroupingEffect(this);
			}
			
			ActivityLib.activityHandlersInit(this);
		},
		
		
		grouping : function(x, y) {
			if (x == undefined || y == undefined) {
				// just redraw the activity
				x = this.items.shape.getBBox().x;
				y = this.items.shape.getBBox().y;
			}

			if (this.items) {
				this.items.remove();
			}
			
			// create activity SVG elements
			paper.setStart();
			var shape = paper.path(Raphael.format('M {0} {1}' + layout.defs.activity, x, y))
							 .attr({
									'fill' : layout.colors.activity
								 });
			
			paper.image('../images/grouping.gif', x + 47, y + 2, 30, 30);
			paper.text(x + 62, y + 40, ActivityLib.shortenActivityTitle(this.title));
			
			this.items = paper.setFinish();
			this.items.shape = shape;
			
			ActivityLib.activityHandlersInit(this);
		},
		
		
		gate : function(x, y) {
			if (x == undefined || y == undefined) {
				x = this.items.shape.getBBox().x;
				y = this.items.shape.getBBox().y;
			}

			if (this.items) {
				this.items.remove();
			}
			
			// create activity SVG elements
			paper.setStart();
			var shape = paper.path(Raphael.format('M {0} {1}' + layout.defs.gate, x + 8, y))
							 .attr({
								 'fill' : layout.colors.gate
							 });
			
			paper.text(x + 15, y + 14, 'STOP')
			     .attr({
					'font-size' : 9,
					'font' : 'sans-serif',
					'stroke' : layout.colors.gateText
			     });
			
			this.items = paper.setFinish();
			this.items.shape = shape;
			
			ActivityLib.activityHandlersInit(this);
		},
		
		
		branching : function(x, y) {
			if (x == undefined || y == undefined) {
				// just redraw the activity
				x = this.items.shape.getBBox().x;
				y = this.items.shape.getBBox().y;
			}
			
			if (this.items) {
				this.items.remove();
			}
			
			// create activity SVG elements
			paper.setStart();
			var shape = paper.path(Raphael.format('M {0} {1}'
								                  + (this.isStart ? layout.defs.branchingEdgeStart : layout.defs.branchingEdgeEnd),
								                  x, y + 8))
							 .attr({
								 'fill' : this.isStart ? layout.colors.branchingEdgeStart
							                           : layout.colors.branchingEdgeEnd
							 });
			
			var title = this.branchingActivity.title;
			paper.text(x + 8, y + 27,  title + (this.isStart ? '\nstart'
		                                        		 : '\nend'))
			     .attr({
					'font-size' : 9,
					'font' : 'sans-serif'
			     });
			
			this.items = paper.setFinish();
			this.items.shape = shape;
			
			ActivityLib.activityHandlersInit(this);
		}
	},
	
	
	/**
	 * Methods initialising and refreshing properties dialog for various kinds of activities.
	 */
	loadPropertiesDialogContent : {
		tool : function() {
			var activity = this,
				content = activity.propertiesContent;
			if (!content) {
				// first run, create the content
				content = activity.propertiesContent = $('#propertiesContentTool').clone().attr('id', null)
														.show().data('activity', activity);
				$('.propertiesContentFieldTitle', content).val(activity.title);
				
				$('input, select', content).change(function(){
					// extract changed properties and redraw the activity
					var content = $(this).closest('.dialogContainer'),
						activity = content.data('activity'),
						redrawNeeded = false,
						newTitle =  $('.propertiesContentFieldTitle', content).val();
					if (newTitle != activity.title) {
						activity.title = newTitle;
						redrawNeeded = true;
					}
					var newGroupingValue = $('.propertiesContentFieldGrouping option:selected', content)
										.data('grouping');
					if (newGroupingValue != activity.grouping) {
						activity.grouping = newGroupingValue;
						redrawNeeded = true;
					}
					activity.defineInMonitor = $('.propertiesContentFieldDefineMonitor', content)
										.is(':checked');
					var newOfflineValue = $('.propertiesContentFieldOffline', content)
										.is(':checked');
					if (newOfflineValue != activity.offline) {
						activity.offline = newOfflineValue;
						redrawNeeded = true;
					}
					
					if (redrawNeeded) {
						activity.draw();
					}
				});
			}
			
			ActivityLib.loadPropertiesDialogContent.fillGroupingDropdown(content, activity.grouping);
		},
		
		
		grouping : function() {
			var activity = this,
				content = activity.propertiesContent;

			if (!content) {
				// make onChange function a local variable, because it's used several times
				var changeFunction = function(){
					// extract changed properties and redraw the activity, if needed
					var content = $(this).closest('.dialogContainer'),
						activity = content.data('activity'),
						redrawNeeded = false,
						newTitle = $('.propertiesContentFieldTitle', content).val();
					if (newTitle != activity.title) {
						activity.title = newTitle;
						redrawNeeded = true;
					}
					
					activity.groupingType = $('.propertiesContentFieldGroupingType', content).val();
					
					$('input[name="propertiesContentFieldGroupDivide"]', content).each(function(){
							// enable/disable division types, depending on radio buttons next to them
							$(this).next().find('.spinner').spinner('option', 'disabled', !$(this).is(':checked'));
						})
						// hide group/learner division with some grouping types
						.add($('.propertiesContentFieldLearnerCount', content).closest('tr'))
						.css('display', activity.groupingType == 'monitor' ? 'none' : '');
					
					$('.propertiesContentFieldEqualSizes, .propertiesContentFieldViewLearners', content)
						.closest('tr').css('display', activity.groupingType == 'learner' ? '' : 'none');
					
					activity.groupDivide = activity.groupingType == 'monitor' ||
						$('.propertiesContentFieldGroupCountEnable', content).is(':checked') ?
						'groups' : 'learners';
					$('.propertiesContentFieldNameGroups', content).css('display',
							activity.groupDivide == 'groups' ? '' : 'none');		
					
					activity.groupCount = $('.propertiesContentFieldGroupCount', content).val();
					activity.learnerCount = $('.propertiesContentFieldLearnerCount', content).val();
					activity.equalSizes = $('.propertiesContentFieldEqualSizes', content).is(':checked');
					activity.viewLearners = $('.propertiesContentFieldViewLearners', content).is(':checked');
					
					if (redrawNeeded) {
						activity.draw();
					}
				};
				
				// first run, create the content
				content = activity.propertiesContent = $('#propertiesContentGrouping').clone().attr('id', null)
														.show().data('activity', activity);
				
				// init widgets
				$('.propertiesContentFieldTitle', content).val(activity.title);
				$('.propertiesContentFieldGroupingType', content).val(activity.groupingType);
				if (activity.groupDivide == 'learners') {
					$('.propertiesContentFieldLearnerCountEnable', content).attr('checked', 'checked');
				} else {
					$('.propertiesContentFieldGroupCountEnable', content).attr('checked', 'checked');
				}	
				
				// create groups/learners spinners
				$('.propertiesContentFieldGroupCount', content).spinner({
					'min' : 2,
					'disabled' : activity.groupDivide == 'learners'
				}).spinner('value', activity.groupCount)
				  .on('spinchange', changeFunction);
				
				$('.propertiesContentFieldLearnerCount', content).spinner({
					'min' 	   : 1,
					'disabled' : activity.groupDivide == 'groups'
				}).spinner('value', activity.learnerCount)
				  .on('spinchange', changeFunction);
				
				$('.propertiesContentFieldEqualSizes', content).attr('checked', activity.equalSizes ? 'checked' : null);
				$('.propertiesContentFieldViewLearners', content).attr('checked', activity.viewLearners ? 'checked' : null);
				$('.propertiesContentFieldNameGroups', content).button().click(function(){
					ActivityLib.openGroupNamingDialog(activity);
				});
				
				$('input, select', content).change(changeFunction);
				changeFunction.call(content);
			}
		},
		
		
		gate : function() {
			var activity = this,
				content = activity.propertiesContent;
			if (!content) {
				// first run, create the content
				content = activity.propertiesContent = $('#propertiesContentGate').clone().attr('id', null)
														.show().data('activity', activity);
				$('.propertiesContentFieldTitle', content).val(activity.title);
				$('.propertiesContentFieldGateType', content).val(activity.gateType);

				$('input, select', content).change(function(){
					// extract changed properties and redraw the activity
					var content = $(this).closest('.dialogContainer'),
						activity = content.data('activity'),
						redrawNeeded = false,
						newTitle = $('.propertiesContentFieldTitle', content).val();
					if (newTitle != activity.title) {
						activity.title = newTitle;
						redrawNeeded = true;
					}
					activity.gateType = $('.propertiesContentFieldGateType', content).val();
					
					if (redrawNeeded) {
						activity.draw();
					}
				});
			}
		},
		
		
		branching : function() {
			var activity = this,
			content = activity.propertiesContent;
			
			var fillWidgetsFunction = function(){
				$('.propertiesContentFieldTitle', content).val(activity.branchingActivity.title);
				$('.propertiesContentFieldBranchingType', content).val(activity.branchingActivity.branchingType);
				ActivityLib.loadPropertiesDialogContent.fillGroupingDropdown(content, activity.branchingActivity.grouping);
				ActivityLib.loadPropertiesDialogContent.fillToolInputDropdown(content, activity.branchingActivity.input);
			}
			
			if (!content) {
				// first run, create the content
				content = activity.propertiesContent = $('#propertiesContentBranching').clone().attr('id', null)
														.show().data('activity', activity);
				$('.propertiesContentFieldMatchGroups', content).button();
				
				var changeFunction = function(){
					// extract changed properties and redraw the activity
					var content = $(this).closest('.dialogContainer'),
						activity = content.data('activity'),
						branchingActivity = activity.branchingActivity,
						redrawNeeded = false,
						newTitle = $('.propertiesContentFieldTitle', content).val();
					if (newTitle != branchingActivity.title) {
						branchingActivity.title = newTitle;
						redrawNeeded = true;
					}
					branchingActivity.branchingType = $('.propertiesContentFieldBranchingType', content).val();
					
					var groupingRow = $('.propertiesContentFieldGrouping', content).closest('tr');
					if (branchingActivity.branchingType == 'group') {
						branchingActivity.grouping = groupingRow.show()
													.find('option:selected').data('grouping');
					} else {
						groupingRow.hide();
						branchingActivity.grouping = null;
					}
					$('.propertiesContentFieldMatchGroups', content).closest('tr')
						.css('display', branchingActivity.grouping ? '' : 'none');
					
					var inputRow = $('.propertiesContentFieldInput', content).closest('tr');
					if (branchingActivity.branchingType == 'tool') {
						branchingActivity.input = inputRow.show()
							.find('option:selected').data('activity');
					} else {
						inputRow.hide();
						branchingActivity.input = null;
					}

					if (redrawNeeded) {
						branchingActivity.start.draw();
						branchingActivity.end.draw();
					}
				}
				
				$('input, select', content).change(changeFunction);
				fillWidgetsFunction();
				changeFunction.call(content);
			}
			
			fillWidgetsFunction();
		},
		
		
		/**
		 * 	Find all groupings on canvas and fill dropdown menu with their titles
		 */
		fillGroupingDropdown : function(content, grouping) {
			// find all groupings on canvas and fill dropdown menu with their titles
			var emptyOption = $('<option />'),
				groupingDropdown = $('.propertiesContentFieldGrouping', content).empty().append(emptyOption);
			$.each(layout.activities, function(){
				if (this.type == 'grouping') {
					var option = $('<option />').text(this.title)
												.appendTo(groupingDropdown)
												// store reference to grouping object
												.data('grouping', this);
					if (this == grouping) {
						option.attr('selected', 'selected');
					}
				}
			});
			if (!grouping) {
				// no grouping selected
				emptyOption.attr('selected', 'selected');
			}
		},
		
		
		fillToolInputDropdown : function(content, input) {
			// find all tools that support input and fill dropdown menu with their titles
			var emptyOption = $('<option />'),
				inputDropdown = $('.propertiesContentFieldInput', content).empty().append(emptyOption);
			$.each(layout.activities, function(){
				if (this.supportsOutputs) {
					var option = $('<option />').text(this.title)
												.appendTo(inputDropdown)
												// store reference to grouping object
												.data('activity', this);
					if (this == input) {
						option.attr('selected', 'selected');
					}
				}
			});
			if (!input) {
				// no grouping selected
				emptyOption.attr('selected', 'selected');
			}
		}
	},
	
	
	/**
	 * Make a new activity fully functional on canvas.
	 */
	activityHandlersInit : function(activity) {
		// set all the handlers
		activity.items
			.data('activity', activity)
			.mousedown(HandlerLib.activityMousedownHandler)
			.touchmove(HandlerLib.dragItemsMoveHandler)
			.click(HandlerLib.activityClickHandler)
			.dblclick(HandlerLib.activityDblclickHandler)
			.attr({
				'cursor' : 'pointer'
			});
			
			if (activity.type == 'branchingEdge' && activity.branchingActivity.end) {
				// highligh branching edges on hover
				activity.branchingActivity.start.items.hover(HandlerLib.branchingEdgeMouseoverHandler,
						HandlerLib.branchingEdgeMouseoutHandler);
				activity.branchingActivity.end.items.hover(HandlerLib.branchingEdgeMouseoverHandler,
						HandlerLib.branchingEdgeMouseoutHandler);
			}
		},

	
	/**
	 * Deletes the given activity.
	 */
	removeActivity : function(activity, forceRemove) {
		if (!forceRemove && activity.type == 'branchingEdge'){
			// user removes one of the branching edges, so remove the whole activity
			if (confirm('Are you sure you want to remove the whole branching activity?')){
				var otherEdge = activity.isStart ? activity.branchingActivity.end
						                         : activity.branchingActivity.start;
				ActivityLib.removeActivity(otherEdge, true);
			} else {
				return;
			}
		}
		
		// remove the transitions
		// need to use slice() to copy the array as it gets modified in removeTransition()
		$.each(activity.transitions.from.slice(), function() {
			ActivityLib.removeTransition(this);
		});
		$.each(activity.transitions.to.slice(), function() {
			ActivityLib.removeTransition(this);
		});
		
		// remove the activity from reference tables
		layout.activities.splice(layout.activities.indexOf(activity), 1);
		if (layout.items.copiedActivity = activity) {
			layout.items.copiedActivity = null;
		}
		if (activity.type == 'grouping') {
			$.each(layout.activities, function(){
				if (activity == this.grouping) {
					this.grouping = null;
					this.draw();
				}
			});
		}
		
		// visually remove the activity
		activity.items.remove();
	},
	
	
	/**
	 * Draws a transition between two activities.
	 */
	drawTransition : function(fromActivity, toActivity, redraw) {
		// only converge points are allowed to have few inbound transitions
		if (!redraw
				&& toActivity.transitions.to.length > 0
				&& !(toActivity.type == 'branchingEdge' && !toActivity.isStart)) {
			alert('Transition to this activity already exists');
			return;
		}

		// user chose to create outbound transition from an activity that already has one
		if (!redraw
				&& fromActivity.transitions.from.length > 0
				&& !(fromActivity.type == 'branchingEdge' && fromActivity.isStart)
				&& !(toActivity.type == 'branchingEdge'   && toActivity.isStart)) {
			if (confirm('Transition from this activity already exists.\n' +
					    'Do you want to create branching here?')) {
				ActivityLib.addBranching(fromActivity, toActivity);
			}
			return;
		}
		
		// remove the existing transition
		$.each(fromActivity.transitions.from, function(index) {
			if (this.toActivity == toActivity) {
				ActivityLib.removeTransition(this);
				return false;
			}
		});
		
		// calculate middle points of each activity
		var points = ActivityLib.findTransitionPoints(fromActivity, toActivity);
		
		// do the actual drawing
		paper.setStart();
		paper.path(Raphael.format('M {0} {1} L {2} {3}', points.startX, points.startY, points.endX, points.endY))
		                  .attr({
		                 	'stroke'       : layout.colors.transition,
		                	'stroke-width' : 2
		                  });

		// draw the arrow and turn it in the same direction as the line
		var angle = 90 + Math.atan2(points.endY - points.startY, points.endX - points.startX) * 180 / Math.PI;
		var arrowPath = Raphael.transformPath(Raphael.format('M {0} {1}' + layout.defs.transArrow, points.middleX, points.middleY), 
				                              Raphael.format('R {0} {1} {2}', angle, points.middleX, points.middleY));
		paper.path(arrowPath)
						 .attr({
							'stroke' : layout.colors.transition,
							'fill'   : layout.colors.transition
						 });
		var transition = paper.setFinish();
		transition.data('transition', transition);
		
		// set up references in activities and the transition
		fromActivity.transitions.from.push(transition);
		toActivity.transitions.to.push(transition);
		transition.fromActivity = fromActivity;
		transition.toActivity = toActivity;
		transition.toBack();
		transition.mousedown(HandlerLib.transitionMousedownHandler);
		
		if (!redraw && fromActivity.type == 'branchingEdge' && fromActivity.isStart
				&& (toActivity.type != 'branchingEdge' || fromActivity.branchingActivity != toActivity.branchingActivity)) {
			// create a new branch
			var branch = new ActivityLib.BranchActivity(null, null, fromActivity.branchingActivity, transition);
			fromActivity.branchingActivity.branches.push(branch);
			transition.data('branch', branch);
		}
		
		return transition;
	},
	
	
	/**
	 * Removes the given transition.
	 */
	removeTransition : function(transition) {
		// find the transition and remove it
		var transitions = transition.fromActivity.transitions.from;
		transitions.splice(transitions.indexOf(transition), 1);
		transitions = transition.toActivity.transitions.to;
		transitions.splice(transitions.indexOf(transition), 1);
		
		var branch = transition[0].data('branch');
		if (branch) {
			var branches = branch.branchingActivity.branches;
			branches.splice(branches.indexOf(branch), 1);
		}
		
		transition.remove();
	},
	
	
	/**
	 * Adds branching activity when user draws an extra outbout transition from.
	 */
	addBranching : function(fromActivity, toActivity1) {
		// find the other toActivity
		var existingTransition = fromActivity.transitions.from[0],
			toActivity2 = existingTransition.toActivity,
			branchingEdgeStart = null,
			branchingEdgeEnd = null,
			convergeActivity1 = toActivity1,
		    convergeActivity2 = toActivity2;
		// find converge activity of the new branch
		while (convergeActivity1.transitions.from.length > 0) {
			convergeActivity1 = convergeActivity1.transitions.from[0].toActivity;
		};
		
		if (toActivity2.type == 'branchingEdge' && toActivity2.isStart) {
			// there is already a branching activity, reuse existing items
			branchingEdgeStart = toActivity2;
			branchingEdgeEnd = toActivity2.branchingActivity.end;
		} else {
			// add new branching
			ActivityLib.removeTransition(existingTransition);
			
			// calculate position of branching point
			var branchPoints1 = ActivityLib.findTransitionPoints(fromActivity, toActivity1),
			    branchPoints2 = ActivityLib.findTransitionPoints(fromActivity, toActivity2),
			    branchEdgeStartX = branchPoints1.middleX + (branchPoints2.middleX - branchPoints1.middleX)/2,
			    branchEdgeStartY = branchPoints1.middleY + (branchPoints2.middleY - branchPoints1.middleY)/2,
			    branchingEdgeStart = new ActivityLib.BranchingEdgeActivity(null, branchEdgeStartX,
			    		branchEdgeStartY, null, null, null);
			layout.activities.push(branchingEdgeStart);
			
			// find last activities in subsequences and make an converge point between them
			while (convergeActivity2.transitions.from.length > 0) {
				convergeActivity2 = convergeActivity2.transitions.from[0].toActivity;
			};

			var convergePoints = ActivityLib.findTransitionPoints(convergeActivity1, convergeActivity2),
				branchingEdgeEnd = new ActivityLib.BranchingEdgeActivity(null, convergePoints.middleX,
					convergePoints.middleY, null, null, branchingEdgeStart.branchingActivity);
			layout.activities.push(branchingEdgeEnd);
			
			// draw all required transitions
			ActivityLib.drawTransition(fromActivity, branchingEdgeStart);
			ActivityLib.drawTransition(branchingEdgeStart, toActivity2);
			ActivityLib.drawTransition(convergeActivity2, branchingEdgeEnd);
		}

		ActivityLib.drawTransition(branchingEdgeStart, toActivity1);
		ActivityLib.drawTransition(convergeActivity1, branchingEdgeEnd);
	},
	
	
	/**
	 * Draws an extra border around the selected activity.
	 */
	addSelectEffect : function (activity) {
		// do not draw twice
		if (!activity.items.selectEffect) {
			var box = activity.items.getBBox();
			
			// a simple rectange a bit wider than the actual activity boundaries
			activity.items.selectEffect = paper.rect(
					box.x - 7,
					box.y - 7,
					box.width + 14,
					box.height + 14)
				.attr({
					'stroke'           : layout.colors.selectEffect,
					'stroke-dasharray' : '-'
				});
			activity.items.push(activity.items.selectEffect);
			layout.items.selectedActivity = activity;
			
			// show the properties dialog for the selected activity
			if (activity.loadPropertiesDialogContent) {
				activity.loadPropertiesDialogContent();
				var dialog = layout.items.propertiesDialog;
				dialog.children().detach();
				dialog.append(activity.propertiesContent);
				dialog.dialog('open');
				dialog.find('input').blur();
			}
		}
	},
	
	
	removeSelectEffect : function() {
		var selectedActivity = layout.items.selectedActivity;
		// does selection exist at all?
		if (selectedActivity) {
			if (selectedActivity.items.selectEffect) {
				selectedActivity.items.selectEffect.remove();
				selectedActivity.items.selectEffect = null;
			}
			
			// no selected activity = no properties dialog
			layout.items.propertiesDialog.dialog('close');
			layout.items.selectedActivity = null;
		}
	},
	
	
	/**
	 * Adds visual grouping effect on an activity.
	 */
	addGroupingEffect : function(activity) {
		if (!activity.items.groupingEffect) {
			var shape = activity.items.shape,
				activityBox = shape.getBBox();
			
			activity.items.groupingEffect = paper.rect(
					activityBox.x + 5,
					activityBox.y + 5,
					activityBox.width,
					activityBox.height)
				.attr({
					'fill' : shape.attr('fill')
				})
				.toBack();
			activity.items.push(activity.items.groupingEffect);
			
			// this is needed, for some reason, otherwise the activity can not be selected
			HandlerLib.resetCanvasMode();
		}
	},
	
	
	/**
	 * Drop the dragged activity on the canvas.
	 */
	dropActivity : function(activity) {
		// finally transform the dragged elements
		var transformation = activity.items.shape.attr('transform');
		activity.items.transform('');
		if (transformation.length > 0) {
			// find new X and Y and redraw the activity
			var activityBox =  activity.items.shape.getBBox();
			activity.draw(activityBox.x + transformation[0][1],
						  activityBox.y + transformation[0][2]);
		}

		// redraw transitions
		$.each(activity.transitions.from.slice(), function(){
			ActivityLib.drawTransition(activity, this.toActivity, true);
		});
		$.each(activity.transitions.to.slice(), function(){
			ActivityLib.drawTransition(this.fromActivity, activity, true);
		});
	},
	
	
	/**
	 * Open separate window with activity authoring on double click.
	 */
	openActivityAuthoring : function(activity){
		// fetch authoring URL for a Tool Activity
		if (!activity.authorURL && activity.toolID) {
			$.ajax({
				async : false,
				cache : false,
				url : LAMS_URL + "authoring/author.do",
				dataType : 'json',
				data : {
					'method'          : 'createToolContent',
					'toolID'          : activity.toolID,
					'contentFolderID' : contentFolderID
				},
				success : function(response) {
					activity.authorURL = response.authorURL;
					activity.id = response.toolContentID;
					if (!contentFolderID) {
						// if LD did not have contentFolderID, it was just generated
						// so remember it
						contentFolderID = response.contentFolderID;
					}
				}
			});
		}
		
		if (activity.authorURL) {
			window.open(activity.authorURL, 'activityAuthoring' + activity.id,
					"HEIGHT=800,WIDTH=1024,resizable=yes,scrollbars=yes,status=false," +
					"menubar=no,toolbar=no");
		}
	},
	
	
	/**
	 * Drop the dragged transition.
	 */
	dropTransition : function(transition) {
		// if the transition was over rubbish bin, remove it
		transition.transform('');
		transition.toBack();
	},
	
	
	/**
	 * Calculates start, middle and end points of a line between two activities. 
	 */
	findTransitionPoints : function(fromActivity, toActivity) {
		var fromActivityBox = fromActivity.items.shape.getBBox(),
			toActivityBox = toActivity.items.shape.getBBox(),
			
			// find points in the middle of each activity
			points = {
				'startX'  : fromActivityBox.x + fromActivityBox.width / 2,
				'startY'  : fromActivityBox.y + fromActivityBox.height / 2,
				'endX'    : toActivityBox.x + toActivityBox.width / 2,
				'endY'    : toActivityBox.y + toActivityBox.height / 2
			},

			// find intersection points of the temporary transition
			tempTransition = Raphael.parsePathString(Raphael.format(
				'M {0} {1} L {2} {3}', points.startX, points.startY, points.endX, points.endY)),
			fromIntersect = Raphael.pathIntersection(tempTransition, fromActivity.items.shape.attr('path')),
			toIntersect = Raphael.pathIntersection(tempTransition, toActivity.items.shape.attr('path'));
		
		// find points on borders of activities, if they exist
		if (fromIntersect.length > 0) {
			points.startX = fromIntersect[0].x;
			points.startY = fromIntersect[0].y;
		}
		if (toIntersect.length > 0) {
			points.endX = toIntersect[0].x;
			points.endY = toIntersect[0].y;
		}
		// middle point of the transition
		points.middleX = points.startX + (points.endX - points.startX)/2;
		points.middleY = points.startY + (points.endY - points.startY)/2;
		
		return points;
	},
	
	
	/**
	 * Crawles through branches setting their lengths and finding the longest one.
	 */
	updateBranchesLength : function(branchingActivity) {
		var longestBranchLength = 0;
		$.each(branchingActivity.branches, function(){
			// include the first activity
			var branchLength = 1,
				activity = this.transitionFrom.toActivity;
			while (activity.transitions.from.length > 0) {
				activity = activity.transitions.from[0].toActivity;
				// check if reached the end of branch
				if (activity.type == 'branchingEdge') {
					break;
				} else {
					branchLength++;
				}
			};
			this.branchLength = branchLength;
			if (branchLength > longestBranchLength) {
				longestBranchLength = branchLength;
			}
		});
		
		branchingActivity.longestBranchLength = longestBranchLength;
	},
	
	
	/**
	 * Reduce length of activity's title so it fits in its SVG shape.
	 */
	shortenActivityTitle : function(title) {
		if (title.length > 18) {
			title = title.substring(0, 17) + '...';
		}
		return title;
	},
	
	
	/**
	 * Get real coordinates on paper, based on event coordinates.
	 */
	translateEventOnCanvas : function(event) {
		return [event.pageX + canvas.scrollLeft() - canvas.offset().left,
		        event.pageY + canvas.scrollTop()  - canvas.offset().top];
	},
	
	
	/**
	 * Fills group naming dialog with existing group names and opens it.
	 */
	openGroupNamingDialog : function(activity) {
		var dialog = layout.items.groupNamingDialog;
		// remove existing entries and add reference to the initiating activity
		dialog.empty().dialog('option', 'activity', activity);
		
		for (var groupIndex = 1; groupIndex <= activity.groupCount; groupIndex++) {
			// generate group names if they do not exist
			var groupName = groupIndex > activity.groupNames.length ?
					'Group ' + groupIndex : activity.groupNames[groupIndex - 1];
			$('<input type="text" />').addClass('groupName').appendTo(dialog).val(groupName);
			dialog.append('<br />');
		}
		
		dialog.dialog('open');
	}
};