package org.lamsfoundation.lams.outcome.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.outcome.Outcome;
import org.lamsfoundation.lams.outcome.OutcomeMapping;
import org.lamsfoundation.lams.outcome.OutcomeResult;
import org.lamsfoundation.lams.outcome.OutcomeScale;
import org.lamsfoundation.lams.util.ExcelCell;

public interface IOutcomeService {
    static final long DEFAULT_SCALE_ID = 1;

    String getContentFolderId(Integer organisationId);

    List<Outcome> getOutcomes(Integer organisationId);

    List<Outcome> getOutcomes(String search, Set<Integer> organisationIds);

    List<OutcomeMapping> getOutcomeMappings(Long lessonId, Long toolContentId, Long itemId);

    List<OutcomeScale> getScales(Integer organisationId);

    List<OutcomeResult> getOutcomeResults(Integer userId, Long lessonId, Long toolContentId, Long itemId);

    OutcomeResult getOutcomeResult(Integer userId, Long mappingId);

    OutcomeScale getDefaultScale();

    boolean isDefaultScale(Long scaleId);

    void copyOutcomeMappings(Long sourceLessonId, Long sourceToolContentId, Long sourceItemId, Long targetLessonId,
	    Long targetToolContentId, Long targetItemId);

    LinkedHashMap<String, ExcelCell[][]> exportScales();

    LinkedHashMap<String, ExcelCell[][]> exportOutcomes();

    int importScales(FormFile fileItem) throws IOException;

    int importOutcomes(FormFile fileItem) throws IOException;
}