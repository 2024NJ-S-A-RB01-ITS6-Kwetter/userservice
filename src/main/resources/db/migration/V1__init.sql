
CREATE TABLE userdto
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    bio      VARCHAR(255)          NULL,
    email    VARCHAR(255)          NULL,
    username VARCHAR(255)          NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);