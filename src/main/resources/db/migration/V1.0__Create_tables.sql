CREATE TABLE public.users (
    user_id     int8         NOT NULL GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    encrypted_pwd VARCHAR(100) NOT NULL,
    role VARCHAR(255) NOT NULL,
    is_activated BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_pkey PRIMARY KEY (user_id)
);