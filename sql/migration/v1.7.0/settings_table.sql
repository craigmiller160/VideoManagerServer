-- Already run in dev and prod
CREATE TABLE settings (
    settings_id bigserial,
    root_dir VARCHAR(255),
    primary key (settings_id)
);