-- liquibase formatted sql

-- changeset Sergey:11
CREATE SEQUENCE IF NOT EXISTS role_id_seq START WITH 1 INCREMENT BY 1;

-- changeset Sergey:12
CREATE TABLE role
(
    id        BIGINT NOT NULL,
    role_name TEXT   NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id),
    CONSTRAINT uc_role_role_name UNIQUE (role_name)
);

-- changeset Sergey:13
INSERT INTO role (id, role_name)
VALUES (nextval('role_id_seq'), 'USER');
INSERT INTO role (id, role_name)
VALUES (nextval('role_id_seq'), 'ADMIN');

-- changeset Sergey:14
CREATE TABLE app_user_role
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_app_user_role PRIMARY KEY (role_id, user_id),
    CONSTRAINT fk_app_user_role_on_role FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_app_user_role_on_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

-- changeset Sergey:15
INSERT INTO app_user_role (role_id, user_id)
SELECT r.id, au.id
FROM app_user au
         JOIN app_user_roles aur ON au.id = aur.user_id
         JOIN role r On r.role_name = aur.roles;

-- changeset Sergey:16
ALTER TABLE app_user_roles
    DROP CONSTRAINT fk_app_user_roles_on_user;

-- changeset Sergey:17
DROP TABLE app_user_roles CASCADE;