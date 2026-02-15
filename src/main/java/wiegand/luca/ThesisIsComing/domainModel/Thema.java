package wiegand.luca.ThesisIsComing.domainModel;

import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.exception.LinkNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    private Set<Betreuer> betreuer;



    public Thema(UUID id, String titel) {
        this.id = id;
        this.titel = titel;
        this.beschreibungMarkdown = null;
        this.voraussetzungen = new HashSet<>();
        this.fachgebiete = new HashSet<>();
        this.dateien = new HashSet<>();
        this.links = new HashSet<>();
        this.betreuer = new HashSet<>();
    }

    public Thema(UUID id, String titel, String beschreibung, Set<Kurs> voraussetzungen,
                 Set<Fachgebiet> fachgebiete, Set<Informationsdatei> dateien, Set<Link> links, Set<Betreuer> betreuer) {
        this.id = id;
        this.titel = titel;
        this.beschreibungMarkdown = beschreibung;
        this.voraussetzungen = voraussetzungen != null ? voraussetzungen : new HashSet<>();
        this.fachgebiete = fachgebiete != null ? fachgebiete : new HashSet<>();
        this.dateien = dateien != null ? dateien : new HashSet<>();
        this.links = links != null ? links : new HashSet<>();
        this.betreuer = betreuer != null ? betreuer : new HashSet<>();
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

    public void addLink(Link link) {
        if (link == null) {
            throw new InvalidLinkException("Link darf nicht null sein");
        }
        links.add(link);
    }

    public void updateLink(Link existing, Link updated) {
        removeLink(existing);
        addLink(updated);
    }

    public void removeLink(Link link) {
        if (link == null) {
            throw new InvalidLinkException("Link darf nicht null sein");
        }
        boolean removed = links.remove(link);
        if (!removed) {
            throw new LinkNotFoundException("Link wurde nicht gefunden");
        }
    }

    public Set<Betreuer> getBetreuer() {
        return betreuer;
    }

    public void assignBetreuer(UUID betreuerId) {
        Betreuer betreuer = new Betreuer(betreuerId, "");
        this.betreuer.add(betreuer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Thema)) {
            return false;
        }
        Thema other = (Thema) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }




}
