package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

import java.time.LocalDateTime;

public class Informationsdatei {
    private String titel;
    private String beschreibung;
    private String uploaderName;
    private LocalDateTime uploadDatum;
    private String dateisystemPfad;


    public Informationsdatei(String titel, String beschreibung, String uploaderName, LocalDateTime uploadDatum, String dateisystemPfad) {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.uploaderName = uploaderName;
        this.uploadDatum = uploadDatum;
        this.dateisystemPfad = dateisystemPfad;
    }

    // Getter und Setter Methoden
    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
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
}
