package wiegand.luca.ThesisIsComing.domainModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wiegand.luca.ThesisIsComing.testdata.DomainTestData.betreuer;
import static wiegand.luca.ThesisIsComing.testdata.DomainTestData.betreuerWithKontakt;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BetreuerTest {

  @Test
  @DisplayName("Betreuer trims name and clears empty contact info")
  void trims_name_and_clears_empty_contact_info() {
    Betreuer betreuer = betreuer(UUID.randomUUID(), "  Anna  ");

    betreuer.updateDetails("  Anna  ", " ", "");

    assertThat(betreuer.getName()).isEqualTo("Anna");
    assertThat(betreuer.getKontaktinformationen()).isNull();
  }

  @Test
  @DisplayName("Betreuer rejects blank names")
  void rejects_blank_names() {
    assertThatThrownBy(() -> new Betreuer(UUID.randomUUID(), " "))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Betreuer requires non-blank GitHub login")
  void requires_non_blank_github_login() {
    Betreuer betreuer = betreuer();

    assertThatThrownBy(() -> betreuer.assignGithubLogin("  "))
        .isInstanceOf(IllegalArgumentException.class);
    betreuer.assignGithubLogin(" anna-gh ");

    assertThat(betreuer.getGithubLogin()).isEqualTo("anna-gh");
  }

  @Test
  @DisplayName("Betreuer keeps non-empty contact info")
  void keeps_non_empty_contact_info() {
    Betreuer betreuer = betreuerWithKontakt("Anna", " anna@uni.de ", " M-1 ");

    assertThat(betreuer.getKontaktinformationen()).isNotNull();
    assertThat(betreuer.getKontaktinformationen().getEmail()).isEqualTo("anna@uni.de");
    assertThat(betreuer.getKontaktinformationen().getMatrikelnummer()).isEqualTo("M-1");
  }
}
