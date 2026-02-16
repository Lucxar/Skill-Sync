package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

public class Fachgebiet {

  private String bezeichnung;

  public Fachgebiet(String bezeichnung) {
    this.bezeichnung = normalize(bezeichnung);
  }

  // Getter und Setter
  public String getBezeichnung() {
    return bezeichnung;
  }

  public void setBezeichnung(String bezeichnung) {
    this.bezeichnung = normalize(bezeichnung);
  }

  private static String normalize(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Fachgebiet darf nicht null sein");
    }
    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Fachgebiet darf nicht leer sein");
    }
    return trimmed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Fachgebiet)) {
      return false;
    }
    Fachgebiet other = (Fachgebiet) o;
    return bezeichnung.equalsIgnoreCase(other.bezeichnung);
  }

  @Override
  public int hashCode() {
    return bezeichnung.toLowerCase().hashCode();
  }
}
