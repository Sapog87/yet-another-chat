-- liquibase formatted sql

-- changeset Sergey:10
CREATE INDEX idx_message_chat_id ON message (chat_id);