CREATE TABLE notification_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    requested_quantity BIGINT NOT NULL,
    reservation_status VARCHAR(20) NOT NULL,
    notification_type VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    notification_body VARCHAR(1000) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
