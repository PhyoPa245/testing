/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.vote;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.vote.pojos.VoteContent;
import org.lamsfoundation.lams.tool.vote.pojos.VoteSession;
import org.lamsfoundation.lams.tool.vote.pojos.VoteUploadedFile;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;
import org.lamsfoundation.lams.tool.vote.web.VoteAuthoringForm;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;


/**
 * <p> Common Voting utility functions live here. </p>
 * 
 * @author Ozgur Demirtas
 */
public abstract class VoteUtils implements VoteAppConstants {

	static Logger logger = Logger.getLogger(VoteUtils.class.getName());
	
    public static String replaceNewLines(String text)
    {
        logger.debug("using text: " + text);
        String newText = text.replaceAll("\n","<br>");
        logger.debug("newText: " + newText);
        
        return newText;
    }

    public static String getCurrentLearnerID()
    {
        String userID = "";
        HttpSession ss = SessionManager.getSession();
        logger.debug("ss: " + ss);
        
        if (ss != null)
        {
    	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
    	    if ((user != null) && (user.getUserID() != null))
    	    {
    	    	userID = user.getUserID().toString();
    		    logger.debug("retrieved userId: " + userID);
    	    }
        }
        return userID;
    }

    
    
	/**
	 * 
	 * getGMTDateTime(HttpServletRequest request)
	 * 
	 * @param request
	 * @return
	 */
	/* fix this */
    public static Date getGMTDateTime()
    {
    	Date date=new Date(System.currentTimeMillis());
    	logger.debug("date: " + date);
    	return date;
    }

    
	
	public static UserDTO getToolUser()
	{
		/*obtain user object from the session*/
	    HttpSession ss = SessionManager.getSession();
	    /* get back login user DTO */
	    UserDTO toolUser = (UserDTO) ss.getAttribute(AttributeNames.USER);
		logger.debug("retrieving toolUser: " + toolUser);
		return 	toolUser;
	}
	
	
	public static Long getUserId()
	{
		UserDTO toolUser=getToolUser();
		long userId=toolUser.getUserID().longValue();
		logger.debug("userId: " + userId);
		return new Long(userId);
	}
	
	public static String getUserName()
	{
		/* double check if username and login is the same */
		UserDTO toolUser=getToolUser();
		String userName=toolUser.getLogin();
		logger.debug("userName: " + userName);
		return userName;
	}
	
	public static String getUserFullName()
	{
		UserDTO toolUser=getToolUser();
		String fullName=toolUser.getFirstName() + " " + toolUser.getLastName();  
		logger.debug("fullName: " + fullName);
		return fullName;
	}
	
	public static String getFormattedDateString(Date date)
	{
		logger.debug("getFormattedDateString: " +  
				DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(date));
		return (DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(date));
	}
	
	public static void saveTimeZone(HttpServletRequest request)
	{
		TimeZone timeZone=TimeZone.getDefault();
	    logger.debug("current timezone: " + timeZone.getDisplayName());
	    logger.debug("current timezone id: " + timeZone.getID());
	}
	
	public static String getCurrentTimeZone()
	{
		TimeZone timeZone=TimeZone.getDefault();
	    logger.debug("current timezone: " + timeZone.getDisplayName());
	    return timeZone.getDisplayName();
	}
	
	
	/**
	 * existsContent(long toolContentId)
	 * @param long toolContentId
	 * @return boolean
	 * determine whether a specific toolContentId exists in the db
	 */
	public static boolean existsContent(Long toolContentId, HttpServletRequest request, IVoteService voteService)
	{
	    logger.debug("voteService: " + voteService);

    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
	    logger.debug("retrieving voteContent: " + voteContent);
	    if (voteContent == null) 
	    	return false;

	    
		return true;	
	}

