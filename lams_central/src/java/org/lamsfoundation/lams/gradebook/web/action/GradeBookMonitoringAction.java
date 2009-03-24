/****************************************************************
 * Copyright (C) 2008 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $Id$ */
package org.lamsfoundation.lams.gradebook.web.action;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.gradebook.dto.GBUserGridRowDTO;
import org.lamsfoundation.lams.gradebook.dto.GradeBookGridRowDTO;
import org.lamsfoundation.lams.gradebook.dto.comparators.GBUserFullNameComparator;
import org.lamsfoundation.lams.gradebook.dto.comparators.GBUserMarkComparator;
import org.lamsfoundation.lams.gradebook.service.IGradeBookService;
import org.lamsfoundation.lams.gradebook.util.GradeBookConstants;
import org.lamsfoundation.lams.gradebook.util.GradeBookUtil;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.dto.LessonDetailsDTO;
import org.lamsfoundation.lams.lesson.service.ILessonService;
import org.lamsfoundation.lams.monitoring.service.IMonitoringService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author lfoxton
 * 
 * Handles the monitor interface for gradebook
 * 
 * This is where marking for an activity/lesson takes place
 * 
 * 
 * @struts.action path="/gradebook/gradebookMonitoring" parameter="dispatch"
 *                scope="request" name="monitoringForm" validate="false"
 * 
 * @struts:action-forward name="monitorgradebook"
 *                        path="/gradebook/gradeBookMonitor.jsp"
 * @struts:action-forward name="error" path=".error"
 * @struts:action-forward name="message" path=".message"
 */
public class GradeBookMonitoringAction extends LamsDispatchAction {

    private static Logger logger = Logger.getLogger(GradeBookMonitoringAction.class);

    private static IGradeBookService gradeBookService;
    private static IUserManagementService userService;
    private static ILessonService lessonService;
    private static IMonitoringService monitoringService;

