package wiegand.luca.ThesisIsComing.ApplicationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerRoleAssignmentException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.OptimisticLockingException;
import wiegand.luca.ThesisIsComing.ApplicationService.DateiReplaceData;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BetreuerServiceTest {

  @Mock private BetreuerRepository repo;

  @Mock private DateiSpeicher dateiSpeicher;

  @Mock private CurrentUser currentUser;

  private BetreuerService service;

  @BeforeEach
  void setup() {
    service = new BetreuerService(repo, dateiSpeicher, currentUser);
    lenient().when(currentUser.hasRole(SecurityRoles.ROLE_ADMIN)).thenReturn(true);
  }

  @Test
  void listAll_returns_all_betreuer() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Betreuer b1 = new Betreuer(id1, "Anna");
    Betreuer b2 = new Betreuer(id2, "Max");
    when(repo.findAll()).thenReturn(List.of(b1, b2));

    List<Betreuer> result = service.listAll();

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(b1, b2);
    verify(repo).findAll();
  }

  @Test
  void searchBetreuer_returns_all_when_no_filters() {
    Betreuer b1 = new Betreuer(UUID.randomUUID(), "Anna");
    when(repo.findAll()).thenReturn(List.of(b1));

    List<Betreuer> result = service.searchBetreuer(new SucheForm(null, null));

    assertThat(result).containsExactly(b1);
    verify(repo).findAll();
  }

  @Test
  void searchBetreuer_filters_by_name() {
    Betreuer b1 = new Betreuer(UUID.randomUUID(), "Anna");
    when(repo.findByNameContainsIgnoreCase("Ann")).thenReturn(List.of(b1));

    List<Betreuer> result = service.searchBetreuer(new SucheForm("Ann", null));

    assertThat(result).containsExactly(b1);
    verify(repo).findByNameContainsIgnoreCase("Ann");
  }

  @Test
  void searchBetreuer_filters_by_fachgebiet() {
    Betreuer b1 = new Betreuer(UUID.randomUUID(), "Anna");
    when(repo.findByFachgebiet("AI")).thenReturn(List.of(b1));

    List<Betreuer> result = service.searchBetreuer(new SucheForm(null, "AI"));

    assertThat(result).containsExactly(b1);
    verify(repo).findByFachgebiet("AI");
  }

  @Test
  void searchBetreuer_filters_by_name_and_fachgebiet() {
    Betreuer b1 = new Betreuer(UUID.randomUUID(), "Anna");
    when(repo.findByNameContainsIgnoreCaseAndFachgebiet("Ann", "AI")).thenReturn(List.of(b1));

    List<Betreuer> result = service.searchBetreuer(new SucheForm("Ann", "AI"));

    assertThat(result).containsExactly(b1);
    verify(repo).findByNameContainsIgnoreCaseAndFachgebiet("Ann", "AI");
  }

  @Test
  void getById_throws_when_missing() {
    when(repo.findById(any(UUID.class))).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.getById(UUID.randomUUID()))
        .isInstanceOf(BetreuerNotFoundException.class);
    verify(repo).findById(any(UUID.class));
  }

  @Test
  void addLink_saves_updated_betreuer() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.addLink(id, "https://example.com", "Beispiel");

    ArgumentCaptor<Betreuer> captor = ArgumentCaptor.forClass(Betreuer.class);
    verify(repo).save(captor.capture());
    assertThat(captor.getValue().getLinks())
        .containsExactly(new Link("https://example.com", "Beispiel"));
  }

  @Test
  void updateLink_replaces_existing_link() {
    UUID id = UUID.randomUUID();
    Link oldLink = new Link("https://old", "Alt");
    Betreuer betreuer = new Betreuer(id, "Anna", null, Set.of(), Set.of(), Set.of(oldLink));
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.updateLink(id, "https://old", "Alt", "https://neu", "Neu");

    ArgumentCaptor<Betreuer> captor = ArgumentCaptor.forClass(Betreuer.class);
    verify(repo).save(captor.capture());
    assertThat(captor.getValue().getLinks()).containsExactly(new Link("https://neu", "Neu"));
  }

  @Test
  void addLink_throws_when_invalid() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));

    assertThatThrownBy(() -> service.addLink(id, " ", "")).isInstanceOf(InvalidLinkException.class);
  }

  @Test
  void createBetreuer_saves_and_returns_created_betreuer() {
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Betreuer created = service.createBetreuer("Anna", "anna@uni.de", "M-1", Set.of());

    ArgumentCaptor<Betreuer> captor = ArgumentCaptor.forClass(Betreuer.class);
    verify(repo).save(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("Anna");
    assertThat(created.getName()).isEqualTo("Anna");
  }

  @Test
  void createBetreuer_throws_when_name_blank() {
    assertThatThrownBy(() -> service.createBetreuer(" ", null, null, Set.of()))
        .isInstanceOf(BetreuerValidationException.class);
    verify(repo, never()).save(any(Betreuer.class));
  }

  @Test
  void updateBetreuer_updates_details() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Alt");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Betreuer updated =
        service.updateBetreuer(id, "Neu", "neu@uni.de", "M-2", Set.of(), betreuer.getVersion());

    assertThat(updated.getName()).isEqualTo("Neu");
    assertThat(updated.getKontaktinformationen().getEmail()).isEqualTo("neu@uni.de");
  }

  @Test
  void updateBetreuer_throws_when_name_blank() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Alt");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));

    assertThatThrownBy(
            () -> service.updateBetreuer(id, " ", null, null, Set.of(), betreuer.getVersion()))
        .isInstanceOf(BetreuerValidationException.class);
    verify(repo, never()).save(any(Betreuer.class));
  }

  @Test
  void updateBetreuer_throws_when_version_mismatch() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Alt");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));

    assertThatThrownBy(
            () -> service.updateBetreuer(id, "Neu", null, null, Set.of(), 999L))
        .isInstanceOf(OptimisticLockingException.class);
    verify(repo, never()).save(any(Betreuer.class));
  }

  @Test
  void createBetreuer_sets_fachgebiete() {
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Betreuer created = service.createBetreuer("Anna", null, null, Set.of(new Fachgebiet("AI")));

    assertThat(created.getFachgebiete()).hasSize(1);
  }

  @Test
  void deleteBetreuer_deletes_when_found() {
    UUID id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.of(new Betreuer(id, "Anna")));

    service.deleteBetreuer(id);

    verify(repo).deleteById(id);
  }

  @Test
  void assignBetreuerRole_sets_github_login() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.findByGithubLogin("anna-gh")).thenReturn(Optional.empty());
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Betreuer updated = service.assignBetreuerRole(id, "anna-gh");

    assertThat(updated.getGithubLogin()).isEqualTo("anna-gh");
    verify(repo).save(any(Betreuer.class));
  }

  @Test
  void assignBetreuerRole_throws_when_login_taken() {
    UUID id = UUID.randomUUID();
    UUID otherId = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    Betreuer other = new Betreuer(otherId, "Max");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.findByGithubLogin("taken")).thenReturn(Optional.of(other));

    assertThatThrownBy(() -> service.assignBetreuerRole(id, "taken"))
        .isInstanceOf(BetreuerRoleAssignmentException.class);
    verify(repo, never()).save(any(Betreuer.class));
  }

  @Test
  void removeBetreuerRole_throws_when_no_login() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));

    assertThatThrownBy(() -> service.removeBetreuerRole(id))
        .isInstanceOf(BetreuerRoleAssignmentException.class);
    verify(repo, never()).save(any(Betreuer.class));
  }

  @Test
  void uploadDatei_saves_metadata_and_file() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(dateiSpeicher.speichere(any(String.class), any(byte[].class))).thenReturn("stored.pdf");
    when(currentUser.login()).thenReturn("anna");

    DateiUploadData data =
        new DateiUploadData(
            "Dokument",
            "Beschreibung",
            "info.pdf",
            12L,
            "content".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    Informationsdatei result = service.uploadDatei(id, data);

    ArgumentCaptor<Betreuer> captor = ArgumentCaptor.forClass(Betreuer.class);
    verify(repo).save(captor.capture());
    assertThat(result.getDateityp()).isEqualTo(Dateityp.PDF);
    assertThat(result.getOriginalDateiname()).isEqualTo("info.pdf");
    assertThat(result.getGroesseInBytes()).isEqualTo(12L);
    assertThat(captor.getValue().getDateien()).hasSize(1);
  }

  @Test
  void uploadDatei_throws_when_file_too_large() {
    UUID id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.of(new Betreuer(id, "Anna")));
    byte[] content = new byte[(10 * 1024 * 1024) + 1];

    DateiUploadData data =
        new DateiUploadData("Dokument", null, "info.pdf", content.length, content);

    assertThatThrownBy(() -> service.uploadDatei(id, data))
        .isInstanceOf(DateiValidationException.class);
    verify(repo, never()).save(any(Betreuer.class));
  }

  @Test
  void uploadDatei_throws_when_type_unknown() {
    UUID id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.of(new Betreuer(id, "Anna")));
    DateiUploadData data =
        new DateiUploadData(
            "Dokument",
            null,
            "info.txt",
            5L,
            "content".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    assertThatThrownBy(() -> service.uploadDatei(id, data))
        .isInstanceOf(DateiValidationException.class);
    verify(repo, never()).save(any(Betreuer.class));
  }

  @Test
  void updateDateiMetadaten_updates_fields() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    Informationsdatei datei =
        new Informationsdatei(
            "Alt",
            "Beschreibung",
            "anna",
            java.time.LocalDateTime.now(),
            "stored.pdf",
            Dateityp.PDF,
            "info.pdf",
            12L);
    betreuer.addDatei(datei);
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Informationsdatei updated =
        service.updateDateiMetadaten(id, "stored.pdf", "Neu", "Neu Beschreibung");

    assertThat(updated.getTitel()).isEqualTo("Neu");
    assertThat(updated.getBeschreibung()).isEqualTo("Neu Beschreibung");
    verify(repo).save(betreuer);
  }

  @Test
  void replaceDatei_updates_metadata_and_replaces_file() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    Informationsdatei datei =
        new Informationsdatei(
            "Alt",
            null,
            "anna",
            java.time.LocalDateTime.now(),
            "stored.pdf",
            Dateityp.PDF,
            "info.pdf",
            12L);
    betreuer.addDatei(datei);
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(dateiSpeicher.speichere(any(String.class), any(byte[].class))).thenReturn("neu.pdf");

    DateiReplaceData data =
        new DateiReplaceData(
            "neu.pdf", 5L, "content".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    Informationsdatei replaced = service.replaceDatei(id, "stored.pdf", data);

    assertThat(replaced.getDateisystemPfad()).isEqualTo("neu.pdf");
    assertThat(replaced.getOriginalDateiname()).isEqualTo("neu.pdf");
    assertThat(replaced.getGroesseInBytes()).isEqualTo(5L);
    verify(repo).save(betreuer);
    verify(dateiSpeicher).loesche("stored.pdf");
  }

  @Test
  void deleteDatei_removes_file_and_persists() {
    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Anna");
    Informationsdatei datei =
        new Informationsdatei(
            "Alt",
            null,
            "anna",
            java.time.LocalDateTime.now(),
            "stored.pdf",
            Dateityp.PDF,
            "info.pdf",
            12L);
    betreuer.addDatei(datei);
    when(repo.findById(id)).thenReturn(Optional.of(betreuer));
    when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.deleteDatei(id, "stored.pdf");

    assertThat(betreuer.getDateien()).isEmpty();
    verify(repo).save(betreuer);
    verify(dateiSpeicher).loesche("stored.pdf");
  }
}
