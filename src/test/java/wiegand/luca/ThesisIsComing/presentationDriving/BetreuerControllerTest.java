package wiegand.luca.ThesisIsComing.presentationDriving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.betreuerForm;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.dateiUploadForm;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.linkForm;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.linkUpdateForm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerThemenBeziehungService;
import wiegand.luca.ThesisIsComing.ApplicationService.DateiReplaceData;
import wiegand.luca.ThesisIsComing.ApplicationService.SucheForm;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;

@WebMvcTest(BetreuerController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration.class)
@ActiveProfiles("test")
class BetreuerControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private BetreuerService service;

  @MockitoBean private BetreuerThemenBeziehungService beziehungService;

  @MockitoBean private ThemaService themaService;

  @Test
  @DisplayName("'/betreuer' renders a list of Betreuer into HTML '/betreuer/list'.")
  void list_view_renders_betreuer_model() throws Exception {
    Betreuer betreuer = new Betreuer(UUID.randomUUID(), "Anna");
    when(service.searchBetreuer(any(SucheForm.class))).thenReturn(List.of(betreuer));

    mvc.perform(
            get("/betreuer")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isOk())
        .andExpect(view().name("betreuer/list"))
        .andExpect(model().attributeExists("betreuer"))
        .andExpect(model().attributeExists("sucheForm"));
  }

  @Test
  @DisplayName("'/betreuer' applies search parameters")
  void list_view_applies_search_params() throws Exception {
    Betreuer betreuer = new Betreuer(UUID.randomUUID(), "Anna");
    when(service.searchBetreuer(any(SucheForm.class))).thenReturn(List.of(betreuer));

    mvc.perform(
            get("/betreuer")
                .param("suchbegriff", "Ann")
                .param("fachgebiet", "AI")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isOk())
        .andExpect(view().name("betreuer/list"))
        .andExpect(model().attributeExists("sucheForm"));

    ArgumentCaptor<SucheForm> captor = ArgumentCaptor.forClass(SucheForm.class);
    verify(service).searchBetreuer(captor.capture());
    assertThat(captor.getValue().suchbegriff()).isEqualTo("Ann");
    assertThat(captor.getValue().fachgebiet()).isEqualTo("AI");
  }

  @Test
  @DisplayName("'/betreuer/{id}' renders a Betreuer into HTML '/betreuer/detail'")
  void find_by_ID_returns_correct_betreuer_model() throws Exception {

    UUID id = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(id, "Tom");
    Thema thema = new Thema(UUID.randomUUID(), "Thema A");
    when(service.getById(id)).thenReturn(betreuer);
    when(service.findThemenByBetreuerIdOrderByTitel(id)).thenReturn(List.of(thema));
    when(themaService.listAll()).thenReturn(List.of(thema));

    var result =
        mvc.perform(
                get("/betreuer/{id}", id)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
            .andExpect(status().isOk())
            .andExpect(view().name("betreuer/detail"))
            .andExpect(model().attributeExists("betreuer"))
            .andExpect(model().attributeExists("themen"))
            .andExpect(model().attributeExists("allThemen"))
            .andReturn();

    var doc = org.jsoup.Jsoup.parse(result.getResponse().getContentAsString());
    assertThat(doc.text()).contains("Thema A");
  }

  @Test
  @DisplayName("'/betreuer/{id}/themen' assigns a Thema and redirects")
  void assign_thema_redirects() throws Exception {
    UUID betreuerId = UUID.randomUUID();
    UUID themaId = UUID.randomUUID();

    mvc.perform(
            post("/betreuer/{id}/themen", betreuerId)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("themaId", themaId.toString()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + betreuerId));

    verify(beziehungService).assignThemaToBetreuer(betreuerId, themaId);
  }

  @Test
  @DisplayName("'/betreuer/{id}/themen' is forbidden for students")
  void assign_thema_forbidden_for_students() throws Exception {
    UUID betreuerId = UUID.randomUUID();
    UUID themaId = UUID.randomUUID();

    mvc.perform(
            post("/betreuer/{id}/themen", betreuerId)
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("themaId", themaId.toString()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("'/betreuer/{id}/links' adds a link and redirects")
  void add_link_redirects() throws Exception {
    UUID betreuerId = UUID.randomUUID();

    mvc.perform(
            post("/betreuer/{id}/links", betreuerId)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("linkForm", linkForm()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + betreuerId));

    verify(service).addLink(betreuerId, "https://example.com", "Beispiel");
  }

  @Test
  @DisplayName("'/betreuer/{id}/links/update' updates a link and redirects")
  void update_link_redirects() throws Exception {
    UUID betreuerId = UUID.randomUUID();

    mvc.perform(
            post("/betreuer/{id}/links/update", betreuerId)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("linkUpdateForm", linkUpdateForm()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + betreuerId));

    verify(service).updateLink(betreuerId, "https://old", "Alt", "https://neu", "Neu");
  }

  @Test
  @DisplayName("'/betreuer/{id}/links/delete' deletes a link and redirects")
  void delete_link_redirects() throws Exception {
    UUID betreuerId = UUID.randomUUID();

    mvc.perform(
            post("/betreuer/{id}/links/delete", betreuerId)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("linkForm", linkForm()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + betreuerId));

    verify(service).removeLink(betreuerId, "https://example.com", "Beispiel");
  }

  @Test
  @DisplayName("'/betreuer/{id}/links' is forbidden for students")
  void add_link_forbidden_for_students() throws Exception {
    UUID betreuerId = UUID.randomUUID();

    mvc.perform(
            post("/betreuer/{id}/links", betreuerId)
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("linkForm", linkForm()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("'/betreuer/neu' returns form view for admins")
  void betreuer_neu_returns_form_view() throws Exception {
    mvc.perform(
            get("/betreuer/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(view().name("betreuer/form"))
        .andExpect(model().attributeExists("betreuerForm"));
  }

  @Test
  @DisplayName("POST /betreuer creates Betreuer and redirects")
  void create_betreuer_redirects() throws Exception {
    UUID id = UUID.randomUUID();
    when(service.createBetreuer(
            any(String.class), any(String.class), any(String.class), any(Set.class)))
        .thenReturn(new Betreuer(id, "Anna"));

    mvc.perform(
            post("/betreuer")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("betreuerForm", betreuerForm()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + id));
  }

  @Test
  @DisplayName("POST /betreuer returns form errors for blank name")
  void create_betreuer_returns_errors_on_blank_name() throws Exception {
    mvc.perform(
            post("/betreuer")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("betreuerForm", betreuerForm(" ", null, null)))
        .andExpect(status().isOk())
        .andExpect(view().name("betreuer/form"))
        .andExpect(model().attributeHasFieldErrors("betreuerForm", "name"));
  }

  @Test
  @DisplayName("/betreuer/{id}/bearbeiten returns form view")
  void edit_form_returns_view() throws Exception {
    UUID id = UUID.randomUUID();
    when(service.getById(id)).thenReturn(new Betreuer(id, "Anna"));

    mvc.perform(
            get("/betreuer/{id}/bearbeiten", id)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(view().name("betreuer/form"))
        .andExpect(model().attributeExists("betreuerForm"));
  }

  @Test
  @DisplayName("POST /betreuer/{id}/bearbeiten updates and redirects")
  void update_betreuer_redirects() throws Exception {
    UUID id = UUID.randomUUID();
    when(service.updateBetreuer(
            any(UUID.class),
            any(String.class),
            any(String.class),
            any(String.class),
            any(Set.class),
            any(Long.class)))
        .thenReturn(new Betreuer(id, "Anna"));

    mvc.perform(
            post("/betreuer/{id}/bearbeiten", id)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("betreuerForm", betreuerForm("Anna", "", "")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + id));
  }

  @Test
  @DisplayName("POST /betreuer/{id}/loeschen deletes and redirects")
  void delete_betreuer_redirects() throws Exception {
    UUID id = UUID.randomUUID();

    mvc.perform(
            post("/betreuer/{id}/loeschen", id)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer"));

    verify(service).deleteBetreuer(id);
  }

  @Test
  @DisplayName("POST /betreuer is forbidden for students")
  void create_betreuer_forbidden_for_students() throws Exception {
    mvc.perform(
            post("/betreuer")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("betreuerForm", betreuerForm("Anna", null, null)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("POST /betreuer/{id}/loeschen is forbidden for students")
  void delete_betreuer_forbidden_for_students() throws Exception {
    mvc.perform(
            post("/betreuer/{id}/loeschen", UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("POST /betreuer/{id}/dateien uploads and redirects")
  void upload_datei_redirects() throws Exception {
    UUID betreuerId = UUID.randomUUID();
    MockMultipartFile file =
        new MockMultipartFile(
            "datei",
            "info.pdf",
            "application/pdf",
            "content".getBytes(java.nio.charset.StandardCharsets.UTF_8));
    when(service.uploadDatei(any(UUID.class), any()))
        .thenReturn(
            new Informationsdatei(
                "Dokument",
                null,
                "anna",
                LocalDateTime.now(),
                "stored.pdf",
                Dateityp.PDF,
                "info.pdf",
                7L));

    mvc.perform(
            multipart("/betreuer/{id}/dateien", betreuerId)
                .file(file)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("dateiUploadForm", dateiUploadForm(file)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + betreuerId));
  }

  @Test
  @DisplayName("POST /betreuer/{id}/dateien is forbidden for students")
  void upload_datei_forbidden_for_students() throws Exception {
    UUID betreuerId = UUID.randomUUID();
    MockMultipartFile file =
        new MockMultipartFile(
            "datei",
            "info.pdf",
            "application/pdf",
            "content".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    mvc.perform(
            multipart("/betreuer/{id}/dateien", betreuerId)
                .file(file)
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("dateiUploadForm", dateiUploadForm(file)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("POST /betreuer/{id}/dateien/{dateiname}/metadaten updates metadata and redirects")
  void update_datei_metadaten_redirects() throws Exception {
    UUID betreuerId = UUID.randomUUID();
    String dateiname = "stored.pdf";

    mvc.perform(
            post("/betreuer/{id}/dateien/{dateiname}/metadaten", betreuerId, dateiname)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("titel", "Neu")
                .param("beschreibung", "Beschreibung"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + betreuerId));

    verify(service).updateDateiMetadaten(betreuerId, dateiname, "Neu", "Beschreibung");
  }

  @Test
  @DisplayName("POST /betreuer/{id}/dateien/{dateiname}/ersetzen replaces file and redirects")
  void replace_datei_redirects() throws Exception {
    UUID betreuerId = UUID.randomUUID();
    String dateiname = "stored.pdf";
    MockMultipartFile file =
        new MockMultipartFile(
            "datei",
            "neu.pdf",
            "application/pdf",
            "content".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    mvc.perform(
            multipart("/betreuer/{id}/dateien/{dateiname}/ersetzen", betreuerId, dateiname)
                .file(file)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + betreuerId));

    ArgumentCaptor<DateiReplaceData> captor = ArgumentCaptor.forClass(DateiReplaceData.class);
    verify(service)
        .replaceDatei(
            org.mockito.Mockito.eq(betreuerId), org.mockito.Mockito.eq(dateiname), captor.capture());
    assertThat(captor.getValue().originalDateiname()).isEqualTo("neu.pdf");
  }

  @Test
  @DisplayName("POST /betreuer/{id}/dateien/{dateiname}/loeschen deletes and redirects")
  void delete_datei_redirects() throws Exception {
    UUID betreuerId = UUID.randomUUID();
    String dateiname = "stored.pdf";

    mvc.perform(
            post("/betreuer/{id}/dateien/{dateiname}/loeschen", betreuerId, dateiname)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + betreuerId));

    verify(service).deleteDatei(betreuerId, dateiname);
  }

  @Test
  @DisplayName("/betreuer returns 404 when list is empty")
  void list_returns_404_when_empty() throws Exception {
    when(service.searchBetreuer(any(SucheForm.class))).thenReturn(List.of());

    mvc.perform(
            get("/betreuer")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isNotFound())
        .andExpect(view().name("errors/404"))
        .andExpect(model().attributeExists("message"));
  }

  @Test
  @DisplayName("/betreuer/{id} returns 404 when betreuer not found")
  void detail_returns_404_when_not_found() throws Exception {
    when(service.getById(any(UUID.class)))
        .thenThrow(new BetreuerNotFoundException(UUID.randomUUID()));

    mvc.perform(
            get("/betreuer/{id}", UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isNotFound())
        .andExpect(view().name("errors/404"))
        .andExpect(model().attributeExists("message"));
  }

  @Test
  @DisplayName("/betreuer/{id}/links shows error on invalid link")
  void add_link_shows_error_on_invalid_link() throws Exception {
    UUID betreuerId = UUID.randomUUID();
    Betreuer betreuer = new Betreuer(betreuerId, "Anna");
    Thema thema = new Thema(UUID.randomUUID(), "Thema A");
    when(service.getById(betreuerId)).thenReturn(betreuer);
    when(service.findThemenByBetreuerIdOrderByTitel(betreuerId)).thenReturn(List.of(thema));
    when(themaService.listAll()).thenReturn(List.of(thema));
    doThrow(new InvalidLinkException("Ungueltig"))
        .when(service)
        .addLink(betreuerId, "https://example.com", "Beispiel");

    mvc.perform(
            post("/betreuer/{id}/links", betreuerId)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("linkForm", linkForm()))
        .andExpect(status().isOk())
        .andExpect(view().name("betreuer/detail"))
        .andExpect(model().attributeExists("linkError"));
  }
}
