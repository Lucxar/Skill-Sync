ALTER TABLE betreuer_informationsdatei
    ADD COLUMN dateityp VARCHAR,
    ADD COLUMN original_dateiname VARCHAR,
    ADD COLUMN groesse_in_bytes BIGINT;
