DELIMITER $$
CREATE PROCEDURE InsertBatchData()
BEGIN
    DECLARE counter INT DEFAULT 1;
    WHILE counter <= 10 DO
            INSERT INTO users (login_id, password, nickname, type, status, role, created_at, modified_at)
            VALUES
                (CONCAT('test', counter, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+1, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+1), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+2, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+2), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+3, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+3), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+4, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+4), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+5, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+5), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+6, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+6), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+7, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+7), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+8, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+8), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
                (CONCAT('test', counter+9, '@email.com'), '$2a$10$w9hKeWwb7pLRVauv/6DOK.it239jAio9luYL2iSYeylIRHhysciMm', CONCAT('test', counter+9), 'NORMAL', 'ACTIVE', 'ROLE_USER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

            SET counter = counter + 10;  -- 10개의 레코드를 한 번에 추가
        END WHILE;
END$$
DELIMITER ;

CALL InsertBatchData();

DROP PROCEDURE InsertBatchData;