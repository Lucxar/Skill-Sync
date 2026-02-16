package wiegand.luca.ThesisIsComing.domainModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;

public class Student {

  private UUID id;
  private String name;
  private String githubLogin;
  private Long version;
  private Kontaktinformationen kontaktinformationen;
  private Set<BestandenerKurs> bestandeneKurse;
  private Set<Fachgebiet> interessensgebiete;

  public Student(String name, Kontaktinformationen kontaktinformationen) {
    this(UUID.randomUUID(), name, null, kontaktinformationen, null, null);
  }

  public Student(UUID id, String name) {
    this(id, name, null, null, null, null);
  }

  public Student(
      UUID id,
      String name,
      String githubLogin,
      Kontaktinformationen kontaktinformationen,
      Set<BestandenerKurs> bestandeneKurse,
      Set<Fachgebiet> interessensgebiete,
      Long version) {
    this.id = id;
    setName(name);
    this.githubLogin = normalizeGithubLogin(githubLogin);
    this.version = version != null ? version : 0L;
    setKontaktinformationen(kontaktinformationen);
    this.bestandeneKurse =
        bestandeneKurse != null ? new HashSet<>(bestandeneKurse) : new HashSet<>();
    this.interessensgebiete =
        interessensgebiete != null ? new HashSet<>(interessensgebiete) : new HashSet<>();
  }

  public Student(
      UUID id,
      String name,
      Kontaktinformationen kontaktinformationen,
      Set<BestandenerKurs> bestandeneKurse,
      Set<Fachgebiet> interessensgebiete) {
    this(id, name, null, kontaktinformationen, bestandeneKurse, interessensgebiete, null);
  }

  public Student(
      UUID id,
      String name,
      String githubLogin,
      Kontaktinformationen kontaktinformationen,
      Set<BestandenerKurs> bestandeneKurse,
      Set<Fachgebiet> interessensgebiete) {
    this(id, name, githubLogin, kontaktinformationen, bestandeneKurse, interessensgebiete, null);
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

  public Long getVersion() {
    return version;
  }

  public void setName(String name) {
    this.name = normalizeName(name);
  }

  public Kontaktinformationen getKontaktinformationen() {
    return kontaktinformationen;
  }

  public void setKontaktinformationen(Kontaktinformationen kontaktinformationen) {
    this.kontaktinformationen = normalizeKontaktinformationen(kontaktinformationen);
  }

  public Set<BestandenerKurs> getBestandeneKurse() {
    return bestandeneKurse;
  }

  public Set<Fachgebiet> getInteressensgebiete() {
    return interessensgebiete;
  }

  public void updateDetails(String name, String email, String matrikelnummer) {
    setName(name);
    setKontaktinformationen(new Kontaktinformationen(email, matrikelnummer));
  }

  public void replaceVoraussetzungen(
      Set<BestandenerKurs> bestandeneKurse, Set<Fachgebiet> interessensgebiete) {
    this.bestandeneKurse =
        bestandeneKurse != null ? new HashSet<>(bestandeneKurse) : new HashSet<>();
    this.interessensgebiete =
        interessensgebiete != null ? new HashSet<>(interessensgebiete) : new HashSet<>();
  }

  private static String normalizeName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name darf nicht leer sein");
    }
    return name.trim();
  }

  private static String normalizeGithubLogin(String login) {
    if (login == null) {
      return null;
    }
    String trimmed = login.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private static Kontaktinformationen normalizeKontaktinformationen(
      Kontaktinformationen kontaktinformationen) {
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
    if (!(o instanceof Student)) {
      return false;
    }
    Student other = (Student) o;
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
