CREATE TABLE is_scanning (
    id BIGINT NOT NULL,
    is_scanning BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
);

INSERT INTO is_scanning (id, is_scanning) VALUES (1, false);