CREATE TABLE is_scanning (
    id BIGINT NOT NULL,
    is_scanning BOOLEAN NOT NULL DEFAULT true,
    last_scan_success BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
);

INSERT INTO is_scanning (id) VALUES (1);