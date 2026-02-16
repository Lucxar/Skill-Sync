package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;

public record BetreuerForm(
    @NotBlank(message = "Name darf nicht leer sein") String name,
    String email,
    String matrikelnummer,
    String fachgebiete,
    Long version) {

  public Set<Fachgebiet> toFachgebiete() {
    Set<Fachgebiet> result = new HashSet<>();
    for (String entry : splitEntries(fachgebiete)) {
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
}
