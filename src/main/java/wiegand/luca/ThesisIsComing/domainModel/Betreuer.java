package wiegand.luca.ThesisIsComing.domainModel;

import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.exception.LinkNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Betreuer {

    private UUID id;
    private String name;
    private String githubLogin;
    private Kontaktinformationen kontaktinformationen;
    private Set<Fachgebiet> fachgebiete;
    private Set<Informationsdatei> dateien;
    private Set<Link> links;
    private Set<Thema> themen;


    public Betreuer (String name, Kontaktinformationen kontaktinformationen) {
        this.id = UUID.randomUUID();
        setName(name);
        this.githubLogin = null;
        setKontaktinformationen(kontaktinformationen);
        this.fachgebiete = new HashSet<>();
        this.dateien = new HashSet<>();
        this.links = new HashSet<>();
        this.themen = new HashSet<>();
    }

    public Betreuer(UUID id, String name) {
        this.id = id;
        setName(name);
        this.githubLogin = null;
        setKontaktinformationen(null);
        this.fachgebiete = new HashSet<>();
        this.dateien = new HashSet<>();
        this.links = new HashSet<>();
        this.themen = new HashSet<>();
    }

    public Betreuer(UUID id,
                    String name,
                    String githubLogin,
                    Kontaktinformationen kontaktinformationen,
                    Set<Fachgebiet> fachgebiete,
                    Set<Informationsdatei> dateien,
                    Set<Link> links) {
        this.id = id;
        setName(name);
        this.githubLogin = githubLogin;
        setKontaktinformationen(kontaktinformationen);
        this.fachgebiete = fachgebiete != null ? new HashSet<>(fachgebiete) : new HashSet<>();
        this.dateien = dateien != null ? new HashSet<>(dateien) : new HashSet<>();
        this.links = links != null ? new HashSet<>(links) : new HashSet<>();
        this.themen = new HashSet<>();
    }

    public Betreuer(UUID id, String name, Kontaktinformationen kontaktinformationen, Set<Fachgebiet> fachgebiete, Set<Informationsdatei> dateien, Set<Link> links) {
        this(id, name, null, kontaktinformationen, fachgebiete, dateien, links);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalizeName(name);
    }

    public String getGithubLogin() {
        return githubLogin;
    }

    public Kontaktinformationen getKontaktinformationen() {
        return kontaktinformationen;
    }

    public void setKontaktinformationen(Kontaktinformationen kontaktinformationen) {
        this.kontaktinformationen = normalizeKontaktinformationen(kontaktinformationen);
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

    public Set<Thema> getThemen() {
        return themen;
    }

    public void updateDetails(String name, String email, String matrikelnummer) {
        setName(name);
        setKontaktinformationen(new Kontaktinformationen(email, matrikelnummer));
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

    public void assignThema(UUID themaId) {
        Thema thema = new Thema(themaId, "Unbekannt");
        this.themen.add(thema);
    }

    private static String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht leer sein");
        }
        return name.trim();
    }

    private static Kontaktinformationen normalizeKontaktinformationen(Kontaktinformationen kontaktinformationen) {
        if (kontaktinformationen == null) {
            return null;
        }
        String email = normalizeOptional(kontaktinformationen.getEmail());
        String matrikelnummer = normalizeOptional(kontaktinformationen.getMatrikelnummer());
        if (email == null && matrikelnummer == null) {
            return null;
        }
        return new Kontaktinformationen(email, matrikelnummer);
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Betreuer)) {
            return false;
        }
        Betreuer other = (Betreuer) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
