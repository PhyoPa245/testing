package org.lamsfoundation.lams.outcome.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.lamsfoundation.lams.outcome.Outcome;
import org.lamsfoundation.lams.outcome.OutcomeMapping;
import org.lamsfoundation.lams.outcome.OutcomeResult;
import org.lamsfoundation.lams.outcome.OutcomeScale;
import org.lamsfoundation.lams.outcome.OutcomeScaleItem;
import org.lamsfoundation.lams.outcome.dao.IOutcomeDAO;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.ExcelCell;
import org.lamsfoundation.lams.util.FileUtil;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.multipart.MultipartFile;

public class OutcomeService implements IOutcomeService {
    private IOutcomeDAO outcomeDAO;
    private MessageService messageService;

    private static Logger log = Logger.getLogger(OutcomeService.class);

    @Override
    public String getContentFolderId(Integer organisationId) {
	String contentFolderId = outcomeDAO.getContentFolderID(organisationId);
	return contentFolderId == null ? FileUtil.generateUniqueContentFolderID() : contentFolderId;
    }

    @Override
    public List<Outcome> getOutcomes(Integer organisationId) {
	return outcomeDAO.getOutcomesSortedByName(organisationId);
    }

    @Override
    public List<OutcomeScale> getScales(Integer organisationId) {
	return outcomeDAO.getScalesSortedByName(organisationId);
    }

    @Override
    public List<Outcome> getOutcomes(String search, Set<Integer> organisationIds) {
	if (organisationIds == null) {
	    Integer userId = OutcomeService.getUserDTO().getUserID();
	    organisationIds = new HashSet<Integer>(outcomeDAO.getAuthorOrganisations(userId));
	}
	return outcomeDAO.getOutcomesSortedByName(search, organisationIds);
    }

    @Override
    public List<OutcomeMapping> getOutcomeMappings(Long lessonId, Long toolContentId, Long itemId) {
	return outcomeDAO.getOutcomeMappings(lessonId, toolContentId, itemId);
    }

    @Override
    public List<OutcomeResult> getOutcomeResults(Integer userId, Long lessonId, Long toolContentId, Long itemId) {
	return outcomeDAO.getOutcomeResults(userId, lessonId, toolContentId, itemId);
    }

    @Override
    public OutcomeResult getOutcomeResult(Integer userId, Long mappingId) {
	return outcomeDAO.getOutcomeResult(userId, mappingId);
    }

    @Override
    public OutcomeScale getDefaultScale() {
	return (OutcomeScale) outcomeDAO.find(OutcomeScale.class, DEFAULT_SCALE_ID);
    }

    @Override
    public boolean isDefaultScale(Long scaleId) {
	return scaleId != null && DEFAULT_SCALE_ID == scaleId;
    }

    @Override
    public void copyOutcomeMappings(Long sourceLessonId, Long sourceToolContentId, Long sourceItemId,
	    Long targetLessonId, Long targetToolContentId, Long targetItemId) {
	List<OutcomeMapping> sourceMappings = getOutcomeMappings(sourceLessonId, sourceToolContentId, sourceItemId);
	for (OutcomeMapping sourceMapping : sourceMappings) {
	    OutcomeMapping targetMapping = new OutcomeMapping();
	    targetMapping.setOutcome(sourceMapping.getOutcome());
	    targetMapping.setLessonId(targetLessonId);
	    targetMapping.setToolContentId(targetToolContentId);
	    targetMapping.setItemId(targetItemId);
	    outcomeDAO.insert(targetMapping);
	}
    }

    @Override
    public LinkedHashMap<String, ExcelCell[][]> exportScales() {
	LinkedHashMap<String, ExcelCell[][]> dataToExport = new LinkedHashMap<String, ExcelCell[][]>();

	// The entire data list
	List<ExcelCell[]> rowList = new LinkedList<ExcelCell[]>();
	ExcelCell[] row = new ExcelCell[4];
	row[0] = new ExcelCell(messageService.getMessage("outcome.manage.add.name"), true);
	row[1] = new ExcelCell(messageService.getMessage("outcome.manage.add.code"), true);
	row[2] = new ExcelCell(messageService.getMessage("outcome.manage.add.description"), true);
	row[3] = new ExcelCell(messageService.getMessage("scale.manage.add.value"), true);
	rowList.add(row);

	List<OutcomeScale> scales = getScales(null);
	for (OutcomeScale scale : scales) {
	    row = new ExcelCell[4];
	    row[0] = new ExcelCell(scale.getName(), false);
	    row[1] = new ExcelCell(scale.getCode(), false);
	    row[2] = new ExcelCell(scale.getDescription(), false);
	    row[3] = new ExcelCell(scale.getItemString(), false);
	    rowList.add(row);
	}

	ExcelCell[][] data = rowList.toArray(new ExcelCell[][] {});
	dataToExport.put(messageService.getMessage("scale.title"), data);
	return dataToExport;
    }

