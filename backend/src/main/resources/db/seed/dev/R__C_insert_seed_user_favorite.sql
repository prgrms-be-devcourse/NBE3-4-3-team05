# 모든 회원이 모든 관심사를 등록
INSERT INTO users_favorite (user_id, favorite_id)
SELECT u.user_id, f.favorite_id
FROM users u
         CROSS JOIN favorites f;
