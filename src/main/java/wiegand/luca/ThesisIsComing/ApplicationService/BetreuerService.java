package wiegand.luca.ThesisIsComing.ApplicationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;
import org.springframework.dao.OptimisticLockingFailureException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerRoleAssignmentException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.OptimisticLockingException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ZuordnungNichtErlaubtException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

@Service
public class BetreuerService {

  private static final long MAX_UPLOAD_BYTES = 10 * 1024 * 1024;

  private final BetreuerRepository betreuerRepository;
  private final DateiSpeicher dateiSpeicher;
  private final CurrentUser currentUser;

  public BetreuerService(
      BetreuerRepository betreuerRepository, DateiSpeicher dateiSpeicher, CurrentUser currentUser) {
    this.betreuerRepository = betreuerRepository;
    this.dateiSpeicher = dateiSpeicher;
    this.currentUser = currentUser;
  }

  public List<Betreuer> listAll() {
    return StreamSupport.stream(betreuerRepository.findAll().spliterator(), false).toList();
  }

  public List<Betreuer> searchBetreuer(SucheForm form) {
    SucheForm criteria = form == null ? new SucheForm(null, null) : form;
    String suchbegriff = criteria.normalizedSuchbegriff();
    Fachgebiet fachgebiet = criteria.toFachgebiet();
    boolean hasSuchbegriff = suchbegriff != null;
    boolean hasFachgebiet = fachgebiet != null;
    if (!hasSuchbegriff && !hasFachgebiet) {
      return listAll();
    }
    if (hasSuchbegriff && hasFachgebiet) {
      return betreuerRepository.findByNameContainsIgnoreCaseAndFachgebiet(
          suchbegriff, fachgebiet.getBezeichnung());
    }
    if (hasSuchbegriff) {
      return betreuerRepository.findByNameContainsIgnoreCase(suchbegriff);
    }
    return betreuerRepository.findByFachgebiet(fachgebiet.getBezeichnung());
  }

  public Betreuer getById(UUID id) {
    return betreuerRepository.findById(id).orElseThrow(() -> new BetreuerNotFoundException(id));
  }

  public Betreuer save(Betreuer betreuer) {
    return betreuerRepository.save(betreuer);
  }

  public Betreuer createBetreuer(
      String name, String email, String matrikelnummer, Set<Fachgebiet> fachgebiete) {
    try {
      Kontaktinformationen kontaktinformationen = new Kontaktinformationen(email, matrikelnummer);
      Betreuer betreuer = new Betreuer(name, kontaktinformationen);
      betreuer.replaceFachgebiete(fachgebiete);
      return betreuerRepository.save(betreuer);
    } catch (IllegalArgumentException ex) {
      throw new BetreuerValidationException(ex.getMessage());
    }
  }

  public Betreuer updateBetreuer(
      UUID id,
      String name,
      String email,
      String matrikelnummer,
      Set<Fachgebiet> fachgebiete,
      Long expectedVersion) {
    assertBetreuerOwner(id);
    Betreuer betreuer = getById(id);
    assertVersionMatches(betreuer.getVersion(), expectedVersion);
    try {
      betreuer.updateDetails(name, email, matrikelnummer);
      betreuer.replaceFachgebiete(fachgebiete);
      return saveWithOptimisticHandling(betreuer);
    } catch (IllegalArgumentException ex) {
      throw new BetreuerValidationException(ex.getMessage());
    }
  }

  public void deleteBetreuer(UUID id) {
    assertBetreuerOwner(id);
    getById(id);
    betreuerRepository.deleteById(id);
  }

  public java.util.Optional<Betreuer> findByGithubLogin(String githubLogin) {
    String normalized = normalizeGithubLogin(githubLogin);
    if (normalized == null) {
      return java.util.Optional.empty();
    }
    return betreuerRepository.findByGithubLogin(normalized);
  }

  public Betreuer assignBetreuerRole(UUID betreuerId, String githubLogin) {
    String normalized = normalizeGithubLogin(githubLogin);
    if (normalized == null) {
      throw new BetreuerRoleAssignmentException("GitHub-Login darf nicht leer sein");
    }
    Betreuer betreuer = getById(betreuerId);
    betreuerRepository
        .findByGithubLogin(normalized)
        .filter(existing -> !existing.getId().equals(betreuerId))
        .ifPresent(
            existing -> {
              throw new BetreuerRoleAssignmentException("GitHub-Login ist bereits vergeben");
            });
    try {
      betreuer.assignGithubLogin(normalized);
    } catch (IllegalArgumentException ex) {
      throw new BetreuerRoleAssignmentException(ex.getMessage());
    }
    return saveWithOptimisticHandling(betreuer);
  }

