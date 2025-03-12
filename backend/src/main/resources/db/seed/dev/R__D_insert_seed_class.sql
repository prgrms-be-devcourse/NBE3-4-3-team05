INSERT INTO class_entity (name, favorite, description, master_id, created_at, modified_at)
VALUES ('테스트 모임1', '축구', '테스트 모임1의 설명입니다.', 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임2', '영화', '테스트 모임2의 설명입니다.', 2, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임3', '독서', '테스트 모임3의 설명입니다.', 3, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임4', '그림', '테스트 모임4의 설명입니다.', 4, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임5', '코딩', '테스트 모임5의 설명입니다.', 5, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임6', '음악', '테스트 모임6의 설명입니다.', 6, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임7', '축구', '테스트 모임7의 설명입니다.', 7, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임8', '영화', '테스트 모임8의 설명입니다.', 8, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임9', '독서', '테스트 모임9의 설명입니다.', 9, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
       ('테스트 모임10', '그림', '테스트 모임10의 설명입니다.', 10, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO class_user_entity (class_id, user_id)
SELECT class.class_id as class_id, user.user_id as user_id
FROM users user
         JOIN class_entity class ON class.master_id = user.user_id;
