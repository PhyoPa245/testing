-- Turn off autocommit, so nothing is committed if there is an error
SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS=0;
----------------------Put all sql statements below here-------------------------
 
-- LDEV-3147 Simplify tools: get rid of instructions tab, define in monitor and offline activity options
ALTER TABLE tl_lasbmt11_content MODIFY title varchar(255) not null;
ALTER TABLE tl_lasbmt11_content MODIFY reflect_instructions text;

UPDATE lams_tool SET tool_version='20140407' WHERE tool_signature='lasbmt11';

----------------------Put all sql statements above here-------------------------

-- If there were no errors, commit and restore autocommit to on
COMMIT;
SET AUTOCOMMIT = 1;
SET FOREIGN_KEY_CHECKS=1;