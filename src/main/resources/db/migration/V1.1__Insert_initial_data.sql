INSERT INTO public.users (user_id, name, email, encrypted_pwd, role, is_activated, created_at)
VALUES
    (1, '김민수', 'minsu.kim@example.com', 'encryptedpwd1', 'USER', TRUE, NOW()),
    (2, '박지영', 'jiyeong.park@example.com', 'encryptedpwd2', 'ADMIN', TRUE, NOW()),
    (3, '이현우', 'hyunwoo.lee@example.com', 'encryptedpwd3', 'USER', FALSE, NOW()),
    (4, '최수정', 'soojung.choi@example.com', 'encryptedpwd4', 'ADMIN', TRUE, NOW()),
    (5, '정하영', 'hayoung.jung@example.com', 'encryptedpwd5', 'USER', TRUE, NOW());
