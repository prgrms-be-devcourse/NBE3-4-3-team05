# Data :            2025-03-13
# Author :          강성욱
# Description :     Schedules column(위도, 경도, 모임 장소) 추가.

ALTER TABLE `schedules`
    ADD COLUMN `lat` DOUBLE NOT NULL,
    ADD COLUMN `lng` DOUBLE NOT NULL,
    ADD COLUMN `meeting_place` VARCHAR(255) NOT NULL;
