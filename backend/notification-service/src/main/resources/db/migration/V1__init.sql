CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    type        VARCHAR(60) NOT NULL,
    title       VARCHAR(200) NOT NULL,
    message     VARCHAR(1000) NOT NULL,
    channel     VARCHAR(20),
    read_flag   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  DATETIME NOT NULL,
    INDEX idx_user_read (user_id, read_flag)
);
