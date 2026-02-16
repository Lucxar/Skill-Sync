package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ZuordnungNichtErlaubtException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

@Service
public class BetreuerThemenBeziehungService {

  private final BetreuerService betreuerService;
  private final ThemaService themaService;
  private final CurrentUser currentUser;

  public BetreuerThemenBeziehungService(
      BetreuerService betreuerService, ThemaService themaService, CurrentUser currentUser) {
    this.betreuerService = betreuerService;
    this.themaService = themaService;
    this.currentUser = currentUser;
  }

  public Betreuer assignThemaToBetreuer(UUID betreuerId, UUID themaId) {
    assertBetreuerZugriff(betreuerId);
    Betreuer betreuer = betreuerService.getById(betreuerId);
    betreuer.assignThema(themaId);
    return betreuerService.save(betreuer);
  }

  public List<Thema> listThemenByBetreuer(UUID betreuerId) {
    Betreuer betreuer = betreuerService.getById(betreuerId);
    return betreuer.getThemen().stream().toList();
  }

  public Thema assignBetreuerToThema(UUID themaId, UUID betreuerId) {
    assertBetreuerZugriff(betreuerId);
    Thema thema = themaService.getById(themaId);
    thema.assignBetreuer(betreuerId);
    return themaService.save(thema);
  }

  private void assertBetreuerZugriff(UUID betreuerId) {
    if (currentUser.hasRole(SecurityRoles.ROLE_ADMIN)) {
      return;
    }
    if (!currentUser.hasRole(SecurityRoles.ROLE_BETREUER)) {
      throw new ZuordnungNichtErlaubtException("Keine Berechtigung fuer Zuordnung.");
    }
    String login = currentUser.login();
    if (login == null || login.isBlank()) {
      throw new ZuordnungNichtErlaubtException("Kein Betreuer-Login vorhanden.");
    }
    UUID aktuellerBetreuerId =
        betreuerService
            .findByGithubLogin(login)
            .map(Betreuer::getId)
            .orElseThrow(
                () -> new ZuordnungNichtErlaubtException("Kein Betreuer fuer Login hinterlegt."));
    if (!aktuellerBetreuerId.equals(betreuerId)) {
      throw new ZuordnungNichtErlaubtException("Betreuer darf nur eigene Zuordnungen verwalten.");
    }
  }
}
