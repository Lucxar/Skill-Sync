package wiegand.luca.ThesisIsComing.ApplicationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ZuordnungNichtErlaubtException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

@ExtendWith(MockitoExtension.class)
class BetreuerThemenBeziehungServiceTest {

  @Mock private BetreuerService betreuerService;

  @Mock private ThemaService themaService;

  @Mock private CurrentUser currentUser;

  private BetreuerThemenBeziehungService service;

  @BeforeEach
  void setup() {
    service = new BetreuerThemenBeziehungService(betreuerService, themaService, currentUser);
  }

  @Test
  @DisplayName("assignThemaToBetreuer adds Thema and saves Betreuer")
  void assignThemaToBetreuer_adds_and_saves() {
    UUID betreuerId = UUID.randomUUID();
    UUID themaId = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(betreuerId, "Anna");

    when(currentUser.hasRole(SecurityRoles.ROLE_ADMIN)).thenReturn(true);
    when(betreuerService.getById(betreuerId)).thenReturn(betreuer);
    when(betreuerService.save(any(Betreuer.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Betreuer result = service.assignThemaToBetreuer(betreuerId, themaId);

    assertThat(result.getThemen()).anyMatch(thema -> themaId.equals(thema.getId()));
    verify(betreuerService).save(any(Betreuer.class));
  }

  @Test
  @DisplayName("listThemenByBetreuer returns assigned Themen")
  void listThemenByBetreuer_returns_themen() {
    UUID betreuerId = UUID.randomUUID();
    UUID themaId = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(betreuerId, "Anna");
    betreuer.assignThema(themaId);

    when(betreuerService.getById(betreuerId)).thenReturn(betreuer);

    List<Thema> result = service.listThemenByBetreuer(betreuerId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(themaId);
  }

  @Test
  @DisplayName("Betreuer darf nur eigene Zuordnungen verwalten")
  void betreuer_darf_nur_eigene_zuordnungen() {
    UUID betreuerId = UUID.randomUUID();
    UUID fremderBetreuerId = UUID.randomUUID();
    UUID themaId = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(betreuerId, "Anna");

    when(currentUser.hasRole(SecurityRoles.ROLE_ADMIN)).thenReturn(false);
    when(currentUser.hasRole(SecurityRoles.ROLE_BETREUER)).thenReturn(true);
    when(currentUser.login()).thenReturn("anna");
    when(betreuerService.findByGithubLogin("anna")).thenReturn(java.util.Optional.of(betreuer));
    when(themaService.getById(themaId)).thenReturn(new Thema(themaId, "Thema"));
    when(themaService.save(any(Thema.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.assignBetreuerToThema(themaId, betreuerId);

    assertThatThrownBy(() -> service.assignBetreuerToThema(themaId, fremderBetreuerId))
        .isInstanceOf(ZuordnungNichtErlaubtException.class);
  }

  @Test
  @DisplayName("Studierende duerfen keine Zuordnungen erstellen")
  void studierende_duerfen_nicht_zuordnen() {
    UUID betreuerId = UUID.randomUUID();
    UUID themaId = UUID.randomUUID();

    when(currentUser.hasRole(SecurityRoles.ROLE_ADMIN)).thenReturn(false);
    when(currentUser.hasRole(SecurityRoles.ROLE_BETREUER)).thenReturn(false);

    assertThatThrownBy(() -> service.assignThemaToBetreuer(betreuerId, themaId))
        .isInstanceOf(ZuordnungNichtErlaubtException.class);
  }
}
