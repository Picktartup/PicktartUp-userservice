INSERT INTO users (name, email, encrypted_pwd, role, is_activated, created_at)
VALUES
    ('김관리', 'admin1@picktartup.com', '$2a$10$YOUR_ENCRYPTED_PWD_HERE', 'ADMIN', true, CURRENT_TIMESTAMP),
    ('이어드민', 'admin2@picktartup.com', '$2a$10$YOUR_ENCRYPTED_PWD_HERE', 'ADMIN', true, CURRENT_TIMESTAMP),
    ('박지민', 'jimin@gmail.com', '$2a$10$YOUR_ENCRYPTED_PWD_HERE', 'USER', true, CURRENT_TIMESTAMP),
    ('김유진', 'yujin@naver.com', '$2a$10$YOUR_ENCRYPTED_PWD_HERE', 'USER', true, CURRENT_TIMESTAMP),
    ('이서준', 'seojun@kakao.com', '$2a$10$YOUR_ENCRYPTED_PWD_HERE', 'USER', true, CURRENT_TIMESTAMP),
    ('최민서', 'minseo@gmail.com', '$2a$10$YOUR_ENCRYPTED_PWD_HERE', 'USER', true, CURRENT_TIMESTAMP),
    ('류채현', 'lch010201@gmail.com', '$2a$10$YOUR_ENCRYPTED_PWD_HERE', 'USER', true, CURRENT_TIMESTAMP),
    ('최나영', 'dmddl282@gmail.com', '$2a$10$YOUR_ENCRYPTED_PWD_HERE', 'USER', true, CURRENT_TIMESTAMP);


