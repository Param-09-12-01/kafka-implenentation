CREATE TABLE orders
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    quantity BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);