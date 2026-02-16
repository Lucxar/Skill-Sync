package wiegand.luca.ThesisIsComing.ApplicationService;

import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;

public record SucheForm(String suchbegriff, String fachgebiet) {

  public boolean isEmpty() {
    return isBlank(suchbegriff) && isBlank(fachgebiet);
  }

  public String normalizedSuchbegriff() {
    return normalize(suchbegriff);
  }

  public Fachgebiet toFachgebiet() {
    String value = normalize(fachgebiet);
    return value == null ? null : new Fachgebiet(value);
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private static String normalize(String value) {
    return isBlank(value) ? null : value.trim();
  }
}