    @SuppressWarnings("unchecked")
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	try {
	    initServices();
	    Long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	    logger.debug("request monitorGradebook for lesson: " + lessonId.toString());
	    UserDTO user = getUser();
	    if (user == null) {
		logger.error("User missing from session. ");
		return mapping.findForward("error");
	    } else {
		Lesson lesson = lessonId != null ? getLessonService().getLesson(lessonId) : null;
		if (lesson == null) {
		    logger.error("Lesson " + lessonId + " does not exist. Unable to monitor lesson");
		    return mapping.findForward("error");
		}

		if (lesson.getLessonClass() == null || !lesson.getLessonClass().isStaffMember(getRealUser(user))) {
		    logger.error("User " + user.getLogin()
			    + " is not a monitor in the requested lesson. Cannot access the lesson for monitor.");
		    return displayMessage(mapping, request, "error.authorisation");
		}

		logger.debug("user is staff");

		LessonDetailsDTO lessonDetatilsDTO = lesson.getLessonDetails();
		request.setAttribute("lessonDetails", lessonDetatilsDTO);

		return mapping.findForward("monitorgradebook");
	    }
	} catch (Exception e) {
	    logger.error("Failed to load monitor lesson", e);
	    return mapping.findForward("error");
	}
    }

    /**
     * Returns an xml representation of the entire lesson's gradebook data. It
     * is essentially a list of users and their marks.
     * 
     * This will fill the top grid area of monitor gradebook, when a user row is
     * clicked then the activity-specific data will be loaded
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ActionForward getGradeBookUserRows(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	initServices();
	int page = WebUtil.readIntParam(request, GradeBookConstants.PARAM_PAGE);
	int rowLimit = WebUtil.readIntParam(request, GradeBookConstants.PARAM_ROWS);
	String sortOrder = WebUtil.readStrParam(request, GradeBookConstants.PARAM_SORD);
	String sortBy = WebUtil.readStrParam(request, GradeBookConstants.PARAM_SIDX, true);
	Long lessonID = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	
	String method = WebUtil.readStrParam(request, "method");

	Lesson lesson = lessonService.getLesson(lessonID);

	if (lesson != null) {
	    
	    // Get the user gradebook list from the db
	    List<GBUserGridRowDTO> gradeBookUserDTOs = new ArrayList<GBUserGridRowDTO>();
	    
	    if (method.equals("userView")) {
		gradeBookUserDTOs = gradeBookService.getGradeBookLessonData(lesson);
	    } else if (method.equals("activityView")) {
		Long activityID = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
		
		Activity activity = monitoringService.getActivityById(activityID);
		if(activity != null) {
		    gradeBookUserDTOs = gradeBookService.getUserGradeBookActivityDTOs(lesson, activity);
		} else {
		    // TODO: handle error
		}
	    }

	    // Sort the list appropriately
	    if (sortBy != null) {
		if (sortBy.equals("fullName")) {
		    Collections.sort(gradeBookUserDTOs, new GBUserFullNameComparator());
		} else if (sortBy.equals("mark")) {
		    Collections.sort(gradeBookUserDTOs, new GBUserMarkComparator());
		} else {
		    Collections.sort(gradeBookUserDTOs, new GBUserFullNameComparator());
		}
	    } else {
		Collections.sort(gradeBookUserDTOs, new GBUserFullNameComparator());
	    }

	    // Reverse the order if requested
	    if (sortOrder != null && sortOrder.equals("desc")) {
		Collections.reverse(gradeBookUserDTOs);
	    }

	    // Work out the sublist to fetch based on rowlimit and current page.
	    int totalPages = 1;
	    if (rowLimit < gradeBookUserDTOs.size()) {

		totalPages = new Double(Math.ceil(new Integer(gradeBookUserDTOs.size()).doubleValue()
			/ new Integer(rowLimit).doubleValue())).intValue();
		int firstRow = (page - 1) * rowLimit;
		int lastRow = firstRow + rowLimit;

		if (lastRow > gradeBookUserDTOs.size()) {
		    gradeBookUserDTOs = gradeBookUserDTOs.subList(firstRow, gradeBookUserDTOs.size());
		} else {
		    gradeBookUserDTOs = gradeBookUserDTOs.subList(firstRow, lastRow);
		}

	    }

	    String ret = "";
	    
	    if (method.equals("userView")) {
		ret = GradeBookUtil.toGridXML(gradeBookUserDTOs, page, totalPages, GradeBookUtil.GRID_TYPE_MONITOR_USER_VIEW); 
	    } else if (method.equals("activityView")) {
		ret = GradeBookUtil.toGridXML(gradeBookUserDTOs, page, totalPages, GradeBookUtil.GRID_TYPE_MONITOR_ACTIVITY_VIEW);
	    }
	    
	    response.setContentType("text/xml");
	    PrintWriter out = response.getWriter();
	    out.print(ret);
	} else {
	    // TODO: handle error
	}

	return null;
    }
    
    
    /**
     * Returns an xml representation of the entire lesson's gradebook data. It
     * is essentially a list of users and their marks.
     * 
     * This will fill the top grid area of monitor gradebook, when a user row is
     * clicked then the activity-specific data will be loaded
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ActionForward getActivityViewLessonGradeBookData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	initServices();
	int page = WebUtil.readIntParam(request, GradeBookConstants.PARAM_PAGE);
	int rowLimit = WebUtil.readIntParam(request, GradeBookConstants.PARAM_ROWS);
	String sortOrder = WebUtil.readStrParam(request, GradeBookConstants.PARAM_SORD);
	String sortBy = WebUtil.readStrParam(request, GradeBookConstants.PARAM_SIDX, true);
	Long lessonID = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	String method = WebUtil.readStrParam(request, "method");

	Lesson lesson = lessonService.getLesson(lessonID);

	if (lesson != null) {
	    
	    List<GradeBookGridRowDTO> gradeBookActivityDTOs = new ArrayList<GradeBookGridRowDTO>(); 
	    
	    // Get the user gradebook list from the db
	    // A slightly different list is needed for userview or activity view
	    if (method.equals("userView")) {
		String login = WebUtil.readStrParam(request, GradeBookConstants.PARAM_LOGIN);
		User learner = userService.getUserByLogin(login);
		if (learner != null) {
		    gradeBookActivityDTOs = gradeBookService.getUserGradeBookActivityDTOs(lesson, learner);
		} else {
		    // TODO: handle error
		}
	    } else if (method.equals("activityView")) {
		gradeBookActivityDTOs = gradeBookService.getActivityGradeBookUserDTOs(lesson);
	    }
	    
	    
	    // Work out the sublist to fetch based on rowlimit and current page.
	    int totalPages = 1;
	    if (rowLimit < gradeBookActivityDTOs.size()) {

		totalPages = new Double(Math.ceil(new Integer(gradeBookActivityDTOs.size()).doubleValue()
			/ new Integer(rowLimit).doubleValue())).intValue();
		int firstRow = (page - 1) * rowLimit;
		int lastRow = firstRow + rowLimit;

		if (lastRow > gradeBookActivityDTOs.size()) {
		    gradeBookActivityDTOs = gradeBookActivityDTOs.subList(firstRow, gradeBookActivityDTOs.size());
		} else {
		    gradeBookActivityDTOs = gradeBookActivityDTOs.subList(firstRow, lastRow);
		}

	    }
	    
	    String ret = "";
	    if (method.equals("userView")) {
		ret = GradeBookUtil.toGridXML(gradeBookActivityDTOs, page, rowLimit, GradeBookUtil.GRID_TYPE_MONITOR_USER_VIEW);
	    } else if (method.equals("activityView")){
		ret = GradeBookUtil.toGridXML(gradeBookActivityDTOs, page, totalPages, GradeBookUtil.GRID_TYPE_MONITOR_ACTIVITY_VIEW);
	    }
	    
	    response.setContentType("text/xml");
	    PrintWriter out = response.getWriter();
	    out.print(ret);
	} else {
	    // TODO: handle error
	}

	return null;
    }
    

    /**
     * Updates a user's mark directly for an entire lesson
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward updateUserLessonGradeBookData(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) throws Exception {
	initServices();
	Long lessonID = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	String login = WebUtil.readStrParam(request, "id");
	String markStr = WebUtil.readStrParam(request, "mark", true);
	String feedback =  WebUtil.readStrParam(request, "feedback", true);
	Lesson lesson = lessonService.getLesson(lessonID);
	User learner = userService.getUserByLogin(login);

	if (lesson != null && learner != null) {
	    
	    if (markStr != null && !markStr.equals("")) {
		Double mark = Double.parseDouble(markStr);
		gradeBookService.updateUserLessonGradeBookMark(lesson, learner, mark);
	    }
	    
	    if (feedback != null && !feedback.equals("")) {
		gradeBookService.updateUserLessonGradeBookFeedback(lesson, learner, feedback);
	    }

	} else {
	    // TODO: handle error
	}
	return null;
    }

    /**
     * Updates a user's mark for an activity, then aggregates their total lesson
     * mark
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward updateUserActivityGradeBookData(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) throws Exception {
	initServices();
	Long lessonID = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
	
	String method = WebUtil.readStrParam(request, "method");
	
	
	Long activityID = null;
	String login = null;
	
	// Fetch the id based on which grid it came from
	if (method.equals("activityView")) {
	    activityID = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
	    login = WebUtil.readStrParam(request, "id");
	} else if (method.equals("userView")) {
	    activityID = WebUtil.readLongParam(request, "id");
	    login = WebUtil.readStrParam(request, GradeBookConstants.PARAM_LOGIN);
	}

	String markStr = WebUtil.readStrParam(request, "mark", true);
	String feedback =  WebUtil.readStrParam(request, "feedback", true);

	Activity activity = monitoringService.getActivityById(activityID);
	User learner = userService.getUserByLogin(login);
	Lesson lesson = lessonService.getLesson(lessonID);

	if (lesson != null && activity != null && learner != null && activity.isToolActivity()) {
	    
	    if (markStr != null && !markStr.equals("")) {
		Double mark = Double.parseDouble(markStr);
		gradeBookService.updateUserActivityGradeBookMark(lesson, learner, activity, mark);
	    }
	    
	    if (feedback != null && !feedback.equals("")) {
		gradeBookService.updateUserActivityGradeBookFeedback(activity, learner, feedback);
	    }
	    
	} else {
	    // TODO: handle error
	}
	return null;
    }
    
    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
//    public ActionForward getUserGradeBookActivitiesForActivityView(ActionMapping mapping, ActionForm form,
//	    HttpServletRequest request, HttpServletResponse response) throws Exception {
//	initServices();
//	int page = WebUtil.readIntParam(request, GradeBookConstants.PARAM_PAGE);
//	int rowLimit = WebUtil.readIntParam(request, GradeBookConstants.PARAM_ROWS);
//	String sortOrder = WebUtil.readStrParam(request, GradeBookConstants.PARAM_SORD);
//	String sortBy = WebUtil.readStrParam(request, GradeBookConstants.PARAM_SIDX, true);
//	Long lessonID = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
//	Long activityID = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
//
//	Lesson lesson = lessonService.getLesson(lessonID);
//	Activity activity = monitoringService.getActivityById(activityID);
//
//	if (lesson != null && activity != null) {
//	    
//	    List<GradeBookGridRowDTO> gradeBookActivityDTOs = gradeBookService.getUserGradeBookActivityDTOs(lesson, activity);
//	    String ret = GradeBookUtil.toGridXML(gradeBookActivityDTOs, page, rowLimit, GradeBookUtil.GRID_TYPE_MONITOR_ACTIVITY_VIEW);
//
//	    response.setContentType("text/xml");
//	    PrintWriter out = response.getWriter();
//	    out.print(ret);
//
//	} else {
//	    // Handle error
//	}
//
//	return null;
//    }

    private UserDTO getUser() {
	HttpSession ss = SessionManager.getSession();
	return (UserDTO) ss.getAttribute(AttributeNames.USER);
    }

    private User getRealUser(UserDTO dto) {
	return getUserService().getUserByLogin(dto.getLogin());
    }

    public static IGradeBookService getGradeBookService() {
	return gradeBookService;
    }

    public static IUserManagementService getUserService() {
	return userService;
    }

    public static ILessonService getLessonService() {
	return lessonService;
    }

    private ActionForward displayMessage(ActionMapping mapping, HttpServletRequest req, String messageKey) {
	req.setAttribute("messageKey", messageKey);
	return mapping.findForward("message");
    }

    private void initServices() {
	ServletContext context = this.getServlet().getServletContext();

	if (gradeBookService == null)
	    gradeBookService = (IGradeBookService) WebApplicationContextUtils.getRequiredWebApplicationContext(context)
		    .getBean("gradeBookService");

	if (userService == null)
	    userService = (IUserManagementService) WebApplicationContextUtils.getRequiredWebApplicationContext(context)
		    .getBean("userManagementService");

	if (lessonService == null)
	    lessonService = (ILessonService) WebApplicationContextUtils.getRequiredWebApplicationContext(context)
		    .getBean("lessonService");

	if (monitoringService == null)
	    monitoringService = (IMonitoringService) WebApplicationContextUtils.getRequiredWebApplicationContext(
		    context).getBean("monitoringService");

    }
}
