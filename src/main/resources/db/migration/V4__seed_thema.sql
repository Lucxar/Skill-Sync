INSERT INTO thema (id, titel, beschreibung_markdown)
VALUES (
    '3f7d8e1f-2b4a-4f76-9a8d-6b5f1c2d3e40',
    'Machine Learning for Climate Data',
    '## Kurzbeschreibung\n\nAnalyse von Klimadaten mit ML-Verfahren, Schwerpunkt auf Zeitreihen und Anomalieerkennung.'
);

INSERT INTO thema_fachgebiet (bezeichnung, thema_id)
VALUES
    ('Machine Learning', '3f7d8e1f-2b4a-4f76-9a8d-6b5f1c2d3e40'),
    ('Data Science', '3f7d8e1f-2b4a-4f76-9a8d-6b5f1c2d3e40');

INSERT INTO thema_voraussetzung (kursbezeichnung, fachgebiet_bezeichnung, thema_id)
VALUES
    ('Einführung in die KI', 'Machine Learning', '3f7d8e1f-2b4a-4f76-9a8d-6b5f1c2d3e40'),
    ('Datenanalyse', 'Data Science', '3f7d8e1f-2b4a-4f76-9a8d-6b5f1c2d3e40');

INSERT INTO thema_link (url, anzeigetext, thema_id)
VALUES
    ('https://example.com/paper', 'Beispielpublikation', '3f7d8e1f-2b4a-4f76-9a8d-6b5f1c2d3e40');

INSERT INTO thema_informationsdatei (titel, beschreibung, uploader_name, upload_datum, dateisystem_pfad, thema_id)
VALUES
    (
        'Projektbrief',
        'Kurzinfo zum Themenumfang und erwarteten Ergebnissen.',
        'Prof. Beispiel',
        '2026-02-01 10:00:00',
        '/data/themen/projektbrief.pdf',
        '3f7d8e1f-2b4a-4f76-9a8d-6b5f1c2d3e40'
    );
