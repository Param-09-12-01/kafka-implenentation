CREATE TABLE failed_svc_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    svc_name VARCHAR(150) NOT NULL,
    failed_reason VARCHAR(1000) NOT NULL,
    failed_time DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
