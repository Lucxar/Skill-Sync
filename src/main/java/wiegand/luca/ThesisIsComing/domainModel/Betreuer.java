package wiegand.luca.ThesisIsComing.domainModel;

import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Betreuer {

    private UUID id;
    private String name;
    private Kontaktinformationen kontaktinformationen;
    private Set<Fachgebiet> fachgebiete;
    private Set<Informationsdatei> dateien;
    private Set<Link> links;


    public Betreuer (String name, Kontaktinformationen kontaktinformationen) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.kontaktinformationen = kontaktinformationen;
        this.fachgebiete = new HashSet<>();
        this.dateien = new HashSet<>();
        this.links = new HashSet<>();
    }

    public Betreuer(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.kontaktinformationen = null;
        this.fachgebiete = new HashSet<>();
        this.dateien = new HashSet<>();
        this.links = new HashSet<>();
    }

    public Betreuer(UUID id, String name, Kontaktinformationen kontaktinformationen, Set<Fachgebiet> fachgebiete, Set<Informationsdatei> dateien, Set<Link> links) {
        this.id = id;
        this.name = name;
        this.kontaktinformationen = kontaktinformationen;
        this.fachgebiete = fachgebiete != null ? fachgebiete : new HashSet<>();
        this.dateien = dateien != null ? dateien : new HashSet<>();
        this.links = links != null ? links : new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Kontaktinformationen getKontaktinformationen() {
        return kontaktinformationen;
    }

    public Set<Fachgebiet> getFachgebiete() {
        return fachgebiete;
    }

    public Set<Informationsdatei> getDateien() {
        return dateien;
    }

    public Set<Link> getLinks() {
        return links;
    }



}
