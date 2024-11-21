CREATE TABLE public.users
(
    user_id     int8         NOT NULL GENERATED ALWAYS AS IDENTITY,
    name        varchar(50)  NOT NULL,
    email       varchar(100) NOT NULL UNIQUE,
    encrypted_pwd varchar(100) NOT NULL,
    role        varchar(50)  NOT NULL,
    is_activated boolean     NOT NULL DEFAULT TRUE,
    created_at  timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    wallet_id   int8         NULL,
    CONSTRAINT users_pkey PRIMARY KEY (user_id)
);
