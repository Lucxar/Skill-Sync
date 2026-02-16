package wiegand.luca.ThesisIsComing.domainModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wiegand.luca.ThesisIsComing.testdata.DomainTestData.link;
import static wiegand.luca.ThesisIsComing.testdata.DomainTestData.thema;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wiegand.luca.ThesisIsComing.domainModel.exception.LinkNotFoundException;

class ThemaTest {

  @Test
  @DisplayName("Thema trims title and clears empty description")
  void trims_title_and_clears_empty_description() {
    Thema thema = thema(UUID.randomUUID(), "  Thema A  ");

    thema.updateDetails("  Thema A  ", "   ");

    assertThat(thema.getTitel()).isEqualTo("Thema A");
    assertThat(thema.getBeschreibungMarkdown()).isNull();
  }

  @Test
  @DisplayName("Thema rejects blank titles")
  void rejects_blank_titles() {
    assertThatThrownBy(() -> new Thema(UUID.randomUUID(), " "))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Thema removeLink fails for missing links")
  void remove_link_fails_for_missing_links() {
    Thema thema = thema(UUID.randomUUID(), "Thema A");
    var link = link();

    assertThatThrownBy(() -> thema.removeLink(link)).isInstanceOf(LinkNotFoundException.class);
  }
}
