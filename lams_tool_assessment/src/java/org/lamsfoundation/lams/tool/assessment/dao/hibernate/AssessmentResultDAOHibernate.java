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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA 
 * 
 * http://www.gnu.org/licenses/gpl.txt 
 * **************************************************************** 
 */

/* $Id$ */
package org.lamsfoundation.lams.tool.assessment.dao.hibernate;

import java.util.List;

import org.lamsfoundation.lams.tool.assessment.dao.AssessmentResultDAO;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentResult;

public class AssessmentResultDAOHibernate extends BaseDAOHibernate implements AssessmentResultDAO {

    private static final String FIND_LAST_BY_ASSESSMENT_AND_USER = "FROM " + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId = ? AND r.assessment.uid=? AND r.latest=1";

    private static final String FIND_BY_ASSESSMENT_AND_USER_AND_FINISHED = "FROM " + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId = ? AND r.assessment.uid=? AND (r.finishDate != null) ORDER BY r.startDate ASC";

    private static final String FIND_LAST_FINISHED_BY_ASSESSMENT_AND_USER = "FROM "
	    + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId = ? AND r.assessment.uid=? AND (r.finishDate != null) AND r.latest=1";

    private static final String FIND_BY_SESSION_AND_USER = "FROM " + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId = ? AND r.sessionId=?";

    private static final String FIND_BY_SESSION_AND_USER_AND_FINISHED = "FROM " + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId = ? AND r.sessionId=? AND (r.finishDate != null) ORDER BY r.startDate ASC";

    private static final String FIND_LAST_FINISHED_BY_SESSION_AND_USER = "FROM "
	    + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId = ? AND r.sessionId=? AND (r.finishDate != null) AND r.latest=1";

    private static final String FIND_ASSESSMENT_RESULT_COUNT_BY_ASSESSMENT_AND_USER = "select COUNT(*) FROM "
	    + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId=? AND r.assessment.uid=? AND (r.finishDate != null)";

    private static final String FIND_LAST_ASSESSMENT_RESULT_GRADE = "select r.grade FROM "
	    + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId=? AND r.assessment.uid=? AND (r.finishDate != null) AND r.latest=1";

    private static final String FIND_LAST_ASSESSMENT_RESULT_TIME_TAKEN = "select UNIX_TIMESTAMP(r.finishDate) - UNIX_TIMESTAMP(r.startDate) FROM "
	    + AssessmentResult.class.getName()
	    + " AS r WHERE r.user.userId=? AND r.assessment.uid=? AND (r.finishDate != null) AND r.latest=1";

    private static final String FIND_BY_UID = "FROM " + AssessmentResult.class.getName() + " AS r WHERE r.uid = ?";

    @Override
    public List<AssessmentResult> getAssessmentResults(Long assessmentUid, Long userId) {
	return getHibernateTemplate().find(AssessmentResultDAOHibernate.FIND_BY_ASSESSMENT_AND_USER_AND_FINISHED,
		new Object[] { userId, assessmentUid });
    }

    @Override
    public List<AssessmentResult> getFinishedAssessmentResultsBySession(Long sessionId, Long userId) {
	return getHibernateTemplate().find(AssessmentResultDAOHibernate.FIND_BY_SESSION_AND_USER_AND_FINISHED,
		new Object[] { userId, sessionId });
    }

    @Override
    public List<AssessmentResult> getAssessmentResultsBySession(Long sessionId, Long userId) {
	return getHibernateTemplate().find(AssessmentResultDAOHibernate.FIND_BY_SESSION_AND_USER,
		new Object[] { userId, sessionId });
    }

    @Override
    public AssessmentResult getLastAssessmentResult(Long assessmentUid, Long userId) {
	List list = getHibernateTemplate().find(AssessmentResultDAOHibernate.FIND_LAST_BY_ASSESSMENT_AND_USER,
		new Object[] { userId, assessmentUid });
	if ((list == null) || (list.size() == 0)) {
	    return null;
	} else {
	    return (AssessmentResult) list.get(0);
	}
    }

    @Override
    public AssessmentResult getLastFinishedAssessmentResult(Long assessmentUid, Long userId) {
	List list = getHibernateTemplate().find(
		AssessmentResultDAOHibernate.FIND_LAST_FINISHED_BY_ASSESSMENT_AND_USER,
		new Object[] { userId, assessmentUid });
	if ((list == null) || (list.size() == 0)) {
	    return null;
	} else {
	    return (AssessmentResult) list.get(0);
	}
    }

    @Override
    public Float getLastFinishedAssessmentResultGrade(Long assessmentUid, Long userId) {
	List list = getHibernateTemplate().find(AssessmentResultDAOHibernate.FIND_LAST_ASSESSMENT_RESULT_GRADE,
		new Object[] { userId, assessmentUid });
	if ((list == null) || (list.size() == 0)) {
	    return null;
	} else {
	    return ((Number) list.get(0)).floatValue();
	}
    }

    @Override
    public Integer getLastFinishedAssessmentResultTimeTaken(Long assessmentUid, Long userId) {

	List list = getHibernateTemplate().find(FIND_LAST_ASSESSMENT_RESULT_TIME_TAKEN,
		new Object[] { userId, assessmentUid });
	if ((list == null) || (list.size() == 0)) {
	    return null;
	} else {
	    return ((Number) list.get(0)).intValue();
	}
    }

    @Override
    public AssessmentResult getLastFinishedAssessmentResultBySessionId(Long sessionId, Long userId) {
	List list = getHibernateTemplate().find(
		AssessmentResultDAOHibernate.FIND_LAST_FINISHED_BY_SESSION_AND_USER,
		new Object[] { userId, sessionId });
	if ((list == null) || (list.size() == 0)) {
	    return null;
	} else {
	    return (AssessmentResult) list.get(0);
	}
    }

    @Override
    public int getAssessmentResultCount(Long assessmentUid, Long userId) {
	List list = getHibernateTemplate().find(
		AssessmentResultDAOHibernate.FIND_ASSESSMENT_RESULT_COUNT_BY_ASSESSMENT_AND_USER,
		new Object[] { userId, assessmentUid });
	if ((list == null) || (list.size() == 0)) {
	    return 0;
	} else {
	    return ((Number) list.get(0)).intValue();
	}
    }

    @Override
    public AssessmentResult getAssessmentResultByUid(Long assessmentResultUid) {
	List list = getHibernateTemplate().find(AssessmentResultDAOHibernate.FIND_BY_UID,
		new Object[] { assessmentResultUid });
	if ((list == null) || (list.size() == 0)) {
	    return null;
	}
	return (AssessmentResult) list.get(0);
    }

}