  public Betreuer removeBetreuerRole(UUID betreuerId) {
    Betreuer betreuer = getById(betreuerId);
    if (betreuer.getGithubLogin() == null || betreuer.getGithubLogin().isBlank()) {
      throw new BetreuerRoleAssignmentException("Betreuer hat keine GitHub-Zuordnung");
    }
    betreuer.removeGithubLogin();
    return saveWithOptimisticHandling(betreuer);
  }

  public List<Thema> listThemenByBetreuer(UUID betreuerId) {
    Betreuer betreuer = getById(betreuerId);
    return betreuer.getThemen().stream().toList();
  }

  public List<UUID> findThemaIdsByBetreuerId(UUID betreuerId) {
    return betreuerRepository.findThemaIdsByBetreuerId(betreuerId);
  }

  public List<Thema> findThemenByBetreuerId(UUID betreuerId) {
    return betreuerRepository.findThemenByBetreuerId(betreuerId);
  }

  public List<Thema> findThemenByBetreuerIdOrderByTitel(UUID betreuerId) {
    return betreuerRepository.findThemenByBetreuerIdOrderByTitel(betreuerId);
  }

  public long countThemenByBetreuerId(UUID betreuerId) {
    return betreuerRepository.countThemenByBetreuerId(betreuerId);
  }

  public boolean existsBetreuerThema(UUID betreuerId, UUID themaId) {
    return betreuerRepository.existsBetreuerThema(betreuerId, themaId);
  }

  public Informationsdatei uploadDatei(UUID betreuerId, DateiUploadData data) {
    assertBetreuerOwner(betreuerId);
    Betreuer betreuer = getById(betreuerId);
    DateiUploadData validated = validateUploadData(data);
    Dateityp dateityp = resolveDateityp(validated.originalDateiname());
    String gespeicherterName =
        dateiSpeicher.speichere(validated.originalDateiname(), validated.inhalt());
    String uploaderName = resolveUploaderName();
    Informationsdatei datei;
    try {
      datei =
          new Informationsdatei(
              validated.titel(),
              validated.beschreibung(),
              uploaderName,
              LocalDateTime.now(),
              gespeicherterName,
              dateityp,
              validated.originalDateiname(),
              validated.groesseInBytes());
    } catch (IllegalArgumentException ex) {
      throw new DateiValidationException(ex.getMessage());
    }
    betreuer.addDatei(datei);
    saveWithOptimisticHandling(betreuer);
    return datei;
  }

  public Informationsdatei findDatei(UUID betreuerId, String gespeicherterName) {
    Betreuer betreuer = getById(betreuerId);
    return findDatei(betreuer, gespeicherterName);
  }

  public Informationsdatei updateDateiMetadaten(
      UUID betreuerId, String gespeicherterName, String titel, String beschreibung) {
    assertBetreuerOwner(betreuerId);
    Betreuer betreuer = getById(betreuerId);
    Informationsdatei datei = findDatei(betreuer, gespeicherterName);
    try {
      datei.setTitel(titel);
      datei.setBeschreibung(beschreibung);
    } catch (IllegalArgumentException ex) {
      throw new DateiValidationException(ex.getMessage());
    }
    saveWithOptimisticHandling(betreuer);
    return datei;
  }

  public Informationsdatei replaceDatei(
      UUID betreuerId, String gespeicherterName, DateiReplaceData data) {
    assertBetreuerOwner(betreuerId);
    Betreuer betreuer = getById(betreuerId);
    Informationsdatei datei = findDatei(betreuer, gespeicherterName);
    DateiReplaceData validated = validateReplaceData(data);
    Dateityp dateityp = resolveDateityp(validated.originalDateiname());
    String neuerName = dateiSpeicher.speichere(validated.originalDateiname(), validated.inhalt());
    String alterName = datei.getDateisystemPfad();
    datei.setDateisystemPfad(neuerName);
    datei.setDateityp(dateityp);
    datei.setOriginalDateiname(validated.originalDateiname());
    datei.setGroesseInBytes(validated.groesseInBytes());
    datei.setUploaderName(resolveUploaderName());
    datei.setUploadDatum(LocalDateTime.now());
    saveWithOptimisticHandling(betreuer);
    if (alterName != null && !alterName.isBlank()) {
      dateiSpeicher.loesche(alterName);
    }
    return datei;
  }

  public void deleteDatei(UUID betreuerId, String gespeicherterName) {
    assertBetreuerOwner(betreuerId);
    Betreuer betreuer = getById(betreuerId);
    Informationsdatei datei = findDatei(betreuer, gespeicherterName);
    String dateisystemPfad = datei.getDateisystemPfad();
    betreuer.removeDatei(datei);
    saveWithOptimisticHandling(betreuer);
    if (dateisystemPfad != null && !dateisystemPfad.isBlank()) {
      dateiSpeicher.loesche(dateisystemPfad);
    }
  }

