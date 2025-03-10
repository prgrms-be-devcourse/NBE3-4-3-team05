#!/bin/bash

# MySQL 서버가 준비될 때까지 대기
echo "$(date '+%Y-%m-%d %H:%M:%S') - MySQL 서버 준비 중, 대기 시간: 5초"
sleep 5

# 테이블 및 스키마 생성
echo "$(date '+%Y-%m-%d %H:%M:%S') - hobby 데이터베이스와 테이블 생성"
mysql -u root -p${MYSQL_ROOT_PASSWORD} <<EOF
CREATE DATABASE IF NOT EXISTS hobby;
USE hobby;

# User Table
CREATE TABLE \`users\` (
  \`user_id\` bigint NOT NULL AUTO_INCREMENT,
  \`created_at\` datetime(6) DEFAULT NULL,
  \`modified_at\` datetime(6) DEFAULT NULL,
  \`login_id\` varchar(255) DEFAULT NULL,
  \`nickname\` varchar(10) NOT NULL,
  \`password\` varchar(255) DEFAULT NULL,
  \`role\` enum('ROLE_ADMIN','ROLE_USER') NOT NULL,
  \`status\` enum('ACTIVE','DELETE','PENDING') NOT NULL,
  \`type\` enum('NORMAL','OAUTH') NOT NULL,
  PRIMARY KEY (\`user_id\`),
  UNIQUE KEY \`unique_nickname\` (\`nickname\`),
  UNIQUE KEY \`unique_login_id\` (\`login_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#Favorite Table
CREATE TABLE \`favorites\` (
  \`favorite_id\` bigint NOT NULL AUTO_INCREMENT,
  \`favorite_name\` varchar(255) NOT NULL,
  PRIMARY KEY (\`favorite_id\`),
  INDEX \`idx_favorite_name\` (\`favorite_name\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# UserOauth TABLE
CREATE TABLE \`users_oauth\` (
  \`users_oauth_id\` bigint NOT NULL AUTO_INCREMENT,
  \`created_at\` datetime(6) DEFAULT NULL,
  \`modified_at\` datetime(6) DEFAULT NULL,
  \`provider\` enum('GOOGLE','KAKAO','NAVER') DEFAULT NULL,
  \`uid\` varchar(255) DEFAULT NULL,
  \`user_id\` bigint DEFAULT NULL,
  PRIMARY KEY (\`users_oauth_id\`),
  UNIQUE KEY \`unique_uid\` (\`uid\`),
  KEY \`fk_user_id\` (\`user_id\`),
  CONSTRAINT \`fk_user_oauth_user_id\` FOREIGN KEY (\`user_id\`) REFERENCES \`users\` (\`user_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# User Favorite Table
CREATE TABLE \`users_favorite\` (
  \`user_favorite_id\` bigint NOT NULL AUTO_INCREMENT,
  \`favorite_id\` bigint DEFAULT NULL,
  \`user_id\` bigint DEFAULT NULL,
  PRIMARY KEY (\`user_favorite_id\`),
  KEY \`idx_favorite_id\` (\`favorite_id\`),
  KEY \`idx_user_id\` (\`user_id\`),
  CONSTRAINT \`fk_users_favorite_favorite_id\` FOREIGN KEY (\`favorite_id\`) REFERENCES \`favorites\` (\`favorite_id\`),
  CONSTRAINT \`fk_users_favorite_user_id\` FOREIGN KEY (\`user_id\`) REFERENCES \`users\` (\`user_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# Class Entity
CREATE TABLE IF NOT EXISTS \`class_entity\` (
  \`class_id\` bigint NOT NULL AUTO_INCREMENT,
  \`created_at\` datetime(6) DEFAULT NULL,
  \`modified_at\` datetime(6) DEFAULT NULL,
  \`description\` text NOT NULL,
  \`favorite\` varchar(255) NOT NULL,
  \`master_id\` bigint NOT NULL,
  \`name\` varchar(255) NOT NULL,
  PRIMARY KEY (\`class_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# Class Black List 생성
CREATE TABLE IF NOT EXISTS \`class_black_list_entity\` (
  \`cbl_id\` bigint NOT NULL AUTO_INCREMENT,
  \`user_id\` bigint NOT NULL,
  \`class_id\` bigint NOT NULL,
  PRIMARY KEY (\`cbl_id\`),
  KEY \`FK_class_black_list_class_id\` (\`class_id\`),
  CONSTRAINT \`FK_class_black_list_to_class_entity\` FOREIGN KEY (\`class_id\`) REFERENCES \`class_entity\` (\`class_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# Class User Table
CREATE TABLE \`class_user_entity\` (
  \`cu_id\` bigint NOT NULL AUTO_INCREMENT,
  \`user_id\` bigint NOT NULL,
  \`class_id\` bigint NOT NULL,
  PRIMARY KEY (\`cu_id\`),
  KEY \`idx_class_id\` (\`class_id\`),
  CONSTRAINT \`fk_class_user_entity_class_id\` FOREIGN KEY (\`class_id\`) REFERENCES \`class_entity\` (\`class_id\`),
  INDEX \`idx_user_id\` (\`user_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# Schedule Table
CREATE TABLE \`schedules\` (
  \`schedules_id\` bigint NOT NULL AUTO_INCREMENT,
  \`created_at\` datetime(6) DEFAULT NULL,
  \`modified_at\` datetime(6) DEFAULT NULL,
  \`meeting_time\` varchar(255) NOT NULL,
  \`meeting_title\` varchar(255) NOT NULL,
  \`class_id\` bigint DEFAULT NULL,
  PRIMARY KEY (\`schedules_id\`),
  CONSTRAINT \`FK_schedules_class_id\` FOREIGN KEY (\`class_id\`) REFERENCES \`class_entity\` (\`class_id\`),
  KEY \`IDX_schedules_class_id\` (\`class_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# Schedule Checkin Table
CREATE TABLE \`schedules_checkin\` (
  \`sc_id\` bigint NOT NULL AUTO_INCREMENT,
  \`created_at\` datetime(6) DEFAULT NULL,
  \`modified_at\` datetime(6) DEFAULT NULL,
  \`check_in\` bit(1) NOT NULL,
  \`user_id\` bigint NOT NULL,
  \`schedules_id\` bigint DEFAULT NULL,
  PRIMARY KEY (\`sc_id\`),
  CONSTRAINT \`FK_schedules_checkin_schedules_id\` FOREIGN KEY (\`schedules_id\`) REFERENCES \`schedules\` (\`schedules_id\`),
  KEY \`IDX_schedules_checkin_schedules_id\` (\`schedules_id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# Exporter 사용자 생성
CREATE USER IF NOT EXISTS '${EXPORTER_NAME}'@'%' IDENTIFIED BY '${EXPORTER_PASSWORD}' WITH MAX_USER_CONNECTIONS 3;
GRANT PROCESS, REPLICATION CLIENT ON *.* TO '${EXPORTER_NAME}'@'%';
FLUSH PRIVILEGES;
EOF

# 로그: 쿼리 실행 후
echo "$(date '+%Y-%m-%d %H:%M:%S') - hobby 데이터베이스와 exporter 사용자 생성 완료"

# 스크립트 종료 로그
echo "$(date '+%Y-%m-%d %H:%M:%S') - MySQL 초기화 완료"
