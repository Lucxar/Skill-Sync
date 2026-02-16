package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.HashSet;
import java.util.Set;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.StudierendenVoraussetzungen;

public record MatchingForm(String bestandeneKurse, String interessensgebiete) {

  public boolean isEmpty() {
    return isBlank(bestandeneKurse) && isBlank(interessensgebiete);
  }

  public StudierendenVoraussetzungen toVoraussetzungen() {
    return new StudierendenVoraussetzungen(parseBestandeneKurse(), parseInteressensgebiete());
  }

  private Set<BestandenerKurs> parseBestandeneKurse() {
    Set<BestandenerKurs> result = new HashSet<>();
    for (String entry : splitEntries(bestandeneKurse)) {
      result.add(new BestandenerKurs(entry, null, null));
    }
    return result;
  }

  private Set<Fachgebiet> parseInteressensgebiete() {
    Set<Fachgebiet> result = new HashSet<>();
    for (String entry : splitEntries(interessensgebiete)) {
      result.add(new Fachgebiet(entry));
    }
    return result;
  }

  private static Set<String> splitEntries(String input) {
    if (input == null || input.trim().isEmpty()) {
      return Set.of();
    }
    Set<String> result = new HashSet<>();
    for (String raw : input.split("[\n\r,]+")) {
      String trimmed = raw.trim();
      if (!trimmed.isEmpty()) {
        result.add(trimmed);
      }
    }
    return result;
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
