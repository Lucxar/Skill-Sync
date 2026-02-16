package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StudierendenVoraussetzungen {

  private final Set<BestandenerKurs> bestandeneKurse;
  private final Set<Fachgebiet> interessensgebiete;

  public StudierendenVoraussetzungen(
      Set<BestandenerKurs> bestandeneKurse, Set<Fachgebiet> interessensgebiete) {
    this.bestandeneKurse = sanitizeKurse(bestandeneKurse);
    this.interessensgebiete = sanitizeFachgebiete(interessensgebiete);
  }

  public Set<BestandenerKurs> getBestandeneKurse() {
    return Collections.unmodifiableSet(bestandeneKurse);
  }

  public Set<Fachgebiet> getInteressensgebiete() {
    return Collections.unmodifiableSet(interessensgebiete);
  }

  private static Set<BestandenerKurs> sanitizeKurse(Set<BestandenerKurs> kurse) {
    Set<BestandenerKurs> result = new HashSet<>();
    if (kurse == null) {
      return result;
    }
    for (BestandenerKurs kurs : kurse) {
      if (kurs == null) {
        continue;
      }
      String name = normalize(kurs.getKursbezeichnung());
      if (name == null) {
        continue;
      }
      Fachgebiet fachgebiet = null;
      if (kurs.getFachgebietDesKurses() != null) {
        String fachgebietName = normalize(kurs.getFachgebietDesKurses().getBezeichnung());
        if (fachgebietName != null) {
          fachgebiet = new Fachgebiet(fachgebietName);
        }
      }
      result.add(new BestandenerKurs(name, kurs.getBestandenAm(), fachgebiet));
    }
    return result;
  }

  private static Set<Fachgebiet> sanitizeFachgebiete(Set<Fachgebiet> fachgebiete) {
    Set<Fachgebiet> result = new HashSet<>();
    if (fachgebiete == null) {
      return result;
    }
    for (Fachgebiet fachgebiet : fachgebiete) {
      if (fachgebiet == null) {
        continue;
      }
      String name = normalize(fachgebiet.getBezeichnung());
      if (name != null) {
        result.add(new Fachgebiet(name));
      }
    }
    return result;
  }

  private static String normalize(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
