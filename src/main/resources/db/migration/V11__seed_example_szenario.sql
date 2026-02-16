INSERT INTO betreuer (id, name, email, matrikelnummer, github_login)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'Karla Turing',
    'karla.turing@uni.de',
    'M-2001',
    NULL
);

INSERT INTO betreuer_fachgebiet (betreuer_id, bezeichnung)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Compilerbau'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Formale Methoden');

INSERT INTO betreuer_informationsdatei (
    betreuer_id,
    titel,
    beschreibung,
    uploader_name,
    upload_datum,
    dateisystem_pfad,
    dateityp,
    original_dateiname,
    groesse_in_bytes
)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'Betreuungsleitfaden',
    'Hinweise zum Ablauf und zu Erwartungen bei Abschlussarbeiten.',
    'Admin',
    TIMESTAMP '2026-02-16 08:00:00',
    'karla-leitfaden.md',
    'MARKDOWN',
    'betreuungsleitfaden.md',
    0
);
