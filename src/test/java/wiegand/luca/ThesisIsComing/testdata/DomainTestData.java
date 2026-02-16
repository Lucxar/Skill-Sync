package wiegand.luca.ThesisIsComing.testdata;

import java.time.LocalDateTime;
import java.util.UUID;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

public final class DomainTestData {

  private DomainTestData() {}

  public static Betreuer betreuer() {
    return new Betreuer(UUID.randomUUID(), "Anna");
  }

  public static Betreuer betreuer(UUID id, String name) {
    return new Betreuer(id, name);
  }

  public static Betreuer betreuerWithKontakt(String name, String email, String matrikelnummer) {
    return new Betreuer(name, new Kontaktinformationen(email, matrikelnummer));
  }

  public static Thema thema() {
    return new Thema(UUID.randomUUID(), "Thema A");
  }

  public static Thema thema(UUID id, String titel) {
    return new Thema(id, titel);
  }

  public static Student student() {
    return new Student(UUID.randomUUID(), "Tom");
  }

  public static Student student(UUID id, String name) {
    return new Student(id, name);
  }

  public static Student studentWithKontakt(String name, String email, String matrikelnummer) {
    return new Student(name, new Kontaktinformationen(email, matrikelnummer));
  }

  public static Link link() {
    return new Link("https://example.com", "Beispiel");
  }

  public static Kurs kurs(String bezeichnung, String fachgebiet) {
    return new Kurs(bezeichnung, new Fachgebiet(fachgebiet));
  }

  public static Fachgebiet fachgebiet(String bezeichnung) {
    return new Fachgebiet(bezeichnung);
  }

  public static Informationsdatei informationsdatei(Dateityp dateityp) {
    String dateiname = dateityp == Dateityp.MARKDOWN ? "info.md" : "info.pdf";
    return new Informationsdatei(
        "Info",
        "Beschreibung",
        "anna",
        LocalDateTime.now(),
        "stored." + dateityp.name().toLowerCase(),
        dateityp,
        dateiname,
        5L);
  }
}
