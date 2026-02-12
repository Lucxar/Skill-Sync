package wiegand.luca.ThesisIsComing.domainModel;

import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Thema {
    private UUID id;
    private String titel;
    private String beschreibungMarkdown;
    private Set<Kurs> voraussetzungen;
    private Set<Fachgebiet> fachgebiete;
    private Set<Informationsdatei> dateien;
    private Set<Link> links;



    public Thema(UUID id, String titel) {
        this.id = id;
        this.titel = titel;
        this.beschreibungMarkdown = null;
        this.voraussetzungen = new HashSet<>();
        this.fachgebiete = new HashSet<>();
        this.dateien = new HashSet<>();
        this.links = new HashSet<>();
    }

    public Thema(UUID id, String titel, String beschreibung, Set<Kurs> voraussetzungen,
                 Set<Fachgebiet> fachgebiete, Set<Informationsdatei> dateien, Set<Link> links) {
        this.id = id;
        this.titel = titel;
        this.beschreibungMarkdown = beschreibung;
        this.voraussetzungen = voraussetzungen != null ? voraussetzungen : new HashSet<>();
        this.fachgebiete = fachgebiete != null ? fachgebiete : new HashSet<>();
        this.dateien = dateien != null ? dateien : new HashSet<>();
        this.links = links != null ? links : new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibungMarkdown() {
        return beschreibungMarkdown;
    }

    public void setBeschreibungMarkdown(String beschreibungMarkdown) {
        this.beschreibungMarkdown = beschreibungMarkdown;
    }

    public Set<Kurs> getVoraussetzungen() {
        return voraussetzungen;
    }

    public void setVoraussetzungen(Set<Kurs> voraussetzungen) {
        this.voraussetzungen = voraussetzungen != null ? voraussetzungen : new HashSet<>();
    }

    public Set<Fachgebiet> getFachgebiete() {
        return fachgebiete;
    }

    public void setFachgebiete(Set<Fachgebiet> fachgebiete) {
        this.fachgebiete = fachgebiete != null ? fachgebiete : new HashSet<>();
    }

    public Set<Informationsdatei> getDateien() {
        return dateien;
    }

    public void setDateien(Set<Informationsdatei> dateien) {
        this.dateien = dateien != null ? dateien : new HashSet<>();
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links != null ? links : new HashSet<>();
    }

    


}
