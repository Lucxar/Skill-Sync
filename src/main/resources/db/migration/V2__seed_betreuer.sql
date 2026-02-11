insert into betreuer (id, name, email, matrikelnummer) values
  ('11111111-1111-1111-1111-111111111111', 'Dr. Anna Keller', 'anna.keller@uni.de', 'M-1001'),
  ('22222222-2222-2222-2222-222222222222', 'Prof. Max Braun', 'max.braun@uni.de', 'M-1002'),
  ('33333333-3333-3333-3333-333333333333', 'Dr. Lara Wolf', 'lara.wolf@uni.de', 'M-1003'),
  ('44444444-4444-4444-4444-444444444444', 'Prof. Tim Brandt', 'tim.brandt@uni.de', 'M-1004'),
  ('55555555-5555-5555-5555-555555555555', 'Dr. Eva Scholz', 'eva.scholz@uni.de', 'M-1005');

insert into betreuer_fachgebiet (betreuer_id, bezeichnung) values
  ('11111111-1111-1111-1111-111111111111', 'Machine Learning'),
  ('11111111-1111-1111-1111-111111111111', 'NLP'),
  ('22222222-2222-2222-2222-222222222222', 'Datenbanken'),
  ('33333333-3333-3333-3333-333333333333', 'Software Engineering'),
  ('44444444-4444-4444-4444-444444444444', 'Verteilte Systeme'),
  ('55555555-5555-5555-5555-555555555555', 'IT-Security');

insert into betreuer_link (betreuer_id, url, anzeigetext) values
  ('11111111-1111-1111-1111-111111111111', 'https://uni.de/keller', 'Profil'),
  ('22222222-2222-2222-2222-222222222222', 'https://uni.de/braun', 'Profil'),
  ('33333333-3333-3333-3333-333333333333', 'https://uni.de/wolf', 'Profil');

insert into betreuer_informationsdatei (betreuer_id, titel, beschreibung, uploader_name, upload_datum, dateisystem_pfad) values
  ('11111111-1111-1111-1111-111111111111', 'Themenliste', 'Aktuelle Themen', 'System', now(), '/files/keller-themen.pdf'),
  ('33333333-3333-3333-3333-333333333333', 'Leitfaden', 'Ablauf Bachelorarbeit', 'System', now(), '/files/wolf-leitfaden.pdf');
