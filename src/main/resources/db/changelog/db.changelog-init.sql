-- liquibase formatted sql

-- changeset Sergey:1
CREATE SEQUENCE IF NOT EXISTS app_user_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS chat_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS message_id_seq START WITH 1 INCREMENT BY 1;

-- changeset Sergey:2
CREATE TABLE app_user
(
    id            BIGINT                      NOT NULL,
    name          TEXT                        NOT NULL,
    username      TEXT                        NOT NULL,
    password_hash TEXT                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uc_app_user_login UNIQUE (username)
);

-- changeset Sergey:3
CREATE TABLE chat
(
    id              BIGINT                      NOT NULL,
    group_chat_name TEXT,
    is_group        BOOLEAN                     NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_chat PRIMARY KEY (id)
);

-- changeset Sergey:4
CREATE TABLE app_user_chat
(
    user_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    CONSTRAINT pk_app_user_chat PRIMARY KEY (user_id, chat_id),
    CONSTRAINT fk_app_user_chat_on_chat FOREIGN KEY (chat_id) REFERENCES chat (id),
    CONSTRAINT fk_app_user_chat_on_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

-- changeset Sergey:5
CREATE TABLE app_user_roles
(
    user_id BIGINT NOT NULL,
    roles   TEXT,
    CONSTRAINT fk_app_user_roles_on_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

-- changeset Sergey:6
CREATE TABLE message
(
    id         BIGINT                      NOT NULL,
    sender_id  BIGINT                      NOT NULL,
    text       TEXT                        NOT NULL,
    chat_id    BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_message PRIMARY KEY (id),
    CONSTRAINT fk_message_on_chat FOREIGN KEY (chat_id) REFERENCES chat (id),
    CONSTRAINT fk_message_on_sender FOREIGN KEY (sender_id) REFERENCES app_user (id)
);

-- changeset Sergey:7
CREATE INDEX idx_app_user_name ON app_user (name);

-- changeset Sergey:8
CREATE INDEX idx_chat_group_chat_name ON chat (group_chat_name);

-- changeset Sergey:9
CREATE INDEX idx_app_user_roles_user_id ON app_user_roles (user_id);