  public void addLink(UUID betreuerId, String url, String anzeigetext) {
    assertBetreuerOwner(betreuerId);
    Betreuer betreuer = getById(betreuerId);
    betreuer.addLink(new Link(url, anzeigetext));
    saveWithOptimisticHandling(betreuer);
  }

  public void updateLink(
      UUID betreuerId, String oldUrl, String oldAnzeigetext, String newUrl, String newAnzeigetext) {
    assertBetreuerOwner(betreuerId);
    Betreuer betreuer = getById(betreuerId);
    betreuer.updateLink(new Link(oldUrl, oldAnzeigetext), new Link(newUrl, newAnzeigetext));
    saveWithOptimisticHandling(betreuer);
  }

  public void removeLink(UUID betreuerId, String url, String anzeigetext) {
    assertBetreuerOwner(betreuerId);
    Betreuer betreuer = getById(betreuerId);
    betreuer.removeLink(new Link(url, anzeigetext));
    saveWithOptimisticHandling(betreuer);
  }

  private DateiUploadData validateUploadData(DateiUploadData data) {
    if (data == null) {
      throw new DateiValidationException("Datei-Daten fehlen");
    }
    if (data.inhalt() == null || data.inhalt().length == 0) {
      throw new DateiValidationException("Datei ist leer");
    }
    if (data.groesseInBytes() > MAX_UPLOAD_BYTES) {
      throw new DateiValidationException("Datei ist groesser als 10 MB");
    }
    if (data.originalDateiname() == null || data.originalDateiname().trim().isEmpty()) {
      throw new DateiValidationException("Dateiname fehlt");
    }
    return data;
  }

  private DateiReplaceData validateReplaceData(DateiReplaceData data) {
    if (data == null) {
      throw new DateiValidationException("Datei-Daten fehlen");
    }
    if (data.inhalt() == null || data.inhalt().length == 0) {
      throw new DateiValidationException("Datei ist leer");
    }
    if (data.groesseInBytes() > MAX_UPLOAD_BYTES) {
      throw new DateiValidationException("Datei ist groesser als 10 MB");
    }
    if (data.originalDateiname() == null || data.originalDateiname().trim().isEmpty()) {
      throw new DateiValidationException("Dateiname fehlt");
    }
    return data;
  }

  private Dateityp resolveDateityp(String dateiname) {
    try {
      return Dateityp.fromDateiname(dateiname);
    } catch (IllegalArgumentException ex) {
      throw new DateiValidationException(ex.getMessage());
    }
  }

  private String resolveUploaderName() {
    String login = currentUser.login();
    return (login == null || login.isBlank()) ? "unbekannt" : login;
  }

  private static String normalizeGithubLogin(String githubLogin) {
    if (githubLogin == null) {
      return null;
    }
    String trimmed = githubLogin.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private Betreuer saveWithOptimisticHandling(Betreuer betreuer) {
    try {
      return betreuerRepository.save(betreuer);
    } catch (OptimisticLockingFailureException ex) {
      throw new OptimisticLockingException(
          "Datensatz wurde zwischenzeitlich geaendert. Bitte Seite neu laden.");
    }
  }

  private static void assertVersionMatches(Long current, Long expected) {
    if (current == null || expected == null || !current.equals(expected)) {
      throw new OptimisticLockingException(
          "Datensatz wurde zwischenzeitlich geaendert. Bitte Seite neu laden.");
    }
  }

  private Informationsdatei findDatei(Betreuer betreuer, String gespeicherterName) {
    if (gespeicherterName == null || gespeicherterName.isBlank()) {
      throw new DateiNotFoundException("Datei wurde nicht gefunden");
    }
    return betreuer.getDateien().stream()
        .filter(datei -> gespeicherterName.equals(datei.getDateisystemPfad()))
        .findFirst()
        .orElseThrow(() -> new DateiNotFoundException("Datei wurde nicht gefunden"));
  }

  private void assertBetreuerOwner(UUID betreuerId) {
    if (currentUser.hasRole(SecurityRoles.ROLE_ADMIN)) {
      return;
    }
    if (!currentUser.hasRole(SecurityRoles.ROLE_BETREUER)) {
      throw new ZuordnungNichtErlaubtException("Keine Berechtigung fuer Betreuerprofil.");
    }
    String login = currentUser.login();
    if (login == null || login.isBlank()) {
      throw new ZuordnungNichtErlaubtException("Kein Betreuer-Login vorhanden.");
    }
    UUID aktuellerBetreuerId =
        findByGithubLogin(login)
            .map(Betreuer::getId)
            .orElseThrow(
                () -> new ZuordnungNichtErlaubtException("Kein Betreuer fuer Login hinterlegt."));
    if (!aktuellerBetreuerId.equals(betreuerId)) {
      throw new ZuordnungNichtErlaubtException(
          "Betreuer duerfen nur ihr eigenes Profil bearbeiten.");
    }
  }
}
