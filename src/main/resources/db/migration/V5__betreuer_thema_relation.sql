CREATE TABLE betreuer_thema
(
    betreuer_id UUID NOT NULL,
    thema_id    UUID NOT NULL,
    CONSTRAINT pk_betreuer_thema PRIMARY KEY (betreuer_id, thema_id)
);

ALTER TABLE betreuer_thema
    ADD CONSTRAINT fk_betreuer_thema_on_betreuer FOREIGN KEY (betreuer_id) REFERENCES betreuer (id) ON DELETE CASCADE;

ALTER TABLE betreuer_thema
    ADD CONSTRAINT fk_betreuer_thema_on_thema FOREIGN KEY (thema_id) REFERENCES thema (id) ON DELETE CASCADE;
