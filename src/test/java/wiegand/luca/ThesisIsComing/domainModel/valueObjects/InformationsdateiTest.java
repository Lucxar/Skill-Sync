package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InformationsdateiTest {

  @Test
  @DisplayName("Informationsdatei normalizes optional fields")
  void normalizes_optional_fields() {
    Informationsdatei datei =
        new Informationsdatei(
            " Titel ",
            " Beschreibung ",
            "anna",
            LocalDateTime.now(),
            "stored.pdf",
            Dateityp.PDF,
            "  ",
            -5L);

    assertThat(datei.getTitel()).isEqualTo("Titel");
    assertThat(datei.getOriginalDateiname()).isNull();
    assertThat(datei.getGroesseInBytes()).isEqualTo(0L);
  }

  @Test
  @DisplayName("Informationsdatei requires title and type")
  void requires_title_and_type() {
    assertThatThrownBy(
            () ->
                new Informationsdatei(
                    " ",
                    null,
                    "anna",
                    LocalDateTime.now(),
                    "stored.pdf",
                    Dateityp.PDF,
                    "info.pdf",
                    1L))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(
            () ->
                new Informationsdatei(
                    "Titel", null, "anna", LocalDateTime.now(), "stored.pdf", null, "info.pdf", 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
