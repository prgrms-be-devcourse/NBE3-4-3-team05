# Data :            2025-03-12
# Author :          강성욱
# Description :     Table 초기 설정 진행

CREATE INDEX `IDX_schedules_checkin_schedules_id`
    ON `schedules_checkin` (`schedules_id`);

CREATE INDEX `idx_user_checkin_user_id_check_in`
    ON `schedules_checkin` (`user_id`, `check_in`);