/****************************************************************
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $$Id$$ */	

package org.lamsfoundation.lams.tool.sbmt.service;

import java.util.List;
import java.util.SortedMap;

import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.contentrepository.IVersionedNode;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.tool.sbmt.InstructionFiles;
import org.lamsfoundation.lams.tool.sbmt.SubmitUser;
import org.lamsfoundation.lams.tool.sbmt.SubmitFilesContent;
import org.lamsfoundation.lams.tool.sbmt.SubmitFilesReport;
import org.lamsfoundation.lams.tool.sbmt.SubmitFilesSession;
import org.lamsfoundation.lams.tool.sbmt.dto.FileDetailsDTO;
import org.lamsfoundation.lams.tool.sbmt.exception.SubmitFilesException;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;

/**
 * @author Manpreet Minhas
 */
public interface ISubmitFilesService {

	/**
	 * Returns the <code>SubmitFilesContent</code> object corresponding to the
	 * given <code>contentID</code>. If could not find out corresponding 
	 * <code>SubmitFilesContent</code> by given <code>contentID</code>, return a not-null
	 * but emtpy <code>SubmitFilesContent</code> instance.
	 * 
	 * @param contentID
	 *            The <code>content_id</code> of the object to be looked up
	 * @return SubmitFilesContent The required populated object
	 */
	public SubmitFilesContent getSubmitFilesContent(Long contentID);

	/**
	 * 
	 * Returns the <code>SubmitFilesReport</code> object corresponding to the
	 * given <code>reportID</code>
	 * 
	 * @param reportID
	 * @return SubmitFilesReport The required populated object
	 */
	public SubmitFilesReport getSubmitFilesReport(Long reportID);

	/**
	 * This method uploads a file with the given name and description. It's a
	 * two step process
	 * <ol>
	 * <li>It first uploads the file to the content repository</li>
	 * <li>And then it updates the database</li>
	 * </ol>
	 * @param fileDescription
	 *            The description of the file being uploaded.
	 * @param userID
	 * 			  The <code>User</code> who has uploaded the file.
	 * @param contentID
	 *            The content_id of the record to be updated in the database
	 * @param uploadedFile
	 *            The STRUTS org.apache.struts.upload.FormFile type
	 * 
	 * @throws SubmitFilesException
	 */
	public void uploadFileToSession(Long sessionID, FormFile uploadFile,
			   String fileDescription, Integer userID) throws SubmitFilesException;
	/**
	 * Upload file to repository and persist relative attributes into database.
	 *   
	 * @param contentID
	 * @param uploadFile
	 * @param fileType
	 * @return If successs, return an instance of <code>InstructionFile</code>. Otherwise, return null.
	 * @throws SubmitFilesException
	 */
	public InstructionFiles uploadFileToContent(Long contentID, FormFile uploadFile, String fileType) throws SubmitFilesException;
	/**
	 * This method returns a list of files that were uploaded by the
	 * given <code>User<code> for given <code>contentID</code>.
	 * 
	 * This method is used in the learning enviornment for displaying 
	 * the files being uploaded by the given user, as the user 
	 * uploads them one by one.
	 * 
	 * @param userID The <code>user_id</code> of the <code>User</code>
	 * @param sessionID The <code>session_id</code> to be looked up
	 * @return List The list of required objects.
	 */
	public List getFilesUploadedByUser(Integer userID, Long sessionID);
	/**
	 * This method returns a SortedMap of all files that were submitted users within a
	 * given <code>sessionID</code>.
	 *  
	 * @param sessionID The <code>session_id</code> to be looked up
	 * @return SortedMap, the key is UserDTO, the value is a List of FileDetailsDTO objects 
	 */
	public SortedMap getFilesUploadedBySession(Long sessionID);

	public void updateMarks(Long reportID, Long marks, String comments);
	
	public FileDetailsDTO getFileDetails(Long detailID);
	/**
	 * Get SubmitFilesSession instance according to the given session id.
	 * @param sessionID
	 * @return
	 */
	public SubmitFilesSession getSessionById(Long sessionID);
	public IVersionedNode downloadFile(Long uuid, Long versionID);

	/**
	 * Release marks and comments information to learners, for a special session.
	 * @param sessionID
	 * @return success return true, otherwise return false.
	 */
	public boolean releaseMarksForSession(Long sessionID);

	public void deleteFromRepository(Long uuid, Long versionID);
	/**
	 * When learner finish submission, it invokes this function and will remark the <code>finished</code> field.
	 * 
	 * @param sessionID
	 * @param userID
	 */
	public void finishSubmission(Long sessionID, Integer userID);
    /**
     * Create the default content for the given contentID. These default data will copy from default record in 
     * Tool Content database table.
     * 
     * @return
     * 		The SubmitFilesContent with default content and given contentID
     */
	public SubmitFilesContent createDefaultContent(Long contentID);
	
	
    /**
     * This method retrieves the default content id.
     * @param toolSignature The tool signature which is defined in lams_tool table.
     * @return the default content id
     */
    public Long getToolDefaultContentIdBySignature(String toolSignature);
    
    /**
     * This method retrieves a list of SubmitFileSession from the contentID.
     * @param contentID
     * @return a list of SubmitFileSession
     */
    //public List getSubmitFilesSessionsByContentID(Long contentID);
    public List getSubmitFilesSessionByContentID(Long contentID);
    /**
     * Save or update tool content into database.
     * @param persistContent
     * 			The <code>SubmitFilesContent</code> to be updated
     */
	public void saveOrUpdateContent(SubmitFilesContent persistContent);
	
	/**
	 * Create refection entry into notebook tool.
	 * @param sessionId
	 * @param notebook_tool
	 * @param tool_signature
	 * @param userId
	 * @param entryText
	 */
	public Long createNotebookEntry(Long sessionId, Integer notebookToolType, String toolSignature, Integer userId, String entryText);
	/**
	 * Get reflection entry from notebook tool.
	 * @param sessionId
	 * @param idType
	 * @param signature
	 * @param userID
	 * @return
	 */
	public NotebookEntry getEntry(Long sessionId, Integer idType, String signature, Integer userID);

	/**
	 * Delete instruction file by UID
	 * @param uid
	 */
	public void deleteInstructionFile(Long uid);
	//*************************************************************
	// get SubmitUser methods
	//*************************************************************
	
	/**
	 * Get learner by given <code>toolSessionID</code> and <code>userID</code>.
	 *  
	 * @param sessionID
	 * @param userID
	 * @return
	 */
	public SubmitUser getSessionUser(Long sessionID, Integer userID);
	public SubmitUser getContentUser(Long contentId, Integer userID);
	
	/**
	 * Create new user
	 * @param userDto
	 * @param sessionID
	 * @return
	 */
	public SubmitUser createSessionUser(UserDTO userDto, Long sessionID);
	public SubmitUser createContentUser(UserDTO user, Long contentId);


	/**
	 * Get information of all users who have submitted file.
	 * @return The user information list
	 */
	public List<SubmitUser> getUsersBySession(Long sessionID);
	
	/**
	 * get user by UID
	 * @param uid
	 * @return
	 */
	public SubmitUser getUserByUid(Long uid);



}
