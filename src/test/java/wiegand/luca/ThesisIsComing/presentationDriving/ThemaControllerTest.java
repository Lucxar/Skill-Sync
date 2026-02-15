package wiegand.luca.ThesisIsComing.presentationDriving;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.mockito.ArgumentCaptor;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerThemenBeziehungService;
import wiegand.luca.ThesisIsComing.ApplicationService.SucheForm;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.ThemaListIsEmpty;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(ThemaController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration.class)
@ActiveProfiles("test")
public class ThemaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    ThemaService service;

    @MockitoBean
    BetreuerService betreuerService;

    @MockitoBean
    BetreuerThemenBeziehungService beziehungService;


    @Test
    @DisplayName("/themen returns 'thema/list' view")
    void themen_returns_correct_view() throws Exception {

        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(service.searchThemen(any(SucheForm.class))).thenReturn(List.of(t1));

        mvc.perform(get("/themen")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/list"))
                .andExpect(model().attributeExists("themen"))
                .andExpect(model().attributeExists("sucheForm"));
    }

    @Test
    @DisplayName("/themen applies search parameters")
    void themen_applies_search_params() throws Exception {
        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(service.searchThemen(any(SucheForm.class))).thenReturn(List.of(t1));

        mvc.perform(get("/themen")
                        .param("suchbegriff", "Thema")
                        .param("fachgebiet", "AI")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/list"))
                .andExpect(model().attributeExists("sucheForm"));

        ArgumentCaptor<SucheForm> captor = ArgumentCaptor.forClass(SucheForm.class);
        verify(service).searchThemen(captor.capture());
        assertThat(captor.getValue().suchbegriff()).isEqualTo("Thema");
        assertThat(captor.getValue().fachgebiet()).isEqualTo("AI");
    }

    @Test
    @DisplayName("/themen/{id} returns correct Thema")
    void themen_id_returns_correct_thema() throws Exception {

        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        Betreuer betreuer = new Betreuer(UUID.randomUUID(), "Anna");
        when(service.getById(any(UUID.class))).thenReturn(t1);
        when(service.findBetreuerByThemaIdOrderByName(t1.getId())).thenReturn(List.of(betreuer));
        when(betreuerService.listAll()).thenReturn(List.of(betreuer));

        mvc.perform(get("/themen/{id}", t1.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/detail"))
                .andExpect(model().attributeExists("thema"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("allBetreuer"))
                .andExpect(content().string(containsString("Anna")));
    }

    @Test
    @DisplayName("/themen/{id}/betreuer assigns a Betreuer and redirects")
    void assign_betreuer_redirects() throws Exception {
        UUID themaId = UUID.randomUUID();
        UUID betreuerId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/betreuer", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("betreuerId", betreuerId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + themaId));

        verify(beziehungService).assignBetreuerToThema(themaId, betreuerId);
    }

    @Test
    @DisplayName("/themen/{id}/betreuer is forbidden for students")
    void assign_betreuer_forbidden_for_students() throws Exception {
        UUID themaId = UUID.randomUUID();
        UUID betreuerId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/betreuer", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("betreuerId", betreuerId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/themen/{id}/links adds a link and redirects")
    void add_link_redirects() throws Exception {
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/links", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + themaId));

        verify(service).addLink(themaId, "https://example.com", "Beispiel");
    }

    @Test
    @DisplayName("/themen/{id}/links/update updates a link and redirects")
    void update_link_redirects() throws Exception {
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/links/update", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("oldUrl", "https://old")
                        .param("oldAnzeigetext", "Alt")
                        .param("url", "https://neu")
                        .param("anzeigetext", "Neu"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + themaId));

        verify(service).updateLink(themaId, "https://old", "Alt", "https://neu", "Neu");
    }

    @Test
    @DisplayName("/themen/{id}/links/delete deletes a link and redirects")
    void delete_link_redirects() throws Exception {
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/links/delete", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + themaId));

        verify(service).removeLink(themaId, "https://example.com", "Beispiel");
    }

    @Test
    @DisplayName("/themen/{id}/links is forbidden for students")
    void add_link_forbidden_for_students() throws Exception {
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/links", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/themen/neu returns form view for admins")
    void themen_neu_returns_form_view() throws Exception {
        mvc.perform(get("/themen/neu")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/form"))
                .andExpect(model().attributeExists("themaForm"));
    }

    @Test
    @DisplayName("POST /themen creates Thema and redirects")
    void create_thema_redirects() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.createThema(any(String.class), any(String.class)))
                .thenReturn(new Thema(id, "Neues Thema"));

        mvc.perform(post("/themen")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("titel", "Neues Thema")
                        .param("beschreibungMarkdown", "Beschreibung"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + id));
    }

    @Test
    @DisplayName("POST /themen returns form errors for blank title")
    void create_thema_returns_errors_on_blank_title() throws Exception {
        mvc.perform(post("/themen")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("titel", " ")
                        .param("beschreibungMarkdown", "Beschreibung"))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/form"))
                .andExpect(model().attributeHasFieldErrors("themaForm", "titel"));
    }

    @Test
    @DisplayName("/themen/{id}/bearbeiten returns form view")
    void edit_form_returns_view() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getById(id)).thenReturn(new Thema(id, "Thema 1"));

        mvc.perform(get("/themen/{id}/bearbeiten", id)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/form"))
                .andExpect(model().attributeExists("themaForm"));
    }

    @Test
    @DisplayName("POST /themen/{id}/bearbeiten updates and redirects")
    void update_thema_redirects() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.updateThema(any(UUID.class), any(String.class), any(String.class)))
                .thenReturn(new Thema(id, "Neu"));

        mvc.perform(post("/themen/{id}/bearbeiten", id)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("titel", "Neu")
                        .param("beschreibungMarkdown", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + id));
    }

    @Test
    @DisplayName("POST /themen/{id}/loeschen deletes and redirects")
    void delete_thema_redirects() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/loeschen", id)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen"));

        verify(service).deleteThema(id);
    }

    @Test
    @DisplayName("POST /themen is forbidden for students")
    void create_thema_forbidden_for_students() throws Exception {
        mvc.perform(post("/themen")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("titel", "Neues Thema"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /themen/{id}/loeschen is forbidden for students")
    void delete_thema_forbidden_for_students() throws Exception {
        mvc.perform(post("/themen/{id}/loeschen", UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("/themen returns 404 when list is empty")
    void themen_returns_404_when_list_empty() throws Exception {
        when(service.searchThemen(any(SucheForm.class))).thenReturn(List.of());

        mvc.perform(get("/themen")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    @DisplayName("/themen/{id} returns 404 when Thema not found")
    void themen_id_returns_404_when_not_found() throws Exception {
        when(service.getById(any(UUID.class))).thenThrow(new ThemaNotFoundException(UUID.randomUUID()));

        mvc.perform(get("/themen/{id}", UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attributeExists("message"));
    }




}
