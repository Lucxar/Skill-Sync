# arc42 - ThesisIsComing

## 1. Einleitung und Ziele
Ziel ist eine Webanwendung, die Studierenden hilft, passende Betreuerinnen und Betreuer sowie Themen fuer Abschlussarbeiten zu finden. Das erfolgt ueber Browsing, Suche/Filter und Matching.

Qualitaetsziele:
- Sicherheit: Rollenbasierte Autorisierung, kein Zugriff ohne Berechtigung, Eingaben validiert.
- Wartbarkeit: klare Onion-Architektur mit ArchUnit-Checks.
- Austauschbarkeit: DB-Profile (PostgreSQL/H2) und Flyway-Migrationen.
- Nutzbarkeit/Accessibility: semantisches HTML, Tastaturbedienung, Screenreader-Unterstuetzung (Status siehe Offene Punkte).

Stakeholder:
- Studierende: lesen, suchen, matchen, eigenes Profil pflegen.
- Betreuende: eigenes Profil inkl. Dateien/Links/Themen pflegen.
- Admin: Rollen vergeben/entziehen, globale Verwaltung.
- Organisator/Korrektor: zusaetzliche Rollen, verwaltbar ueber Admin-UI.

## 2. Randbedingungen
- Java/Spring Boot, Spring Security, Spring Data JDBC, Thymeleaf.
- GitHub OAuth fuer Login.
- Datenbank: PostgreSQL (Standard)
- Dateien werden im Dateisystem gespeichert (Uploads), Limit 10 MB.
- Docker Compose fuer lokales Deployment (compose.yaml).

## 3. Kontextabgrenzung

### 3.1 Fachlicher Kontext
Die Anwendung stellt Profile und Themen bereit, bietet Suche/Filter und Matching. Inhalte sind nur nach Login sichtbar. Admins verwalten Rollen; Betreuende und Studierende pflegen eigene Profile.

Kontextdiagramm (fachlich/technisch):

```
 [Studierende/Betreuende/Admin/Organisator/Korrektor]
                    |
                    | HTTPS (Browser)
                    v
            [ThesisIsComing Web App]
               |        |        |
               |        |        +--> [Dateisystem: uploads]
               |        +-----------> [GitHub OAuth]
               +--------------------> [PostgreSQL/H2]
```

### 3.2 Technischer Kontext
- Web: Spring MVC Controller und Thymeleaf Templates.
- Security: OAuth2 Login und @PreAuthorize auf Controller-Endpunkten.
- Persistenz: Spring Data JDBC, Flyway Migrationen unter `src/main/resources/db/migration`.
- Uploads: DateiSpeicherAdapter nutzt `thesis.upload-dir` (Default `uploads`).

Wichtige Endpunkte (Dokumentationsregeln / Ist-Stand):
- Listen/Details: `GET /betreuer`, `GET /betreuer/{id}`, `GET /themen`, `GET /themen/{id}`
- Dateien: `GET /dateien/{betreuerId}/{dateiname}/download`, `GET /dateien/{betreuerId}/{dateiname}/markdown`
- Matching: `GET /matching`, `POST /matching`, `GET /matching/ergebnisse`
- Rollenverwaltung: `GET/POST /admin/rollen`, `POST /admin/rollen/zuweisen`, `POST /admin/rollen/entziehen`
- Profil-Shortcut: `GET /mein-profil`

## 4. Loesungsstrategie
- Onion-Architektur mit klaren Paketen: `domainModel`, `domainService`, `ApplicationService`, `presentationDriving`, `infrastructureDriven`.
- Rollenbasierte Zugriffskontrolle ueber Spring Security und `@PreAuthorize`.
- Domain-validierungen in Aggregaten, Use-Case-Validierung in Services.
- PRG fuer Form-Workflows, kurze Controller-Methoden.
- Markdown-Rendering mit Sanitizing (Jsoup Safelist) gegen Script-Injektion.
- Optimistic Locking ueber `version`-Spalten und Service-Checks.

## 5. Bausteinsicht

### 5.1 Level-1 Bausteine

```
 [presentationDriving]
   - Controller (Betreuer/Thema/Student/Matching/Datei/AdminRollen)
   - Security (SecurityConfiguration, OAuth Loader)
   - Form-Objekte und Views
          |
          v
 [ApplicationService]
   - Use-Cases (BetreuerService, ThemaService, StudentService, MatchingService)
   - Ports (Repository-Interfaces, DateiSpeicher)
   - Security-Interfaces (CurrentUser, SecurityRoles)
          |
          v
 [domainModel / domainService]
   - Aggregate (Betreuer, Thema, Student)
   - Value Objects (Fachgebiet, Link, Informationsdatei, Voraussetzungen)
   - Matching-Regeln und Sortierung

 [infrastructureDriven]
   - Repository-Adapter + DTOs (Spring Data JDBC)
   - DateiSpeicherAdapter (Dateisystem)

 [resources]
   - Flyway Migrationen
   - Templates
```

Kurzbeschreibung der Bausteine:
- presentationDriving: HTTP-Endpunkte, Security-Integration, Formularbindung, Views.
- ApplicationService: Use-Case-Koordination, Autorisierungschecks, Validierungen, Transaktionen.
- domainModel/domainService: Fachliche Logik und Invarianten (inkl. Matching-Regeln).
- infrastructureDriven: Datenbankadapter und Dateispeicher, Mapping zwischen Domain und DTO.

## 6. Laufzeitsicht (Kurz)

Login und Rollenaufloesung:
1. Nutzer ruft Seite auf -> Spring Security erzwingt OAuth2 Login.
2. `GithubOAuth2UserLoader` liest GitHub-Login.
3. Rollen werden zusammengesetzt aus:
   - Admin-Logins (`app.security.admin-logins`),
   - Betreuer-Zuordnung ueber `BetreuerService.findByGithubLogin`,
   - Zusatzrollen aus `user_role_assignment` (Organisator/Korrektor).

Datei-Upload (Betreuerprofil):
1. `POST /betreuer/{id}/dateien` validiert Formular.
2. `BetreuerService.uploadDatei` prueft Typ/Limit (10 MB) und erzeugt Metadaten.
3. `DateiSpeicherAdapter` speichert in `uploads` und liefert konfliktfreien Namen.
4. Datei erscheint in Profil, Markdown wird ueber `/dateien/.../markdown` als HTML gerendert.

Matching:
1. Nutzer sendet Voraussetzungen (Kurse/Interessen) ueber `/matching`.
2. `MatchingService` laedt alle Themen/Betreuer fuer Matching.
3. Domain `MatchingRegeln` bewertet, `MatchingSortierer` sortiert nach Score.

## 7. Deployment-Sicht
- Lokal: `./gradlew bootRun` (DB muss laufen), oder H2 via Profil `h2`.
- Build: `./gradlew bootJar`.
- Docker: `docker compose up --build` mit `compose.yaml`.
- Ports: App `8080`, Postgres `5432`.
- Uploads: Volume-Mount `./uploads:/app/uploads`.

## 8. Querschnittliche Konzepte
- Security: `@PreAuthorize` auf Schreiboperationen, globale Auth-Pflicht in `SecurityConfiguration`.
- Validierung: Bean Validation in Forms und Domain-Validierungen in Aggregaten.
- Sanitizing: Jsoup Safelist fuer Markdown (Themen und Datei-Preview).
- Nebenlaeufigkeit: Optimistic Locking via `version`-Spalten und Service-Checks.
- Tests: Unit, Web, Repository-Integration, Security-Role-Matrix; ArchUnit fuer Onion-Regeln.