    @Override
    public LinkedHashMap<String, ExcelCell[][]> exportOutcomes() {
	LinkedHashMap<String, ExcelCell[][]> dataToExport = new LinkedHashMap<String, ExcelCell[][]>();

	// The entire data list
	List<ExcelCell[]> rowList = new LinkedList<ExcelCell[]>();
	ExcelCell[] row = new ExcelCell[4];
	row[0] = new ExcelCell(messageService.getMessage("outcome.manage.add.name"), true);
	row[1] = new ExcelCell(messageService.getMessage("outcome.manage.add.code"), true);
	row[2] = new ExcelCell(messageService.getMessage("outcome.manage.add.description"), true);
	row[3] = new ExcelCell(messageService.getMessage("outcome.manage.add.scale"), true);
	rowList.add(row);

	List<Outcome> outcomes = getOutcomes(null);
	for (Outcome outcome : outcomes) {
	    row = new ExcelCell[4];
	    row[0] = new ExcelCell(outcome.getName(), false);
	    row[1] = new ExcelCell(outcome.getCode(), false);
	    row[2] = new ExcelCell(outcome.getDescription(), false);
	    row[3] = new ExcelCell(outcome.getScale().getCode(), false);
	    rowList.add(row);
	}

	ExcelCell[][] data = rowList.toArray(new ExcelCell[][] {});
	dataToExport.put(messageService.getMessage("index.outcome.manage"), data);
	return dataToExport;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int importScales(MultipartFile fileItem) throws IOException {
	POIFSFileSystem fs = new POIFSFileSystem(fileItem.getInputStream());
	HSSFWorkbook wb = new HSSFWorkbook(fs);
	HSSFSheet sheet = wb.getSheetAt(0);
	int startRow = sheet.getFirstRowNum();
	int endRow = sheet.getLastRowNum();
	User user = null;
	int counter = 0;

	for (int i = startRow + 5; i < (endRow + 1); i++) {
	    HSSFRow row = sheet.getRow(i);
	    HSSFCell cell = row.getCell(1);
	    String code = cell.getStringCellValue();
	    List<OutcomeScale> foundScales = outcomeDAO.findByProperty(OutcomeScale.class, "code", code);
	    if (!foundScales.isEmpty()) {
		if (log.isDebugEnabled()) {
		    log.debug("Skipping an outcome scale with existing code: " + code);
		}
		continue;
	    }
	    cell = row.getCell(0);
	    String name = cell.getStringCellValue();
	    cell = row.getCell(2);
	    String description = cell.getStringCellValue();
	    cell = row.getCell(3);
	    String itemsString = cell.getStringCellValue();

	    OutcomeScale scale = new OutcomeScale();
	    scale.setName(name);
	    scale.setCode(code);
	    scale.setDescription(description);
	    if (user == null) {
		UserDTO userDTO = OutcomeService.getUserDTO();
		user = (User) outcomeDAO.find(User.class, userDTO.getUserID());
	    }
	    scale.setCreateBy(user);
	    scale.setCreateDateTime(new Date());
	    outcomeDAO.insert(scale);

	    List<String> items = OutcomeScale.parseItems(itemsString);
	    int value = 0;
	    for (String itemString : items) {
		OutcomeScaleItem item = new OutcomeScaleItem();
		item.setName(itemString);
		item.setValue(value++);
		item.setScale(scale);
		outcomeDAO.insert(item);
	    }

	    counter++;
	}
	return counter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int importOutcomes(MultipartFile fileItem) throws IOException {
	POIFSFileSystem fs = new POIFSFileSystem(fileItem.getInputStream());
	HSSFWorkbook wb = new HSSFWorkbook(fs);
	HSSFSheet sheet = wb.getSheetAt(0);
	int startRow = sheet.getFirstRowNum();
	int endRow = sheet.getLastRowNum();
	User user = null;
	int counter = 0;

	for (int i = startRow + 5; i < (endRow + 1); i++) {
	    HSSFRow row = sheet.getRow(i);
	    HSSFCell cell = row.getCell(1);
	    String code = cell.getStringCellValue();
	    List<Outcome> foundOutcomes = outcomeDAO.findByProperty(Outcome.class, "code", code);
	    if (!foundOutcomes.isEmpty()) {
		if (log.isDebugEnabled()) {
		    log.debug("Skipping an outcome with existing code: " + code);
		}
		continue;
	    }
	    cell = row.getCell(3);
	    String scaleCode = cell.getStringCellValue();
	    List<OutcomeScale> foundScales = outcomeDAO.findByProperty(OutcomeScale.class, "code", scaleCode);
	    OutcomeScale scale = foundScales.isEmpty() ? null : foundScales.get(0);
	    if (scale == null) {
		if (log.isDebugEnabled()) {
		    log.debug("Skipping an outcome with missing scale with code: " + scaleCode);
		}
		continue;
	    }
	    cell = row.getCell(0);
	    String name = cell.getStringCellValue();
	    cell = row.getCell(2);
	    String description = cell.getStringCellValue();

	    Outcome outcome = new Outcome();
	    outcome.setName(name);
	    outcome.setCode(code);
	    outcome.setDescription(description);
	    outcome.setScale(scale);
	    if (user == null) {
		UserDTO userDTO = OutcomeService.getUserDTO();
		user = (User) outcomeDAO.find(User.class, userDTO.getUserID());
	    }
	    outcome.setCreateBy(user);
	    outcome.setCreateDateTime(new Date());
	    outcomeDAO.insert(outcome);

	    counter++;
	}
	return counter;
    }

    private static UserDTO getUserDTO() {
	HttpSession ss = SessionManager.getSession();
	return (UserDTO) ss.getAttribute(AttributeNames.USER);
    }

    public void setOutcomeDAO(IOutcomeDAO outcomeDAO) {
	this.outcomeDAO = outcomeDAO;
    }

    public void setMessageService(MessageService messageService) {
	this.messageService = messageService;
    }
}