	/**
	 * it is expected that the tool session id already exists in the tool sessions table
	 * existsSession(long toolSessionId)
	 * @param toolSessionId
	 * @return boolean
	 */
	public static boolean existsSession(Long toolSessionId, HttpServletRequest request, IVoteService voteService)
	{
	    logger.debug("voteService: " + voteService);
	    
	    VoteSession voteSession=voteService.retrieveVoteSession(toolSessionId);
	    logger.debug("voteSession:" + voteSession);
    	
	    if (voteSession == null) 
	    	return false;
	    
		return true;	
	}
	
	
    public static void readContentValues(HttpServletRequest request, VoteContent defaultVoteContent, 
            VoteAuthoringForm voteAuthoringForm, VoteGeneralAuthoringDTO voteGeneralAuthoringDTO)
	{
        logger.debug("setting authoring screen properties");
		/*should never be null anyway as default content MUST exist in the db*/
        if(defaultVoteContent == null)
            throw new NullPointerException("Default VoteContent cannot be null");
        
        voteGeneralAuthoringDTO.setActivityTitle(defaultVoteContent.getTitle());
        voteGeneralAuthoringDTO.setActivityInstructions(defaultVoteContent.getInstructions());
	    
	    voteAuthoringForm.setAllowText(defaultVoteContent.isAllowText()?"1":"0");
	    voteAuthoringForm.setAllowTextEntry(defaultVoteContent.isAllowText()?"1":"0");
	    voteAuthoringForm.setVoteChangable(defaultVoteContent.isVoteChangable()?"1":"0");
	    voteAuthoringForm.setLockOnFinish(defaultVoteContent.isLockOnFinish()?"1":"0");
	    voteAuthoringForm.setReflect(defaultVoteContent.isReflect()?"1":"0");
	    
        voteAuthoringForm.setOnlineInstructions(defaultVoteContent.getOnlineInstructions());
	    voteAuthoringForm.setOfflineInstructions(defaultVoteContent.getOfflineInstructions());
	    

	    voteGeneralAuthoringDTO.setAllowText(defaultVoteContent.isAllowText()?"1":"0");
	    voteGeneralAuthoringDTO.setVoteChangable(defaultVoteContent.isVoteChangable()?"1":"0");
	    voteGeneralAuthoringDTO.setLockOnFinish(defaultVoteContent.isLockOnFinish()?"1":"0");
	    voteAuthoringForm.setReflect(defaultVoteContent.isReflect()?"1":"0");
	    
	    voteGeneralAuthoringDTO.setOnlineInstructions(defaultVoteContent.getOnlineInstructions());
	    voteGeneralAuthoringDTO.setOfflineInstructions(defaultVoteContent.getOfflineInstructions());

	    
	    String maxNomcount= defaultVoteContent.getMaxNominationCount();
	    logger.debug("maxNomcount: " + maxNomcount);
	    if (maxNomcount.equals(""))
	        maxNomcount="0";
	    voteAuthoringForm.setMaxNominationCount(maxNomcount);
	    voteGeneralAuthoringDTO.setMaxNominationCount(maxNomcount);
	}


    public static String stripHTML(String htmlText)
    {
        String noHTMLText = htmlText.replaceAll("\\<.*?\\>","").replaceAll("&nbsp;","").replaceAll("&#[0-9][0-9][0-9][0-9];","");
        String[] htmlTokens = noHTMLText.split("\n");
        String noHtmlNoNewLineStr="";
        for (int i=0; i < htmlTokens.length ; i++)
        {
            if (!htmlTokens[i].trim().equals(""))
            {
                noHtmlNoNewLineStr= noHtmlNoNewLineStr + " " + htmlTokens[i];
            }
        }
        
        logger.debug("trimmed noHtmlNoNewLineStr: " + noHtmlNoNewLineStr.trim());
        if (noHtmlNoNewLineStr.trim().equals(""))
        {
            logger.debug("nomination text is just composed of html markup..." +
            		"returning html formatted text");
            if (htmlText.length() > 50)
                return htmlText.substring(0,51);
            else
                return htmlText;
        }
        
        if (noHtmlNoNewLineStr.length() > 50)
            return noHtmlNoNewLineStr.substring(0,51);
        
        return noHtmlNoNewLineStr;
    }

    
	public static void saveRichText(HttpServletRequest request, VoteGeneralAuthoringDTO voteGeneralAuthoringDTO, 
	        SessionMap sessionMap)
	{
	    logger.debug("doing saveRichText, sessionMap: " + sessionMap);
		String richTextTitle = request.getParameter(TITLE);
	    String richTextInstructions = request.getParameter(INSTRUCTIONS);
	    
	    logger.debug("richTextTitle: " + richTextTitle);
	    logger.debug("richTextInstructions: " + richTextInstructions);
	    
	    
	    if (richTextTitle != null)
	    {
			voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	    }
	    String noHTMLTitle = stripHTML(richTextTitle);
	    logger.debug("noHTMLTitle: " + noHTMLTitle);

	
	    if (richTextInstructions != null)
	    {
			voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);
	    }
	    
