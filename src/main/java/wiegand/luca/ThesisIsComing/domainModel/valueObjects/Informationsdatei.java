package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

import java.time.LocalDateTime;

public class Informationsdatei {
  private String titel;
  private String beschreibung;
  private String uploaderName;
  private LocalDateTime uploadDatum;
  private String dateisystemPfad;
  private Dateityp dateityp;
  private String originalDateiname;
  private long groesseInBytes;

  public Informationsdatei(
      String titel,
      String beschreibung,
      String uploaderName,
      LocalDateTime uploadDatum,
      String dateisystemPfad) {
    this(
        titel,
        beschreibung,
        uploaderName,
        uploadDatum,
        dateisystemPfad,
        Dateityp.fromDateiname(dateisystemPfad),
        null,
        0L);
  }

  public Informationsdatei(
      String titel,
      String beschreibung,
      String uploaderName,
      LocalDateTime uploadDatum,
      String dateisystemPfad,
      Dateityp dateityp,
      String originalDateiname,
      long groesseInBytes) {
    setTitel(titel);
    this.beschreibung = beschreibung;
    this.uploaderName = uploaderName;
    this.uploadDatum = uploadDatum;
    this.dateisystemPfad = dateisystemPfad;
    setDateityp(dateityp);
    this.originalDateiname = normalizeOptional(originalDateiname);
    this.groesseInBytes = Math.max(0L, groesseInBytes);
  }

  // Getter und Setter Methoden
  public String getTitel() {
    return titel;
  }

  public void setTitel(String titel) {
    this.titel = normalizeRequired(titel, "Titel darf nicht leer sein");
  }

  public String getBeschreibung() {
    return beschreibung;
  }

  public void setBeschreibung(String beschreibung) {
    this.beschreibung = beschreibung;
  }

  public String getUploaderName() {
    return uploaderName;
  }

  public void setUploaderName(String uploaderName) {
    this.uploaderName = uploaderName;
  }

  public LocalDateTime getUploadDatum() {
    return uploadDatum;
  }

  public void setUploadDatum(LocalDateTime uploadDatum) {
    this.uploadDatum = uploadDatum;
  }

  public String getDateisystemPfad() {
    return dateisystemPfad;
  }

  public void setDateisystemPfad(String dateisystemPfad) {
    this.dateisystemPfad = dateisystemPfad;
  }

  public Dateityp getDateityp() {
    return dateityp;
  }

  public void setDateityp(Dateityp dateityp) {
    if (dateityp == null) {
      throw new IllegalArgumentException("Dateityp darf nicht null sein");
    }
    this.dateityp = dateityp;
  }

  public String getOriginalDateiname() {
    return originalDateiname;
  }

  public void setOriginalDateiname(String originalDateiname) {
    this.originalDateiname = normalizeOptional(originalDateiname);
  }

  public long getGroesseInBytes() {
    return groesseInBytes;
  }

  public void setGroesseInBytes(long groesseInBytes) {
    this.groesseInBytes = Math.max(0L, groesseInBytes);
  }

  private static String normalizeRequired(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return value.trim();
  }

  private static String normalizeOptional(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
