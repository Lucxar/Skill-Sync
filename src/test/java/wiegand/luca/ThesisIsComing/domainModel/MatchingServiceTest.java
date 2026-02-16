package wiegand.luca.ThesisIsComing.domainModel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.StudierendenVoraussetzungen;

class MatchingServiceTest {

  @Test
  void matchThemen_filters_and_sorts_by_score() {
    StudierendenVoraussetzungen voraussetzungen =
        new StudierendenVoraussetzungen(
            Set.of(new BestandenerKurs("Compilerbau", null, new Fachgebiet("AI"))),
            Set.of(new Fachgebiet("AI")));
    Thema thema1 =
        new Thema(
            UUID.randomUUID(),
            "Thema A",
            null,
            Set.of(new Kurs("Compilerbau", new Fachgebiet("AI"))),
            Set.of(new Fachgebiet("AI")),
            Set.of(),
            Set.of(),
            Set.of());
    Thema thema2 =
        new Thema(
            UUID.randomUUID(),
            "Thema B",
            null,
            Set.of(new Kurs("Mathe", null)),
            Set.of(new Fachgebiet("ML")),
            Set.of(),
            Set.of(),
            Set.of());
    Thema thema3 =
        new Thema(
            UUID.randomUUID(),
            "Thema C",
            null,
            Set.of(),
            Set.of(new Fachgebiet("AI")),
            Set.of(),
            Set.of(),
            Set.of());

    MatchingService service = new MatchingService();

    List<ThemaMatch> result = service.matchThemen(voraussetzungen, List.of(thema1, thema2, thema3));

    assertThat(result).hasSize(2);
    assertThat(result.get(0).thema()).isEqualTo(thema1);
    assertThat(result.get(0).score()).isGreaterThan(result.get(1).score());
  }

  @Test
  void matchBetreuer_scores_by_fachgebiete() {
    StudierendenVoraussetzungen voraussetzungen =
        new StudierendenVoraussetzungen(Set.of(), Set.of(new Fachgebiet("AI")));
    Betreuer betreuer1 =
        new Betreuer(
            UUID.randomUUID(),
            "Anna",
            null,
            null,
            Set.of(new Fachgebiet("AI")),
            Set.of(),
            Set.of());
    Betreuer betreuer2 =
        new Betreuer(
            UUID.randomUUID(), "Bob", null, null, Set.of(new Fachgebiet("DB")), Set.of(), Set.of());

    MatchingService service = new MatchingService();

    List<BetreuerMatch> result =
        service.matchBetreuer(voraussetzungen, List.of(betreuer1, betreuer2));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).betreuer()).isEqualTo(betreuer1);
    assertThat(result.get(0).score()).isEqualTo(1);
  }
}
