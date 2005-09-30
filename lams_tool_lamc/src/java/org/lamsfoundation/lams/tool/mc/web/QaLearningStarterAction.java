/**
 * Created on 8/03/2005
 * initializes the tool's learning mode
 */

package org.lamsfoundation.lams.tool.qa.web;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.qa.QaAppConstants;
import org.lamsfoundation.lams.tool.qa.QaApplicationException;
import org.lamsfoundation.lams.tool.qa.QaComparator;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.QaQueUsr;
import org.lamsfoundation.lams.tool.qa.QaSession;
import org.lamsfoundation.lams.tool.qa.QaUtils;
import org.lamsfoundation.lams.tool.qa.service.IQaService;
import org.lamsfoundation.lams.tool.qa.service.QaServiceProxy;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
/**
 * TO DO:
 * remove line:
 * Long currentToolContentId= new Long(4321); 
 */

/**
 * 
 * @author Ozgur Demirtas
 *
 * <lams base path>/<tool's learner url>&userId=<learners user id>&toolSessionId=123&mode=teacher
 * 
 * Since the toolSessionId is passed, we will derive toolContentId from the toolSessionId
 *
 * This class is used to load the default content and initialize the presentation Map for Learner mode 
 * 
 * createToolSession will not be called once the tool is deployed.
 * 
 * It is important that ALL the session attributes created in this action gets removed by: QaUtils.cleanupSession(request) 
 * 
 */

/**
 * Tool Session:
 *
 * A tool session is the concept by which which the tool and the LAMS core manage a set of learners interacting with the tool. 
 * The tool session id (toolSessionId) is generated by the LAMS core and given to the tool.
 * A tool session represents the use of a tool for a particulate activity for a group of learners. 
 * So if an activity is ungrouped, then one tool session exist for for a tool activity in a learning design.
 *
 * More details on the tool session id are covered under monitoring.
 * When thinking about the tool content id and the tool session id, it might be helpful to think about the tool content id 
 * relating to the definition of an activity, whereas the tool session id relates to the runtime participation in the activity.
 * 
 */

/**
 * 
 * Learner URL:
 * The learner url display the screen(s) that the learner uses to participate in the activity. 
 * When the learner accessed this user, it will have a tool access mode ToolAccessMode.LEARNER.
 *
 * It is the responsibility of the tool to record the progress of the user. 
 * If the tool is a multistage tool, for example asking a series of questions, the tool must keep track of what the learner has already done. 
 * If the user logs out and comes back to the tool later, then the tool should resume from where the learner stopped.
 * When the user is completed with tool, then the tool notifies the progress engine by calling 
 * org.lamsfoundation.lams.learning.service.completeToolSession(Long toolSessionId, User learner).
 *
 * If the tool's content DefineLater flag is set to true, then the learner should see a "Please wait for the teacher to define this part...." 
 * style message.
 * If the tool's content RunOffline flag is set to true, then the learner should see a "This activity is not being done on the computer. 
 * Please see your instructor for details."
 *
 * ?? Would it be better to define a run offline message in the tool? We have instructions for the teacher but not the learner. ??
 * If the tool has a LockOnFinish flag, then the tool should lock learner's entries once they have completed the activity. 
 * If they return to the activity (e.g. via the progress bar) then the entries should be read only.
 *
 */

/**
 * 
 * verifies that the content id passed to the tool is numeric and does refer to an existing content.
 */

public class QaLearningStarterAction extends Action implements QaAppConstants {
	static Logger logger = Logger.getLogger(QaLearningStarterAction.class.getName());

