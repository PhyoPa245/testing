package org.lamsfoundation.lams.tool.assessment.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.lamsfoundation.lams.qb.model.QbQuestion;
import org.lamsfoundation.lams.tool.assessment.AssessmentConstants;
import org.lamsfoundation.lams.tool.assessment.dto.AssessmentResultDTO;
import org.lamsfoundation.lams.tool.assessment.dto.OptionDTO;
import org.lamsfoundation.lams.tool.assessment.dto.QuestionDTO;
import org.lamsfoundation.lams.tool.assessment.dto.QuestionSummary;
import org.lamsfoundation.lams.tool.assessment.dto.TblAssessmentDTO;
import org.lamsfoundation.lams.tool.assessment.dto.TblAssessmentQuestionDTO;
import org.lamsfoundation.lams.tool.assessment.dto.TblAssessmentQuestionResultDTO;
import org.lamsfoundation.lams.tool.assessment.model.Assessment;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestion;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestionResult;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentSession;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentUser;
import org.lamsfoundation.lams.tool.assessment.model.QuestionReference;
import org.lamsfoundation.lams.tool.assessment.service.AssessmentServiceImpl;
import org.lamsfoundation.lams.tool.assessment.service.IAssessmentService;
import org.lamsfoundation.lams.tool.assessment.util.AssessmentEscapeUtils;
import org.lamsfoundation.lams.tool.assessment.util.AssessmentSessionComparator;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tblmonitoring")
public class TblMonitoringController {

    @Autowired
    @Qualifier("laasseAssessmentService")
    private IAssessmentService assessmentService;

