ALTER TABLE thema
    ALTER COLUMN titel SET NOT NULL;

ALTER TABLE thema
    ADD CONSTRAINT chk_thema_titel_not_blank
        CHECK (length(trim(titel)) > 0);
