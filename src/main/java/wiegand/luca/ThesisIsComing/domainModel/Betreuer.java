package wiegand.luca.ThesisIsComing.domainModel;

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
        this.name = name;
        this.githubLogin = null;
        this.kontaktinformationen = kontaktinformationen;
        this.fachgebiete = new HashSet<>();
        this.dateien = new HashSet<>();
        this.links = new HashSet<>();
        this.themen = new HashSet<>();
    }

    public Betreuer(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.githubLogin = null;
        this.kontaktinformationen = null;
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
        this.name = name;
        this.githubLogin = githubLogin;
        this.kontaktinformationen = kontaktinformationen;
        this.fachgebiete = fachgebiete != null ? fachgebiete : new HashSet<>();
        this.dateien = dateien != null ? dateien : new HashSet<>();
        this.links = links != null ? links : new HashSet<>();
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

    public String getGithubLogin() {
        return githubLogin;
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

    public Set<Thema> getThemen() {
        return themen;
    }

    public void assignThema(UUID themaId) {
        Thema thema = new Thema(themaId, "");
        this.themen.add(thema);
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
