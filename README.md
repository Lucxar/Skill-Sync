# ThesisIsComing

Webanwendung, die Studierenden hilft, passende Betreuer und Themen fuer Abschlussarbeiten zu finden — ueber Browsing, Suche und Matching.

> Detaillierte Architekturdokumentation (arc42): [Dokumentation.md](Dokumentation.md)

## Was kann die Anwendung?

Nach dem Login ueber GitHub OAuth wird jeder Nutzer automatisch zu seinem Profil weitergeleitet. Studierende, die sich zum ersten Mal anmelden, werden direkt zur Profilerstellung gefuehrt.

### Fuer Studierende

Studierende legen ein Profil mit ihren abgeschlossenen Kursen und Interessensgebieten an. Ueber die Betreuer- und Themenlisten koennen sie gezielt nach Namen, Titeln oder Fachgebieten suchen und filtern. Das Matching berechnet automatisch, welche Themen und Betreuer am besten zu den eigenen Kursen und Interessen passen — sortiert nach einem Score, der die Anzahl der Uebereinstimmungen widerspiegelt. Themen mit Pflicht-Voraussetzungen werden nur angezeigt, wenn alle geforderten Kurse abgeschlossen sind.

### Fuer Betreuende

Betreuende pflegen ihr Profil mit Fachgebieten, Links und Dateien. Unterstuetzte Dateitypen sind PDF, ZIP und Markdown — Markdown-Dateien erhalten eine automatische HTML-Preview mit Sanitizing gegen Script-Injection. Betreuer koennen Themen fuer Abschlussarbeiten erstellen und verwalten, inklusive Beschreibungen in Markdown und fachlichen Voraussetzungen. Jeder Betreuer kann nur sein eigenes Profil bearbeiten.

### Fuer Admins

Admins verwalten Rollen ueber eine eigene Oberflaeche. Das Rollensystem hat zwei Ebenen: Betreuer-Rollen sind an ein Profil gebunden (ein GitHub-Login wird einem Betreuer-Eintrag zugeordnet), waehrend Zusatzrollen wie Organisator und Korrektor unabhaengig vergeben werden. Admin-Logins werden ueber die Anwendungskonfiguration festgelegt.

### Datenkonsistenz

Gleichzeitige Bearbeitungen desselben Datensatzes durch mehrere Nutzer werden ueber Optimistic Locking erkannt — statt Daten stillschweigend zu ueberschreiben, zeigt die Anwendung eine Konfliktseite an und fordert zum erneuten Laden auf. Themen koennen nur geloescht werden, wenn keine Betreuer mehr zugeordnet sind.

## Features

- **Suche und Filter** — Case-insensitive Suche nach Name/Titel und Fachgebiet auf allen Listenseiten
- **Matching** — Score-basierter Abgleich von Interessen und Kursen mit Themen-Voraussetzungen
- **Datei-Management** — Upload (PDF/ZIP/Markdown, max. 10 MB), Download, Ersetzen, Markdown-Preview
- **Rollensystem** — Studierende, Betreuende, Organisator, Korrektor, Admin (ueber GitHub OAuth)
- **Optimistic Locking** — Konflikterkennung bei gleichzeitiger Bearbeitung
- **Dark/Light Mode** — Swiss-Style Design mit localStorage-Persistenz

## Tech-Stack

| Komponente | Technologie |
|-----------|-------------|
| Backend | Java 21, Spring Boot 4.0 |
| Security | Spring Security, GitHub OAuth2 |
| Persistenz | Spring Data JDBC, Flyway, PostgreSQL 17 |
| Frontend | Thymeleaf, Bootstrap 5.3, Bootstrap Icons |
| Tests | JUnit 5, Testcontainers, ArchUnit, JaCoCo (≥ 80%) |
| Code-Qualitaet | SpotBugs, Spotless (Google Java Format) |
| Deployment | Docker Compose |

## Voraussetzungen

- Java 21
- Docker (fuer PostgreSQL) oder eine laufende PostgreSQL-Instanz
- GitHub OAuth App ([Anleitung](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/creating-an-oauth-app))

## Setup

### 1. Repository klonen

```bash
git clone <repository-url>
cd ThesisIsComing
```

### 2. Umgebungsvariablen konfigurieren

`.env`-Datei im Projektroot erstellen:

```properties
GITHUB_OAUTH_CLIENT_ID=deine-client-id
GITHUB_OAUTH_CLIENT_SECRET=dein-client-secret
```

### 3a. Mit Docker Compose starten (empfohlen)

```bash
docker compose up --build
```

Startet PostgreSQL und die Anwendung. Erreichbar unter `http://localhost:8080`.

### 3b. Ohne Docker starten

PostgreSQL muss laufen (Default: `localhost:5432`, DB `mydatabase`, User `myuser`, Passwort `secret`).

```bash
./gradlew bootRun
```

## Tests ausfuehren

```bash
./gradlew check
```

Beinhaltet Unit-Tests, Integrationstests (Testcontainers + PostgreSQL), ArchUnit-Pruefungen, JaCoCo-Coverage und SpotBugs-Analyse.

## Projektstruktur

```
src/main/java/.../
  presentationDriving/    Controller, Security, Formulare
  ApplicationService/     Use-Cases, Ports, Service-Interfaces
  domainModel/            Aggregate, Value Objects, Matching-Logik
  infrastructureDriven/   Repository-Adapter, DTOs, Dateispeicher

src/main/resources/
  templates/              Thymeleaf-Templates (inkl. Fragments)
  db/migration/           Flyway-Migrationen (V1–V13)
  static/css/             Swiss-Theme CSS, Accessibility CSS

src/test/                 Unit-, Web-, Repository- und Architekturtests
```