    /**
     * Shows ira page in case of Assessment activity
     */
    @RequestMapping("iraAssessment")
    public String iraAssessment(HttpServletRequest request) throws IOException, ServletException {
	Long toolContentId = WebUtil.readLongParam(request, "toolContentID");

	String[] toolContentIds = new String[] { toolContentId.toString() };
	String[] activityTitles = new String[] { "" };
	List<TblAssessmentDTO> assessmentDtos = getAssessmentDtos(toolContentIds, activityTitles);
	request.setAttribute("assessmentDtos", assessmentDtos);

	request.setAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID, toolContentId);
	request.setAttribute("isIraAssessment", true);
	return "pages/tblmonitoring/assessment";
    }

    /**
     * Shows ira page in case of Assessment activity
     */
    @RequestMapping("iraAssessmentStudentChoices")
    public String iraAssessmentStudentChoices(HttpServletRequest request) throws IOException, ServletException {
	Long toolContentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	Assessment assessment = assessmentService.getAssessmentByContentId(toolContentId);

	//prepare list of the questions, filtering out questions that aren't supposed to be answered
	Set<AssessmentQuestion> questionList = new TreeSet<>();
	//in case there is at least one random question - we need to show all questions in a drop down select
	if (assessment.hasRandomQuestion()) {
	    questionList.addAll(assessment.getQuestions());

	    //show only questions from question list otherwise
	} else {
	    for (QuestionReference reference : (Set<QuestionReference>) assessment.getQuestionReferences()) {
		questionList.add(reference.getQuestion());
	    }
	}
	//keep only MCQ type of questions
	Set<QuestionDTO> questionDtos = new TreeSet<>();
	int maxOptionsInQuestion = 0;
	for (AssessmentQuestion question : questionList) {
	    if (QbQuestion.TYPE_MULTIPLE_CHOICE == question.getType()
		    || QbQuestion.TYPE_VERY_SHORT_ANSWERS == question.getType()) {
		questionDtos.add(new QuestionDTO(question));

		//calculate maxOptionsInQuestion
		if (question.getQbQuestion().getQbOptions().size() > maxOptionsInQuestion) {
		    maxOptionsInQuestion = question.getQbQuestion().getQbOptions().size();
		}
	    }
	}
	request.setAttribute("maxOptionsInQuestion", maxOptionsInQuestion);

	int totalNumberOfUsers = assessmentService.getCountUsersByContentId(toolContentId);
	for (QuestionDTO questionDto : questionDtos) {

	    // build candidate dtos
	    for (OptionDTO optionDto : questionDto.getOptionDtos()) {
		int optionAttemptCount = assessmentService.countAttemptsPerOption(toolContentId, optionDto.getUid());

		float percentage = (float) (optionAttemptCount * 100) / totalNumberOfUsers;
		optionDto.setPercentage(percentage);
	    }
	}
	request.setAttribute("questions", questionDtos);

	request.setAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID, toolContentId);
	return "pages/tblmonitoring/iraAssessmentStudentChoices";
    }

    private List<TblAssessmentDTO> getAssessmentDtos(String[] toolContentIds, String[] activityTitles) {
	List<TblAssessmentDTO> assessmentDtos = new ArrayList<TblAssessmentDTO>();
	int i = 0;
	for (String toolContentIdStr : toolContentIds) {
	    String activityTitle = activityTitles[i++];

	    //skip empty contentIds
	    if (toolContentIdStr.length() == 0) {
		continue;
	    }
	    Long toolContentId = Long.parseLong(toolContentIdStr);

	    TblAssessmentDTO assessmentDto = new TblAssessmentDTO();

	    int attemptedLearnersNumber = assessmentService.getCountUsersByContentId(toolContentId);
	    assessmentDto.setAttemptedLearnersNumber(attemptedLearnersNumber);
	    assessmentDto.setActivityTitle(activityTitle);

	    Assessment assessment = assessmentService.getAssessmentByContentId(toolContentId);
	    assessmentDto.setAssessment(assessment);
	    assessmentDto.setQuestions(prepareQuestionsList(assessment));
	    assessmentDtos.add(assessmentDto);
	}

	return assessmentDtos;
    }

    private Set<AssessmentQuestion> prepareQuestionsList(Assessment assessment) {
	// question list to display
	Set<AssessmentQuestion> questions = new TreeSet<>();
	boolean hasRandomQuestion = false;
	for (QuestionReference reference : (Set<QuestionReference>) assessment.getQuestionReferences()) {
	    hasRandomQuestion |= reference.isRandomQuestion();
	}
	// in case there is at least one random question - we need to show all questions
	if (hasRandomQuestion) {
	    questions.addAll(assessment.getQuestions());

	    // show only questions from question list otherwise
	} else {
	    for (QuestionReference reference : (Set<QuestionReference>) assessment.getQuestionReferences()) {
		//sort questions the same way references are sorted (as per LKC request)
		AssessmentQuestion question = reference.getQuestion();
		question.setDisplayOrder(reference.getSequenceId());
		questions.add(question);
	    }
	}
	return questions;
    }

    /**
     * Shows aes page
     */
    @RequestMapping("aes")
    public String aes(HttpServletRequest request) throws IOException, ServletException {
	String[] toolContentIds = request.getParameter("assessmentToolContentIds").split(",");
	String[] activityTitles = request.getParameter("assessmentActivityTitles").split("\\,");

	List<TblAssessmentDTO> assessmentDtos = getAssessmentDtos(toolContentIds, activityTitles);
	request.setAttribute("assessmentDtos", assessmentDtos);

	Long toolContentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID, true);
	request.setAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID, toolContentId);

	return "pages/tblmonitoring/assessment";
    }

    /**
     * Shows ira StudentChoices page
     */
    @RequestMapping("aesStudentChoices")
    public String aesStudentChoices(HttpServletRequest request) throws IOException, ServletException {

	Long toolContentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	Assessment assessment = assessmentService.getAssessmentByContentId(toolContentId);
	Map<Long, QuestionSummary> questionSummaries = assessmentService.getQuestionSummaryForExport(assessment);
	List<TblAssessmentQuestionDTO> tblQuestionDtos = new ArrayList<TblAssessmentQuestionDTO>();
	for (QuestionSummary questionSummary : questionSummaries.values()) {
	    QuestionDTO questionDto = questionSummary.getQuestionDto();

	    TblAssessmentQuestionDTO tblQuestionDto = new TblAssessmentQuestionDTO();
	    tblQuestionDto.setTitle(questionDto.getTitle());
	    tblQuestionDto.setQuestionTypeLabel(AssessmentServiceImpl.getQuestionTypeLabel(questionDto.getType()));
	    tblQuestionDto.setCorrectAnswer(getAssessmentCorrectAnswer(questionDto));

	    List<TblAssessmentQuestionResultDTO> sessionQuestionResults = new ArrayList<TblAssessmentQuestionResultDTO>();
	    for (List<AssessmentQuestionResult> questionResultsPerSession : questionSummary
		    .getQuestionResultsPerSession()) {

		TblAssessmentQuestionResultDTO tblQuestionResultDto = new TblAssessmentQuestionResultDTO();
		String answer = "";
		boolean correct = false;
		if (!questionResultsPerSession.isEmpty()) {
		    AssessmentQuestionResult questionResult = questionResultsPerSession.get(0);
		    answer = AssessmentEscapeUtils.printResponsesForJqgrid(questionResult);
		    correct = questionResult.getMaxMark() == null ? false
			    : (questionResult.getPenalty() + questionResult.getMark() + 0.1) >= questionResult
				    .getMaxMark();
		}
		tblQuestionResultDto.setAnswer(answer);
		tblQuestionResultDto.setCorrect(correct);

		sessionQuestionResults.add(tblQuestionResultDto);
	    }
	    tblQuestionDto.setSessionQuestionResults(sessionQuestionResults);

	    tblQuestionDtos.add(tblQuestionDto);
	}

	SortedSet<AssessmentSession> sessions = new TreeSet<>(new AssessmentSessionComparator());
	sessions.addAll(assessmentService.getSessionsByContentId(assessment.getContentId()));

	request.setAttribute("sessions", sessions);
	request.setAttribute("questionDtos", tblQuestionDtos);
	request.setAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID, toolContentId);
	return "pages/tblmonitoring/assessmentStudentChoices";
    }

    /**
     * Used only for excell export (for getUserSummaryData() method).
     */
    private String getAssessmentCorrectAnswer(QuestionDTO questionDto) {
	StringBuilder sb = new StringBuilder();

	if (questionDto != null) {
	    switch (questionDto.getType()) {
		case QbQuestion.TYPE_ESSAY:
		    return "N.A.";

		case QbQuestion.TYPE_MATCHING_PAIRS:
		    for (OptionDTO optionDto : questionDto.getOptionDtos()) {
			sb.append((optionDto.getMatchingPair() + " - " + optionDto.getName()).replaceAll("\\<.*?\\>", "")
				+ " <br>");
		    }
		    return sb.toString();

		case QbQuestion.TYPE_MULTIPLE_CHOICE:
		case QbQuestion.TYPE_VERY_SHORT_ANSWERS:
		    for (OptionDTO optionDto : questionDto.getOptionDtos()) {
			if (optionDto.getMaxMark() == 1f) {
			    return optionDto.getName();
			}
		    }
		    break;

		case QbQuestion.TYPE_NUMERICAL:
		    for (OptionDTO optionDto : questionDto.getOptionDtos()) {
			if (optionDto.getMaxMark() == 1f) {
			    return "" + optionDto.getNumericalOption();
			}
		    }
		    break;

		case QbQuestion.TYPE_ORDERING:
		    for (OptionDTO optionDto : questionDto.getOptionDtos()) {
			sb.append(optionDto.getName() + "\n");
		    }
		    return sb.toString();

		case QbQuestion.TYPE_TRUE_FALSE:
		    return String.valueOf(questionDto.getCorrectAnswer());

		case QbQuestion.TYPE_MARK_HEDGING:
		    for (OptionDTO optionDto : questionDto.getOptionDtos()) {
			if (optionDto.isCorrect()) {
			    return optionDto.getName();
			}
		    }
		    break;

		default:
		    return null;
	    }
	}

	return null;
    }

    /**
     * Get ModalDialog for Teams tab.
     *
     * @throws JSONException
     * @throws IOException
     */
    @RequestMapping("getModalDialogForTeamsTab")
    public String getModalDialogForTeamsTab(HttpServletRequest request) throws IOException {
	long toolContentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	Long userId = WebUtil.readLongParam(request, AttributeNames.PARAM_USER_ID);

	AssessmentUser user = assessmentService.getUserByIdAndContent(userId, toolContentId);
	AssessmentResultDTO result = assessmentService.getUserMasterDetail(user.getSession().getSessionId(), userId);
	request.setAttribute(AssessmentConstants.ATTR_ASSESSMENT_RESULT, result);

	return "pages/tblmonitoring/teams";
    }

}
