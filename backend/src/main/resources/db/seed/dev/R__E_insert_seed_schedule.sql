-- 각 클래스마다 3개의 일정 생성
-- 첫 번째 일정 (i = 1)
INSERT INTO schedules (class_id, meeting_time, meeting_title, meeting_place, lat, lng, created_at, modified_at)
SELECT class_id,
       DATE_ADD(CURRENT_DATE(), INTERVAL 1 WEEK) AS meeting_time,
       CONCAT('모임 ', class_id, '의 1번째 일정') AS meeting_title,
       CONCAT('서울특별시 강남구 테헤란로 10길') AS meeting_place,
       37.5665 + (1 * 0.001) AS lat,
       126.9780 + (1 * 0.001) AS lng,
       NOW(6) AS created_at,
       NOW(6) AS modified_at
FROM class_entity;

-- 두 번째 일정 (i = 2)
INSERT INTO schedules (class_id, meeting_time, meeting_title, meeting_place, lat, lng, created_at, modified_at)
SELECT class_id,
       DATE_ADD(CURRENT_DATE(), INTERVAL 2 WEEK) AS meeting_time,
       CONCAT('모임 ', class_id, '의 2번째 일정') AS meeting_title,
       CONCAT('서울특별시 강남구 테헤란로 20길') AS meeting_place,
       37.5665 + (2 * 0.001) AS lat,
       126.9780 + (2 * 0.001) AS lng,
       NOW(6) AS created_at,
       NOW(6) AS modified_at
FROM class_entity;

-- 세 번째 일정 (i = 3)
INSERT INTO schedules (class_id, meeting_time, meeting_title, meeting_place, lat, lng, created_at, modified_at)
SELECT class_id,
       DATE_ADD(CURRENT_DATE(), INTERVAL 3 WEEK) AS meeting_time,
       CONCAT('모임 ', class_id, '의 3번째 일정') AS meeting_title,
       CONCAT('서울특별시 강남구 테헤란로 30길') AS meeting_place,
       37.5665 + (3 * 0.001) AS lat,
       126.9780 + (3 * 0.001) AS lng,
       NOW(6) AS created_at,
       NOW(6) AS modified_at
FROM class_entity;
