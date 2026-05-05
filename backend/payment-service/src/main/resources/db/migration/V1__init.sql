CREATE TABLE IF NOT EXISTS payments (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id          BIGINT NOT NULL UNIQUE,
    user_id             BIGINT NOT NULL,
    user_email          VARCHAR(150),
    razorpay_order_id   VARCHAR(100),
    razorpay_payment_id VARCHAR(100),
    razorpay_signature  VARCHAR(200),
    amount              DECIMAL(10,2) NOT NULL,
    currency            VARCHAR(10),
    status              VARCHAR(30) NOT NULL,
    reason              VARCHAR(300),
    created_at          DATETIME NOT NULL,
    updated_at          DATETIME,
    INDEX idx_booking (booking_id),
    INDEX idx_user (user_id)
);
