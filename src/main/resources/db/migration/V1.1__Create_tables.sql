-- users 테이블 생성
CREATE TABLE public.users (
    user_id     BIGINT      NOT NULL DEFAULT nextval('users_seq'),
    name        VARCHAR(50) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    encrypted_pwd VARCHAR(100) NOT NULL,
    role        VARCHAR(255) NOT NULL,
    is_activated BOOLEAN NOT NULL DEFAULT true,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_pkey PRIMARY KEY (user_id)
);

-- 시퀀스 소유권 설정
ALTER SEQUENCE users_seq OWNED BY users.user_id;