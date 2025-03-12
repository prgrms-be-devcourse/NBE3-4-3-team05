# Data :            2025-03-12
# Author :          강성욱
# Description :     Schedules Checkin Table `(schedules_id)`, (`user_id`, `check_in`) 인덱스 추가

-- 외래 키 제약 조건 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 테이블 삭제 (FK 의존 관계 고려하여 순서대로 삭제)
DROP TABLE IF EXISTS schedules_checkin;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS class_user_entity;
DROP TABLE IF EXISTS class_black_list_entity;
DROP TABLE IF EXISTS class_entity;
DROP TABLE IF EXISTS users_favorite;
DROP TABLE IF EXISTS users_oauth;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS users;

-- 외래 키 제약 조건 활성화
SET FOREIGN_KEY_CHECKS = 1;

-- 데이터베이스 생성
CREATE
    DATABASE IF NOT EXISTS hobby;
USE
    hobby;

-- User Table
CREATE TABLE `users`
(
    `user_id`     bigint                             NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6)  DEFAULT NULL,
    `modified_at` datetime(6)  DEFAULT NULL,
    `login_id`    varchar(255) DEFAULT NULL,
    `nickname`    varchar(10)                        NOT NULL,
    `password`    varchar(255) DEFAULT NULL,
    `role`        enum ('ROLE_ADMIN','ROLE_USER')    NOT NULL,
    `status`      enum ('ACTIVE','DELETE','PENDING') NOT NULL,
    `type`        enum ('NORMAL','OAUTH')            NOT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `unique_nickname` (`nickname`),
    UNIQUE KEY `unique_login_id` (`login_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Favorite Table
CREATE TABLE `favorites`
(
    `favorite_id`   bigint       NOT NULL AUTO_INCREMENT,
    `favorite_name` varchar(255) NOT NULL,
    PRIMARY KEY (`favorite_id`),
    INDEX `idx_favorite_name` (`favorite_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- UserOauth Table
CREATE TABLE `users_oauth`
(
    `users_oauth_id` bigint NOT NULL AUTO_INCREMENT,
    `created_at`     datetime(6)                     DEFAULT NULL,
    `modified_at`    datetime(6)                     DEFAULT NULL,
    `provider`       enum ('GOOGLE','KAKAO','NAVER') DEFAULT NULL,
    `uid`            varchar(255)                    DEFAULT NULL,
    `user_id`        bigint                          DEFAULT NULL,
    PRIMARY KEY (`users_oauth_id`),
    UNIQUE KEY `unique_uid` (`uid`),
    KEY `fk_user_id` (`user_id`),
    CONSTRAINT `fk_user_oauth_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- User Favorite Table
CREATE TABLE `users_favorite`
(
    `user_favorite_id` bigint NOT NULL AUTO_INCREMENT,
    `favorite_id`      bigint DEFAULT NULL,
    `user_id`          bigint DEFAULT NULL,
    PRIMARY KEY (`user_favorite_id`),
    KEY `idx_favorite_id` (`favorite_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_users_favorite_favorite_id` FOREIGN KEY (`favorite_id`) REFERENCES `favorites` (`favorite_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_users_favorite_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Class Table
CREATE TABLE `class_entity`
(
    `class_id`    bigint       NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6) DEFAULT NULL,
    `modified_at` datetime(6) DEFAULT NULL,
    `description` text         NOT NULL,
    `favorite`    varchar(255) NOT NULL,
    `master_id`   bigint       NOT NULL,
    `name`        varchar(255) NOT NULL,
    PRIMARY KEY (`class_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Class Black List Table
CREATE TABLE `class_black_list_entity`
(
    `cbl_id`   bigint NOT NULL AUTO_INCREMENT,
    `user_id`  bigint NOT NULL,
    `class_id` bigint NOT NULL,
    PRIMARY KEY (`cbl_id`),
    KEY `FK_class_black_list_class_id` (`class_id`),
    CONSTRAINT `FK_class_black_list_to_class_entity` FOREIGN KEY (`class_id`) REFERENCES `class_entity` (`class_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Class User Table
CREATE TABLE `class_user_entity`
(
    `cu_id`    bigint NOT NULL AUTO_INCREMENT,
    `user_id`  bigint NOT NULL,
    `class_id` bigint NOT NULL,
    PRIMARY KEY (`cu_id`),
    KEY `idx_class_id` (`class_id`),
    CONSTRAINT `fk_class_user_entity_class_id` FOREIGN KEY (`class_id`) REFERENCES `class_entity` (`class_id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Schedule Table
CREATE TABLE `schedules`
(
    `class_id`      bigint      DEFAULT NULL,
    `created_at`    datetime(6) DEFAULT NULL,
    `modified_at`   datetime(6) DEFAULT NULL,
    `schedules_id`  bigint       NOT NULL AUTO_INCREMENT,
    `meeting_time`  varchar(255) NOT NULL,
    `meeting_title` varchar(255) NOT NULL,
    PRIMARY KEY (`schedules_id`),
    CONSTRAINT `FK_schedules_class_id` FOREIGN KEY (`class_id`) REFERENCES `class_entity` (`class_id`) ON DELETE CASCADE,
    KEY `IDX_schedules_class_id` (`class_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Schedule Checkin Table
CREATE TABLE `schedules_checkin`
(
    `sc_id`        bigint NOT NULL AUTO_INCREMENT,
    `created_at`   datetime(6) DEFAULT NULL,
    `modified_at`  datetime(6) DEFAULT NULL,
    `check_in`     bit(1) NOT NULL,
    `user_id`      bigint NOT NULL,
    `schedules_id` bigint      DEFAULT NULL,
    PRIMARY KEY (`sc_id`),
    CONSTRAINT `FK_schedules_checkin_schedules_id` FOREIGN KEY (`schedules_id`) REFERENCES `schedules` (`schedules_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

