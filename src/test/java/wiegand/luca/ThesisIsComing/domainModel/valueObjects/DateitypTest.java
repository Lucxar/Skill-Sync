package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateitypTest {

  @Test
  @DisplayName("Dateityp detects known extensions")
  void detects_known_extensions() {
    assertThat(Dateityp.fromDateiname("info.zip")).isEqualTo(Dateityp.ZIP);
    assertThat(Dateityp.fromDateiname("info.PDF")).isEqualTo(Dateityp.PDF);
    assertThat(Dateityp.fromDateiname("info.md")).isEqualTo(Dateityp.MARKDOWN);
    assertThat(Dateityp.fromDateiname("info.markdown")).isEqualTo(Dateityp.MARKDOWN);
  }

  @Test
  @DisplayName("Dateityp rejects unknown extensions")
  void rejects_unknown_extensions() {
    assertThatThrownBy(() -> Dateityp.fromDateiname("info.txt"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
