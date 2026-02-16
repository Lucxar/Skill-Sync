package wiegand.luca.ThesisIsComing.domainModel;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.StudierendenVoraussetzungen;

public final class MatchingRegeln {

  private MatchingRegeln() {}

  public static MatchScore bewerteThema(Thema thema, StudierendenVoraussetzungen voraussetzungen) {
    if (thema == null || voraussetzungen == null) {
      return MatchScore.notCompatible();
    }
    Set<String> studentKurse =
        kursBezeichnungenAusBestandenenKursen(voraussetzungen.getBestandeneKurse());
    Set<String> requiredKurse = kursBezeichnungenAusKursen(thema.getVoraussetzungen());
    if (!requiredKurse.isEmpty() && !studentKurse.containsAll(requiredKurse)) {
      return MatchScore.notCompatible();
    }
    Set<String> studentFachgebiete = fachgebieteAusVoraussetzungen(voraussetzungen);
    Set<String> themaFachgebiete = fachgebiete(thema.getFachgebiete());
    Set<String> passendeFachgebiete = intersection(studentFachgebiete, themaFachgebiete);
    Set<String> passendeKurse = intersection(studentKurse, requiredKurse);
    if (requiredKurse.isEmpty() && passendeFachgebiete.isEmpty()) {
      return MatchScore.notCompatible();
    }
    int score = passendeFachgebiete.size() + passendeKurse.size();
    return MatchScore.compatible(score, passendeFachgebiete, passendeKurse);
  }

  public static MatchScore bewerteBetreuer(
      Betreuer betreuer, StudierendenVoraussetzungen voraussetzungen) {
    if (betreuer == null || voraussetzungen == null) {
      return MatchScore.notCompatible();
    }
    Set<String> studentFachgebiete = fachgebieteAusVoraussetzungen(voraussetzungen);
    Set<String> betreuerFachgebiete = fachgebiete(betreuer.getFachgebiete());
    Set<String> passendeFachgebiete = intersection(studentFachgebiete, betreuerFachgebiete);
    if (passendeFachgebiete.isEmpty()) {
      return MatchScore.notCompatible();
    }
    return MatchScore.compatible(passendeFachgebiete.size(), passendeFachgebiete, Set.of());
  }

  private static Set<String> kursBezeichnungenAusBestandenenKursen(Set<BestandenerKurs> kurse) {
    Set<String> result = new HashSet<>();
    if (kurse == null) {
      return result;
    }
    for (BestandenerKurs kurs : kurse) {
      if (kurs == null) {
        continue;
      }
      addNormalized(result, kurs.getKursbezeichnung());
    }
    return result;
  }

  private static Set<String> kursBezeichnungenAusKursen(Set<Kurs> kurse) {
    Set<String> result = new HashSet<>();
    if (kurse == null) {
      return result;
    }
    for (Kurs kurs : kurse) {
      if (kurs == null) {
        continue;
      }
      addNormalized(result, kurs.getKursbezeichnung());
    }
    return result;
  }

  private static Set<String> fachgebieteAusVoraussetzungen(
      StudierendenVoraussetzungen voraussetzungen) {
    Set<String> result = new HashSet<>();
    if (voraussetzungen == null) {
      return result;
    }
    for (Fachgebiet fachgebiet : voraussetzungen.getInteressensgebiete()) {
      if (fachgebiet == null) {
        continue;
      }
      addNormalized(result, fachgebiet.getBezeichnung());
    }
    for (BestandenerKurs kurs : voraussetzungen.getBestandeneKurse()) {
      if (kurs == null || kurs.getFachgebietDesKurses() == null) {
        continue;
      }
      addNormalized(result, kurs.getFachgebietDesKurses().getBezeichnung());
    }
    return result;
  }

  private static Set<String> fachgebiete(Set<Fachgebiet> fachgebiete) {
    Set<String> result = new HashSet<>();
    if (fachgebiete == null) {
      return result;
    }
    for (Fachgebiet fachgebiet : fachgebiete) {
      if (fachgebiet == null) {
        continue;
      }
      addNormalized(result, fachgebiet.getBezeichnung());
    }
    return result;
  }

  private static Set<String> intersection(Set<String> left, Set<String> right) {
    Set<String> result = new HashSet<>();
    if (left == null || right == null) {
      return result;
    }
    for (String value : left) {
      if (right.contains(value)) {
        result.add(value);
      }
    }
    return result;
  }

  private static void addNormalized(Set<String> target, String value) {
    String normalized = normalize(value);
    if (normalized != null) {
      target.add(normalized);
    }
  }

  private static String normalize(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed.toLowerCase(Locale.ROOT);
  }
}