	/**
	 * holds the question contents for a given tool session and relevant content
	 */
	protected Map mapQuestions= new TreeMap(new QaComparator());
	/**
	 * holds the answers
	 */  
	protected Map mapAnswers= new TreeMap(new QaComparator());
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
  								throws IOException, ServletException, QaApplicationException {

		QaLearningForm qaQaLearningForm = (QaLearningForm) form;
		
		/**
		 * reset the question index to 1. 
		 */
		request.getSession().setAttribute(CURRENT_QUESTION_INDEX, "1");
		logger.debug("CURRENT_QUESTION_INDEX: " + request.getSession().getAttribute(CURRENT_QUESTION_INDEX));

		/**
		 * reset the current answer
		 */
		request.getSession().setAttribute(CURRENT_ANSWER, "");

		/**
		 * initialize available question display modes in the session
		 */
		request.getSession().setAttribute(QUESTION_LISTING_MODE_SEQUENTIAL,QUESTION_LISTING_MODE_SEQUENTIAL);
	    request.getSession().setAttribute(QUESTION_LISTING_MODE_COMBINED, QUESTION_LISTING_MODE_COMBINED);
	    
	    /**
		 * retrive the service
		 */
		IQaService qaService = QaServiceProxy.getQaService(getServlet().getServletContext());
	    logger.debug("retrieving qaService: " + qaService);
	    request.getSession().setAttribute(TOOL_SERVICE, qaService);
	    
	    /**
	     * mark the http session as a learning activity 
	     */
	    request.getSession().setAttribute(TARGET_MODE,TARGET_MODE_LEARNING);
	    
	    /**
	     * persist time zone information to session scope. 
	     */
	    QaUtils.persistTimeZone(request);
	    
	    /**
	     * obtain and setup the current user's data 
	     */
	    String userId = "";
	    //get session from shared session.
	    HttpSession ss = SessionManager.getSession();
	    //get back login user DTO
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    if ((user == null) || (user.getUserID() == null))
	    {
	    	logger.debug("error: The tool expects userId");
	    	persistError(request,"error.authoringUser.notAvailable");
	    	request.setAttribute(USER_EXCEPTION_USERID_NOTAVAILABLE, new Boolean(true));
	    	return (mapping.findForward(LOAD_QUESTIONS));
	    }else
	    	userId = user.getUserID().toString();
		
	    
	    /**
	     * process incoming tool session id and later derive toolContentId from it. 
	     */
	    String strToolSessionId=request.getParameter(TOOL_SESSION_ID);
	    long toolSessionId=0;
	    if ((strToolSessionId == null) || (strToolSessionId.length() == 0)) 
	    {
	    	persistError(request, "error.toolSessionId.required");
	    	request.setAttribute(USER_EXCEPTION_TOOLSESSIONID_REQUIRED, new Boolean(true));
			logger.debug("forwarding to: " + LOAD);
			return (mapping.findForward(LOAD));
	    }
	    else
	    {
	    	try
			{
	    		toolSessionId=new Long(strToolSessionId).longValue();
		    	logger.debug("passed TOOL_SESSION_ID : " + new Long(toolSessionId));
		    	request.getSession().setAttribute(TOOL_SESSION_ID,new Long(toolSessionId));	
			}
	    	catch(NumberFormatException e)
			{
	    		persistError(request, "error.sessionId.numberFormatException");
	    		logger.debug("add error.sessionId.numberFormatException to ActionMessages.");
				request.setAttribute(USER_EXCEPTION_NUMBERFORMAT, new Boolean(true));
				logger.debug("forwarding to: " + LOAD);
				return (mapping.findForward(LOAD));
			}
	    }
		
	    /**
	     * By now, the passed tool session id MUST exist in the db through the calling of:
	     * public void createToolSession(Long toolSessionId, Long toolContentId) by the container.
	     *  
	     * make sure this session exists in tool's session table by now.
	     */
		
	    if (!QaUtils.existsSession(toolSessionId, request)) 
		{
				logger.debug("tool session does not exist" + toolSessionId);
				/*
				 *for testing only, remove this line in development 
				 */
				Long currentToolContentId= new Long(1234);
				logger.debug("simulating container behaviour: calling createToolSession with toolSessionId : " + 
						new Long(toolSessionId) + " and toolContentId: " + currentToolContentId);
				try
				{
					qaService.createToolSession(new Long(toolSessionId), currentToolContentId);
					logger.debug("simulated container behaviour.");
				}
				catch(ToolException e)
				{
					logger.debug("we should never come here.");
				}
				 
		}
		
		/**
		 * by now, we made sure that the passed tool session id exists in the db as a new record
		 * Make sure we can retrieve it and relavent content
		 */
		
	    
		QaSession qaSession=qaService.retrieveQaSessionOrNullById(toolSessionId);
	    logger.debug("retrieving qaSession: " + qaSession);
	    /**
	     * find out what content this tool session is referring to
	     * get the content for this tool session (many to one mapping)
	     */
	    
	    /**
	     * Each passed tool session id points to a particular content. Many to one mapping.
	     */
		QaContent qaContent=qaSession.getQaContent();
	    logger.debug("using qaContent: " + qaContent);
	    request.getSession().setAttribute(TOOL_CONTENT_ID, qaContent.getQaContentId());
	    logger.debug("using TOOL_CONTENT_ID: " + qaContent.getQaContentId());
	    	    
	    
	    /**
	     * The content we retrieved above must have been created before in Authoring time. 
	     * And the passed tool session id already refers to it.
	     */
	    
	    
	    logger.debug("ACTIVITY_TITLE: " + qaContent.getTitle());
	    request.getSession().setAttribute(ACTIVITY_TITLE,qaContent.getTitle());
	    
	    logger.debug("ACTIVITY_INSTRUCTIONS: " + qaContent.getInstructions());
	    request.getSession().setAttribute(ACTIVITY_INSTRUCTIONS,qaContent.getInstructions());
	    
		logger.debug("REPORT_TITLE_LEARNER: " + qaContent.getReportTitle());
	    request.getSession().setAttribute(REPORT_TITLE_LEARNER,qaContent.getReportTitle());
	    
	    request.getSession().setAttribute(END_LEARNING_MESSAGE,qaContent.getEndLearningMessage());
	    logger.debug("END_LEARNING_MESSAGE: " + qaContent.getEndLearningMessage());
	    /**
	     * Is the tool activity been checked as Run Offline in the property inspector?
	     */
	    logger.debug("IS_TOOL_ACTIVITY_OFFLINE: " + qaContent.isRunOffline());
	    request.getSession().setAttribute(IS_TOOL_ACTIVITY_OFFLINE, new Boolean(qaContent.isRunOffline()).toString());
	    
	    logger.debug("IS_USERNAME_VISIBLE: " + qaContent.isUsernameVisible());
	    request.getSession().setAttribute(IS_USERNAME_VISIBLE, new Boolean(qaContent.isUsernameVisible()));
	    /**
	     * Is the tool activity been checked as Define Later in the property inspector?
	     */
	    logger.debug("IS_DEFINE_LATER: " + qaContent.isDefineLater());
	    request.getSession().setAttribute(IS_DEFINE_LATER, new Boolean(qaContent.isDefineLater()));
	    
	    /**
	     * convince jsp: Learning mode requires this setting for jsp to generate the user's report 
	     */
	    request.getSession().setAttribute(CHECK_ALL_SESSIONS_COMPLETED, new Boolean(false));
	    	    
	    logger.debug("IS_QUESTIONS_SEQUENCED: " + qaContent.isQuestionsSequenced());
	    String feedBackType="";
    	if (qaContent.isQuestionsSequenced())
    	{
    		request.getSession().setAttribute(QUESTION_LISTING_MODE, QUESTION_LISTING_MODE_SEQUENTIAL);
    		feedBackType=FEEDBACK_TYPE_SEQUENTIAL;
    	}
	    else
	    {
	    	request.getSession().setAttribute(QUESTION_LISTING_MODE, QUESTION_LISTING_MODE_COMBINED);
    		feedBackType=FEEDBACK_TYPE_COMBINED;
	    }
	    logger.debug("QUESTION_LISTING_MODE: " + request.getSession().getAttribute(QUESTION_LISTING_MODE));
	    
    	/**
    	 * fetch question content from content
    	 */
    	Iterator contentIterator=qaContent.getQaQueContents().iterator();
    	while (contentIterator.hasNext())
    	{
    		QaQueContent qaQueContent=(QaQueContent)contentIterator.next();
    		if (qaQueContent != null)
    		{
    			int displayOrder=qaQueContent.getDisplayOrder();
        		if (displayOrder != 0)
        		{
        			/**
    	    		 *  add the question to the questions Map in the displayOrder
    	    		 */
            		mapQuestions.put(new Integer(displayOrder).toString(),qaQueContent.getQuestion());
        		}
    		}
    	}
		
    	request.getSession().setAttribute(MAP_ANSWERS, mapAnswers);
    	request.getSession().setAttribute(MAP_QUESTION_CONTENT_LEARNER, mapQuestions);
    	logger.debug("qaContent has : " + mapQuestions.size() + " entries.");
    	
    	request.getSession().setAttribute(TOTAL_QUESTION_COUNT, new Long(mapQuestions.size()).toString());
    	String userFeedback= feedBackType + request.getSession().getAttribute(TOTAL_QUESTION_COUNT) + QUESTIONS;
    	request.getSession().setAttribute(USER_FEEDBACK, userFeedback);
    	
    	
    	/**
	     * Verify that userId does not already exist in the db.
	     * If it does exist, that means, that user already responded to the content and 
	     * his answers must be displayed  read-only
	     * 
	     */
	    QaQueUsr qaQueUsr=qaService.loadQaQueUsr(new Long(userId));
	    logger.debug("QaQueUsr:" + qaQueUsr);
	    if (qaQueUsr != null)
	    {
	    	logger.debug("the learner has already responsed to this content, just generate a read-only report.");
	    	LearningUtil learningUtil= new LearningUtil();
	    	learningUtil.buidLearnerReport(request,1);    	
	    	logger.debug("buidLearnerReport called successfully, forwarding to: " + LEARNER_REPORT);
	    	return (mapping.findForward(LEARNER_REPORT));
	    }
    	/**
    	 * present user with the questions.
    	 */
		logger.debug("forwarding to: " + LOAD);
		return (mapping.findForward(LOAD));	
		
  } 
	
	/**
     * persists error messages to request scope
     * @param request
     * @param message
     */
	public void persistError(HttpServletRequest request, String message)
	{
		ActionMessages errors= new ActionMessages();
		errors.add(Globals.ERROR_KEY, new ActionMessage(message));
		logger.debug("add " + message +"  to ActionMessages:");
		saveErrors(request,errors);	    	    
	}
}  