		String richTextOfflineInstructions=request.getParameter(RICHTEXT_OFFLINEINSTRUCTIONS);
		logger.debug("read parameter richTextOfflineInstructions: " + richTextOfflineInstructions);

		if ((richTextOfflineInstructions != null) && (richTextOfflineInstructions.length() > 0))
		{
			voteGeneralAuthoringDTO.setRichTextOfflineInstructions(richTextOfflineInstructions);
			sessionMap.put(OFFLINE_INSTRUCTIONS_KEY,richTextOfflineInstructions);
		}

		String richTextOnlineInstructions=request.getParameter(RICHTEXT_ONLINEINSTRUCTIONS);
		logger.debug("read parameter richTextOnlineInstructions: " + richTextOnlineInstructions);
		
		if ((richTextOnlineInstructions != null) && (richTextOnlineInstructions.length() > 0))
		{
			voteGeneralAuthoringDTO.setRichTextOnlineInstructions(richTextOnlineInstructions);
			sessionMap.put(ONLINE_INSTRUCTIONS_KEY,richTextOnlineInstructions);
		}
	}
	
	
	public static void configureContentRepository(HttpServletRequest request, IVoteService voteService)
	{
		logger.debug("attempt configureContentRepository");
    	voteService.configureContentRepository();
	    logger.debug("configureContentRepository ran successfully");
	}
	
	

    /**
     * temporary function
     * @return
     */
	public static int getCurrentUserId(HttpServletRequest request) throws VoteApplicationException
    {
	    HttpSession ss = SessionManager.getSession();
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		logger.debug(logger + " " + "VoteUtils" +  " Current user is: " + user + " with id: " + user.getUserID());
		return user.getUserID().intValue();
    }
	

    /**
     * temporary function
     * @return
     */
	public static User createSimpleUser(Integer userId)
	{
		User user=new User();
		user.setUserId(userId);
		return user;
	}

    /**
     * temporary function
     * @return
     */
	public static boolean getDefineLaterStatus()
	{
		return false;
	}
	
	
	/**
	 * builds a map from a list  
	 * convertToMap(List sessionsList)
	 * 
	 * @param sessionsList
	 * @return Map
	 */
	public static Map convertToMap(List sessionsList, String listType)
	{
		Map map= new TreeMap(new VoteComparator());
		logger.debug("listType: " + listType);
		
		Iterator listIterator=sessionsList.iterator();
    	Long mapIndex=new Long(1);
    	
    	
    	while (listIterator.hasNext())
    	{
    		if (listType.equals("String"))
    		{
    			String text=(String)listIterator.next();
    			map.put(mapIndex.toString(), text);
    		}
    		else if (listType.equals("Long"))
    		{
    			Long LongValue=(Long)listIterator.next();
    			map.put(mapIndex.toString(), LongValue);
    		}
    		mapIndex=new Long(mapIndex.longValue()+1);
    	}
    	return map;
	}
	
	
	/**
	 * builds a String based map from a list 
	 * convertToMap(List sessionsList)
	 * 
	 * @param sessionsList
	 * @return Map
	 */
	public static Map convertToStringMap(List sessionsList, String listType)
	{
		Map map= new TreeMap(new VoteComparator());
		logger.debug("listType: " + listType);
		
		Iterator listIterator=sessionsList.iterator();
    	Long mapIndex=new Long(1);
    	
    	
    	while (listIterator.hasNext())
    	{
    		if (listType.equals("String"))
    		{
    			logger.debug("listType String");
    			String text=(String)listIterator.next();
    			map.put(mapIndex.toString(), text);
    		}
    		else if (listType.equals("Long"))
    		{
    			logger.debug("listType Long");
    			Long LongValue=(Long)listIterator.next();
    			map.put(mapIndex.toString(), LongValue.toString());
    		}
    		mapIndex=new Long(mapIndex.longValue()+1);
    	}
    	return map;
	}
	
	
	/**
	 * find out if the content is in use or not. If it is in use, the author can not modify it.
	 * The idea of content being in use is, once any one learner starts using a particular content
	 * that content should become unmodifiable. 
	 * 
	 * isContentInUse(VoteContent voteContent)
	 * @param voteContent
	 * @return boolean
	 */
	public static boolean isContentInUse(VoteContent voteContent)
	{
		logger.debug("is content inuse: " + voteContent.isContentInUse());
		return  voteContent.isContentInUse();
	}
	
	
	/**
	 * find out if the content is being edited in monitoring interface or not. If it is, the author can not modify it.
	 * 
	 * isDefineLater(VoteContent voteContent)
	 * @param voteContent
	 * @return boolean
	 */
	public static boolean isDefineLater(VoteContent voteContent)
	{
		logger.debug("is define later: " + voteContent.isDefineLater());
		return  voteContent.isDefineLater();
	}
	
	
	/**
	 * find out if the content is set to run offline or online. If it is set to run offline , the learners are informed about that..
	 * isRubnOffline(VoteContent voteContent)
	 * 
	 * @param voteContent
	 * @return boolean
	 */
	public static boolean isRunOffline(VoteContent voteContent)
	{
		logger.debug("is run offline: " + voteContent.isRunOffline());
		return voteContent.isRunOffline();
	}

	
    
	public static String getDestination(String sourceVoteStarter)
	{
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		
		if ((sourceVoteStarter != null) && !sourceVoteStarter.equals("monitoring"))
		{
			logger.debug("request is from authoring or define Later url. return to: " + LOAD_QUESTIONS);
			return LOAD_QUESTIONS;	
		}
		else if (sourceVoteStarter == null)
		{
			logger.debug("request is from authoring url. return to: " + LOAD_QUESTIONS);
			return LOAD_QUESTIONS;	
		}
		else
		{
			logger.debug("request is from monitoring url. return to: " + LOAD_MONITORING_CONTENT_EDITACTIVITY);
			return LOAD_MONITORING_CONTENT_EDITACTIVITY;	
		}
	}

	public static void setDefineLater(HttpServletRequest request, boolean value, IVoteService voteService, String toolContentID)
    {
    	logger.debug("toolContentID:" + toolContentID);
    	
    	VoteContent voteContent=voteService.retrieveVote(new Long(toolContentID));
    	logger.debug("voteContent:" + voteContent);
    	if (voteContent != null)
    	{
    		voteContent.setDefineLater(value);
        	logger.debug("defineLater has been set to value: " + value);
        	voteService.updateVote(voteContent);	
    	}
    }

	
	/**
	 * 
	 * cleanUpSessionAbsolute(HttpServletRequest request)
	 * @param request
	 */
    public static void cleanUpSessionAbsolute(HttpServletRequest request)
    {
    	cleanUpUserExceptions(request);
    	logger.debug("completely cleaned the session.");
    }
    
    /**
     *removes attributes except USER_EXCEPTION_NO_STUDENT_ACTIVITY 
     */
    public static void cleanUpUserExceptions(HttpServletRequest request)
    {
    	request.getSession().removeAttribute(USER_EXCEPTION_WRONG_FORMAT);
    	request.getSession().removeAttribute(USER_EXCEPTION_INCOMPATIBLE_IDS);
    	request.getSession().removeAttribute(USER_EXCEPTION_NUMBERFORMAT);
    	request.getSession().removeAttribute(USER_EXCEPTION_CONTENT_DOESNOTEXIST);
    	request.getSession().removeAttribute(USER_EXCEPTION_TOOLSESSION_DOESNOTEXIST);
    	request.getSession().removeAttribute(USER_EXCEPTION_TOOLCONTENT_DOESNOTEXIST);
    	request.getSession().removeAttribute(USER_EXCEPTION_LEARNER_REQUIRED);    	
    	request.getSession().removeAttribute(USER_EXCEPTION_CONTENTID_REQUIRED);
    	request.getSession().removeAttribute(USER_EXCEPTION_TOOLSESSIONID_REQUIRED);
    	request.getSession().removeAttribute(USER_EXCEPTION_TOOLSESSIONID_INCONSISTENT);
    	request.getSession().removeAttribute(USER_EXCEPTION_DEFAULTCONTENT_NOT_AVAILABLE);
    	request.getSession().removeAttribute(USER_EXCEPTION_DEFAULTQUESTIONCONTENT_NOT_AVAILABLE);
    	request.getSession().removeAttribute(USER_EXCEPTION_DEFAULTOPTIONSCONTENT_NOT_AVAILABLE);
    	request.getSession().removeAttribute(USER_EXCEPTION_USERID_NOTAVAILABLE);    	
    	request.getSession().removeAttribute(USER_EXCEPTION_USERID_NOTNUMERIC);
    	request.getSession().removeAttribute(USER_EXCEPTION_ONLYCONTENT_ANDNOSESSIONS);
    	request.getSession().removeAttribute(USER_EXCEPTION_USERID_EXISTING);
    	request.getSession().removeAttribute(USER_EXCEPTION_USER_DOESNOTEXIST);
    	request.getSession().removeAttribute(USER_EXCEPTION_MONITORINGTAB_CONTENTID_REQUIRED);
    	request.getSession().removeAttribute(USER_EXCEPTION_DEFAULTCONTENT_NOTSETUP);
    	request.getSession().removeAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS);    	
    	request.getSession().removeAttribute(USER_EXCEPTION_MODE_REQUIRED);
    	request.getSession().removeAttribute(USER_EXCEPTION_CONTENT_IN_USE);
    	request.getSession().removeAttribute(USER_EXCEPTION_CONTENT_BEING_MODIFIED);
    	request.getSession().removeAttribute(USER_EXCEPTION_CONTENT_RUNOFFLINE);
    	request.getSession().removeAttribute(USER_EXCEPTION_MODE_INVALID);
    	request.getSession().removeAttribute(USER_EXCEPTION_QUESTION_EMPTY);
    	request.getSession().removeAttribute(USER_EXCEPTION_ANSWER_EMPTY);
    	request.getSession().removeAttribute(USER_EXCEPTION_ANSWERS_DUPLICATE);    	
    	request.getSession().removeAttribute(USER_EXCEPTION_OPTIONS_COUNT_ZERO);
    	request.getSession().removeAttribute(USER_EXCEPTION_CHKBOXES_EMPTY);
    	request.getSession().removeAttribute(USER_EXCEPTION_SUBMIT_NONE);
    	request.getSession().removeAttribute(USER_EXCEPTION_NUMBERFORMAT);
    	request.getSession().removeAttribute(USER_EXCEPTION_FILENAME_EMPTY);
    	request.getSession().removeAttribute(USER_EXCEPTION_WEIGHT_MUST_EQUAL100);
    	request.getSession().removeAttribute(USER_EXCEPTION_SINGLE_OPTION);
    }
    
    
    
    public static void setFormProperties(HttpServletRequest request, IVoteService voteService,  
            VoteAuthoringForm  voteAuthoringForm, VoteGeneralAuthoringDTO voteGeneralAuthoringDTO, String strToolContentID, 
            String defaultContentIdStr, String activeModule, SessionMap sessionMap, String httpSessionID)
    {
    	logger.debug("setFormProperties: ");
    	logger.debug("using strToolContentID: " + strToolContentID);
    	logger.debug("using defaultContentIdStr: " + defaultContentIdStr);
    	logger.debug("using activeModule: " + activeModule);
    	logger.debug("using httpSessionID: " + httpSessionID);

    	voteAuthoringForm.setHttpSessionID(httpSessionID);
    	voteGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
    	
    	voteAuthoringForm.setToolContentID(strToolContentID);
    	
    	if ((defaultContentIdStr != null) && (defaultContentIdStr.length() > 0)) 
    	    voteAuthoringForm.setDefaultContentIdStr(new Long(defaultContentIdStr).toString());
    	
    	voteAuthoringForm.setActiveModule(activeModule);
    	voteGeneralAuthoringDTO.setActiveModule(activeModule);
    	
		String voteChangable=request.getParameter("voteChangable");
    	logger.debug("voteChangable: " + voteChangable);
		voteAuthoringForm.setVoteChangable(voteChangable);
		voteGeneralAuthoringDTO.setVoteChangable(voteChangable);
		
		String lockOnFinish=request.getParameter("lockOnFinish");
		logger.debug("lockOnFinish: " + lockOnFinish);
		voteAuthoringForm.setLockOnFinish(lockOnFinish);
		voteGeneralAuthoringDTO.setLockOnFinish(lockOnFinish);
		
		String allowText=request.getParameter("allowText");
		logger.debug("allowText: " + allowText);
		voteAuthoringForm.setAllowText(allowText);
		voteGeneralAuthoringDTO.setAllowText(allowText);
		
		String maxNominationCount=request.getParameter("maxNominationCount");
		logger.debug("maxNominationCount: " + maxNominationCount);
		voteAuthoringForm.setMaxNominationCount(maxNominationCount);
		voteGeneralAuthoringDTO.setMaxNominationCount(maxNominationCount);

		String reflect=request.getParameter("reflect");
		logger.debug("reflect: " + maxNominationCount);
		voteAuthoringForm.setReflect(reflect);
		voteGeneralAuthoringDTO.setReflect(reflect);

		String reflectionSubject=request.getParameter("reflectionSubject");
		logger.debug("reflectionSubject: " + reflectionSubject);
		voteAuthoringForm.setReflectionSubject(reflectionSubject);
		voteGeneralAuthoringDTO.setReflectionSubject(reflectionSubject);


		String offlineInstructions=request.getParameter(OFFLINE_INSTRUCTIONS);
		logger.debug("offlineInstructions: " + offlineInstructions);
		voteAuthoringForm.setOfflineInstructions(offlineInstructions);
		voteGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

		String onlineInstructions=request.getParameter(ONLINE_INSTRUCTIONS);
		logger.debug("onlineInstructions: " + onlineInstructions);
		voteAuthoringForm.setOnlineInstructions(onlineInstructions);
		voteGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);
		
		logger.debug("ending setFormProperties with voteAuthoringForm: " + voteAuthoringForm);
		logger.debug("ending setFormProperties with voteGeneralAuthoringDTO: " + voteGeneralAuthoringDTO);
    }

    
	public static void setDefineLater(HttpServletRequest request, boolean value, String strToolContentID, IVoteService voteService)
    {
		logger.debug("voteService: " + voteService);
    	logger.debug("value:" + value);
    	logger.debug("strToolContentID:" + strToolContentID);
    	
		VoteContent voteContent=voteService.retrieveVote(new Long(strToolContentID));
		
    	logger.debug("voteContent:" + voteContent);
    	if (voteContent != null)
    	{
    		voteContent.setDefineLater(value);
        	logger.debug("defineLater has been set to:" + value);
        	voteService.updateVote(voteContent);	
    	}
    }
	
	

    /** If this file exists in attachments map, move it to the deleted attachments map.
     * Returns the updated deletedAttachments map, creating a new one if needed. If uuid supplied
     * then tries to match on that, otherwise uses filename and isOnline. */
    public static List moveToDelete(String uuid, List attachmentsList, List deletedAttachmentsList ) {

        logger.debug("doing moveToDelete: " + attachmentsList);
        logger.debug("doing moveToDelete: " + deletedAttachmentsList);
        List deletedList = deletedAttachmentsList != null ? deletedAttachmentsList : new ArrayList();
        
        logger.debug("deletedList: " + deletedList);
        
        if ( attachmentsList != null ) {
            logger.debug("attachmentsList not null: " + attachmentsList);
            Iterator iter = attachmentsList.iterator();
            VoteUploadedFile attachment = null;
            while ( iter.hasNext() && attachment == null ) {
                VoteUploadedFile value = (VoteUploadedFile) iter.next();
                logger.debug("value: " + value);
                
                if ( uuid.equals(value.getUuid()) ) {
                    logger.debug("value made attachment:");
                    attachment = value;
                }

            }
            if ( attachment != null ) {
                logger.debug("attachment not null");                
                deletedList.add(attachment);
                attachmentsList.remove(attachment);
            }
        }
        
        logger.debug("final attachmentsList: " + attachmentsList);
        logger.debug("final deletedAttachmentsList: " + deletedAttachmentsList);
        return deletedList;
    }


    public static List moveToDelete(String filename, boolean isOnline, List attachmentsList, List deletedAttachmentsList ) {

        List deletedList = deletedAttachmentsList != null ? deletedAttachmentsList : new ArrayList();
        
        if ( attachmentsList != null ) {
            Iterator iter = attachmentsList.iterator();
            VoteUploadedFile attachment = null;
            while ( iter.hasNext() && attachment == null ) {
                VoteUploadedFile value = (VoteUploadedFile) iter.next();
                if ( filename.equals(value.getFileName()) && isOnline == value.isFileOnline()) {
                    attachment = value;
                }

            }
            if ( attachment != null ) {
                deletedList.add(attachment);
                attachmentsList.remove(attachment);
            }
        }
        
        return deletedList;
    }
    
}
