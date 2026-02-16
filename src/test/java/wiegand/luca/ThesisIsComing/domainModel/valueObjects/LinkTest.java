package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;

class LinkTest {

  @Test
  @DisplayName("Link trims url and display text")
  void trims_url_and_display_text() {
    Link link = new Link(" https://example.com ", " Beispiel ");

    assertThat(link.getUrl()).isEqualTo("https://example.com");
    assertThat(link.getAnzeigetext()).isEqualTo("Beispiel");
  }

  @Test
  @DisplayName("Link rejects blank values")
  void rejects_blank_values() {
    assertThatThrownBy(() -> new Link(" ", "x")).isInstanceOf(InvalidLinkException.class);
    assertThatThrownBy(() -> new Link("https://example.com", " "))
        .isInstanceOf(InvalidLinkException.class);
  }
}
