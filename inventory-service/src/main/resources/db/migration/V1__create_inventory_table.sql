CREATE TABLE inventory
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(255),
    quantity     BIGINT,
    price DOUBLE,
    PRIMARY KEY (id)
);