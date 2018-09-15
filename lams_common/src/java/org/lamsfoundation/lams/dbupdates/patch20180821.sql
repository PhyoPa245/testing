-- Turn off autocommit, so nothing is committed if there is an error
SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS=0;
----------------------Put all sql statements below here-------------------------

-- LDEV-4440 Update system tool access URLs after migration to Spring MVC
UPDATE lams_system_tool SET
learner_url = 'learning/grouping/performGrouping.do',
learner_preview_url = 'learning/grouping/performGrouping.do',
learner_progress_url = 'learning/grouping/viewGrouping.do?mode=teacher'
WHERE system_tool_id = 1;

UPDATE lams_system_tool SET
learner_url = 'learning/gate/knockGate.do',
learner_preview_url = 'learning/gate/knockGate.do'
WHERE system_tool_id = 2;

UPDATE lams_system_tool SET
learner_url = 'learning/gate/knockGate.do',
learner_preview_url = 'learning/gate/knockGate.do'
WHERE system_tool_id = 3;

UPDATE lams_system_tool SET
learner_url = 'learning/gate/knockGate.do',
learner_preview_url = 'learning/gate/knockGate.do'
WHERE system_tool_id = 4;

UPDATE lams_system_tool SET
learner_url = 'learning/gate/knockGate.do',
learner_preview_url = 'learning/gate/knockGate.do'
WHERE system_tool_id = '5';

UPDATE lams_system_tool SET
learner_url = 'learning/branching/performBranching.do',
learner_preview_url = 'learning/branching/performBranching.do'
WHERE system_tool_id = '6';

UPDATE lams_system_tool SET
learner_url = 'learning/branching/performBranching.do',
learner_preview_url = 'learning/branching/performBranching.do'
WHERE system_tool_id = '7';

UPDATE lams_system_tool SET
learner_url = 'learning/branching/performBranching.do',
learner_preview_url = 'learning/branching/performBranching.do'
WHERE system_tool_id = '8';

UPDATE lams_system_tool SET
learner_url = 'learning/gate/knockGate.do',
learner_preview_url = 'learning/gate/knockGate.do'
WHERE system_tool_id = '10';

-- LDEV-4645 Add columns for default lesson settings
ALTER TABLE lams_ext_server_org_map
	ADD COLUMN learner_presence_avail TINYINT(1) DEFAULT 0,
	ADD COLUMN learner_im_avail TINYINT(1) DEFAULT 0,
	ADD COLUMN live_edit_enabled TINYINT(1) DEFAULT 1,
	ADD COLUMN enable_lesson_notifications TINYINT(1) DEFAULT 1,
	ADD COLUMN force_restart TINYINT(1) DEFAULT 0,
	ADD COLUMN allow_restart TINYINT(1) DEFAULT 0,
	ADD COLUMN gradebook_on_complete TINYINT(1) DEFAULT 1;

----------------------Put all sql statements above here-------------------------

-- If there were no errors, commit and restore autocommit to on
COMMIT;
SET AUTOCOMMIT = 1;
SET FOREIGN_KEY_CHECKS=1;
