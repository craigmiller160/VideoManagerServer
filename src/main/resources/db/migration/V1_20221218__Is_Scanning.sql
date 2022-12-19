CREATE SEQUENCE is_scanning_id_seq START 1;
CREATE TABLE is_scanning (
    id BIGINT NOT NULL DEFAULT nextval('is_scanning_id_seq'::regclass),
    is_scanning BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 1
    PRIMARY KEY (id)
);

INSERT INTO is_scanning (is_scanning) VALUES (false);