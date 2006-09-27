/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
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
 * ****************************************************************
 */
/* $Id$ */
package org.lamsfoundation.lams.tool.survey;

public class SurveyConstants {
	public static final String TOOL_SIGNATURE = "lasurv11";
	public static final String SURVEY_SERVICE = "lasurvSurveyService";
	
	public static final int COMPLETED = 1;
	
	//survey type;
	public static final short QUESTION_TYPE_SINGLE_CHOICE = 1;
	public static final short QUESTION_TYPE_MUTLIPLE_CHOICE = 2;
	public static final short QUESTION_TYPE_TEXT_ENTRY = 3;
	
	//for action forward name
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String DEFINE_LATER = "definelater";
	public static final String RUN_OFFLINE = "runOffline";
	public static final String FORWARD_RESULT = "result";

	//for parameters' name
	public static final String PARAM_FILE_VERSION_ID = "fileVersionId";
	public static final String PARAM_FILE_UUID = "fileUuid";
	public static final String PARAM_ITEM_INDEX = "itemIndex";
	public static final String PARAM_RUN_OFFLINE = "runOffline";
	public static final String PARAM_TITLE = "title";
	public static final String ATTR_USER_UID = "userUid";
	
	
	//error message keys
	public static final String ERROR_MSG_TITLE_BLANK = "error.survey.item.title.blank";
	public static final String ERROR_MSG_URL_BLANK = "error.survey.item.url.blank";
	public static final String ERROR_MSG_DESC_BLANK = "error.survey.item.question.blank";
	public static final String ERROR_MSG_LESS_OPTIONS = "error.survey.item.question.less.option";
	public static final String ERROR_MSG_FILE_BLANK = "error.survey.item.file.blank";
	public static final String ERROR_MSG_INVALID_URL = "error.survey.item.invalid.url";
	public static final String ERROR_MSG_UPLOAD_FAILED = "error.upload.failed";
	public static final String ERROR_MSG_MANDATORY_QUESTION = "error.mandatory.question";
	public static final String ERROR_MSG_CHART_ERROR = "error.chart.gen";
	public static final String ERROR_MSG_SINGLE_CHOICE = "error.single.choice.over";

	public static final String MSG_OPEN_RESPONSE ="label.open.response";
	public static final String MSG_PIECHART_TITLE = "piechart.title";
	public static final String MSG_BARCHART_TITLE = "barchart.title";
	public static final String MSG_BARCHART_CATEGORY_AXIS_LABEL = "barchart.category.axis.label";
	public static final String MSG_BARCHART_VALUE_AXIS_LABEL = "barchart.value.axis.label";
	

	
	public static final String ATTR_REFLECTION_ON = "reflectOn";
	public static final String ATTR_REFLECTION_INSTRUCTION = "reflectInstructions";
	public static final String ATTR_REFLECT_LIST = "reflectList";
	public static final String ATTR_SESSION_MAP_ID = "sessionMapID";
	public static final String PAGE_EDITABLE = "isPageEditable";
	public static final String ATTR_FILE_TYPE_FLAG = "fileTypeFlag";
	public static final String ATTR_SURVEY_FORM = "surveyForm";
	
	public static final String ATTR_INSTRUCTION_LIST = "instructionList";
	public static final String ATT_ATTACHMENT_LIST = "instructionAttachmentList";
	public static final String ATTR_DELETED_ATTACHMENT_LIST = "deletedAttachmmentList";
	public static final String ATTR_QUESTION_LIST = "questionList";
	public static final String ATTR_ANSWER_LIST = "answerList";
	public static final String ATTR_QUESTION = "question";
	public static final String ATTR_DELETED_QUESTION_LIST = "deletedQuestionList";
	
	public static final String ATTR_TITLE = "title";
	public static final String ATTR_SURVEY_INSTRUCTION = "instructions";
	public static final String ATTR_FINISH_LOCK = "finishedLock";
	public static final String ATTR_NEXT_ACTIVITY_URL = "nextActivityUrl";
	public static final short SURVEY_TYPE_TEXT_ENTRY = 3;
	public static final short SURVEY_TYPE_MULTIPLE_CHOICES = 2;
	public static final short SURVEY_TYPE_SINGLE_CHOICE = 1;
	public static final String FORWARD_OPEN_QUESTION = "opentext";
	public static final String FORWARD_CHOICE_QUESTION = "question";
	public static final String ATTR_SHOW_ON_ONE_PAGE = "showOnOnePage";
	public static final String PREFIX_QUESTION_CHOICE = "optionChoice";
	public static final String PREFIX_QUESTION_TEXT= "optionText";
	public static final String ERROR_MSG_KEY = "questionError";
	public static final String ATTR_TOTAL_QUESTIONS = "totalQuestions";
	public static final String ATTR_CURRENT_QUESTIONS_IDX = "currentIdx";
	public static final String CHART_TYPE = "chartType";
	public static final String ATTR_QUESTION_UID = "questionUid";
	public static final String ATTR_SUMMARY_LIST = "summaryList";
	public static final String ATTR_SURVEY = "survey";
	
	//POSITION
	public static int POSITION_INSIDE = 0;
	public static int POSITION_FIRST = 1;
	public static int POSITION_LAST = 2;
	public static int POSITION_ONLY_ONE = 3;
	
	public static final String OPTION_SHORT_HEADER = "a";
}
