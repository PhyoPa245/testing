INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
valid_flag,
grouping_support_type_id,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
help_url,
language_file,
create_date_time,
modified_date_time,
admin_url,
supports_outputs
)
VALUES
(
'lascrt11',
'scratchieService',
'Scratchie',
'Scratchie',
'scratchie',
'@tool_version@',
0,
2,
'tool/lascrt11/learning/start.do?mode=learner',
'tool/lascrt11/learning/start.do?mode=author',
'tool/lascrt11/learning/start.do?mode=teacher',
'tool/lascrt11/authoring/start.do',
'tool/lascrt11/monitoring/summary.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/lascrt11',
'org.lamsfoundation.lams.tool.scratchie.ApplicationResources',
NOW(),
NOW(),
'tool/lascrt11/admin/start.do',
1
)