CREATE SEQUENCE categories_category_id_seq START 1;
CREATE TABLE categories (
    category_id BIGINT NOT NULL DEFAULT nextval('categories_category_id_seq'::regclass),
    category_name VARCHAR(255) NOT NULL,
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (category_id)
);

CREATE SEQUENCE series_series_id_seq START 1;
CREATE TABLE series (
    series_id BIGINT NOT NULL DEFAULT nextval('series_series_id_seq'::regclass),
    series_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (series_id)
);

CREATE SEQUENCE stars_star_id_seq START 1;
CREATE TABLE stars (
    star_id BIGINT NOT NULL DEFAULT nextval('stars_star_id_seq'::regclass),
    star_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (star_id)
);

CREATE SEQUENCE video_files_file_id_seq START 1;
CREATE TABLE video_files (
     file_id BIGINT NOT NULL DEFAULT nextval('video_files_file_id_seq'::regclass),
     file_name TEXT NOT NULL,
     display_name TEXT,
     description TEXT,
     last_modified TIMESTAMP NOT NULL,
     file_added TIMESTAMP NOT NULL,
     last_viewed TIMESTAMP NOT NULL,
     active BOOLEAN NOT NULL DEFAULT true,
     last_scan_timestamp TIMESTAMP NOT NULL,
     view_count INT NOT NULL DEFAULT 0,
     PRIMARY KEY (file_id),
     UNIQUE (file_name)
);

CREATE TABLE settings (
    settings_id BIGINT NOT NULL,
    root_dir TEXT NOT NULL,
    PRIMARY KEY (settings_id)
);

CREATE TABLE file_categories (
    file_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (file_id, category_id),
    FOREIGN KEY (file_id) REFERENCES video_files (file_id),
    FOREIGN KEY (category_id) REFERENCES categories (category_id)
);

CREATE TABLE file_series (
    file_id BIGINT NOT NULL,
    series_id BIGINT NOT NULL,
    PRIMARY KEY (file_id, series_id),
    FOREIGN KEY (file_id) REFERENCES video_files (file_id),
    FOREIGN KEY (series_id) REFERENCES series (series_id)
);

CREATE TABLE file_stars (
    file_id BIGINT NOT NULL,
    star_id BIGINT NOT NULL,
    PRIMARY KEY (file_id, star_id),
    FOREIGN KEY (file_id) REFERENCES video_files (file_id),
    FOREIGN KEY (star_id) REFERENCES stars (star_id)
);