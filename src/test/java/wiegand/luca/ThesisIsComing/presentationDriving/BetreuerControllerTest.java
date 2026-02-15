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
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerThemenBeziehungService;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
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

@WebMvcTest(BetreuerController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration.class)
@ActiveProfiles("test")
class BetreuerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BetreuerService service;

    @MockitoBean
    private BetreuerThemenBeziehungService beziehungService;

    @MockitoBean
    private ThemaService themaService;

    @Test
    @DisplayName("'/betreuer' renders a list of Betreuer into HTML '/betreuer/list'.")
    void list_view_renders_betreuer_model() throws Exception {
        Betreuer betreuer = new Betreuer(UUID.randomUUID(), "Anna");
        when(service.listAll()).thenReturn(List.of(betreuer));

        mvc.perform(get("/betreuer")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/list"))
                .andExpect(model().attributeExists("betreuer"));
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

        mvc.perform(get("/betreuer/{id}", id)
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/detail"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("themen"))
                .andExpect(model().attributeExists("allThemen"))
                .andExpect(content().string(containsString("Thema A")));

    }

    @Test
    @DisplayName("'/betreuer/{id}/themen' assigns a Thema and redirects")
    void assign_thema_redirects() throws Exception {
        UUID betreuerId = UUID.randomUUID();
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/betreuer/{id}/themen", betreuerId)
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

        mvc.perform(post("/betreuer/{id}/themen", betreuerId)
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("themaId", themaId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("'/betreuer/{id}/links' adds a link and redirects")
    void add_link_redirects() throws Exception {
        UUID betreuerId = UUID.randomUUID();

        mvc.perform(post("/betreuer/{id}/links", betreuerId)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/betreuer/" + betreuerId));

        verify(service).addLink(betreuerId, "https://example.com", "Beispiel");
    }

    @Test
    @DisplayName("'/betreuer/{id}/links/update' updates a link and redirects")
    void update_link_redirects() throws Exception {
        UUID betreuerId = UUID.randomUUID();

        mvc.perform(post("/betreuer/{id}/links/update", betreuerId)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("oldUrl", "https://old")
                        .param("oldAnzeigetext", "Alt")
                        .param("url", "https://neu")
                        .param("anzeigetext", "Neu"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/betreuer/" + betreuerId));

        verify(service).updateLink(betreuerId, "https://old", "Alt", "https://neu", "Neu");
    }

    @Test
    @DisplayName("'/betreuer/{id}/links/delete' deletes a link and redirects")
    void delete_link_redirects() throws Exception {
        UUID betreuerId = UUID.randomUUID();

        mvc.perform(post("/betreuer/{id}/links/delete", betreuerId)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/betreuer/" + betreuerId));

        verify(service).removeLink(betreuerId, "https://example.com", "Beispiel");
    }

    @Test
    @DisplayName("'/betreuer/{id}/links' is forbidden for students")
    void add_link_forbidden_for_students() throws Exception {
        UUID betreuerId = UUID.randomUUID();

        mvc.perform(post("/betreuer/{id}/links", betreuerId)
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("'/betreuer/neu' returns form view for admins")
    void betreuer_neu_returns_form_view() throws Exception {
        mvc.perform(get("/betreuer/neu")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/form"))
                .andExpect(model().attributeExists("betreuerForm"));
    }

    @Test
    @DisplayName("POST /betreuer creates Betreuer and redirects")
    void create_betreuer_redirects() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.createBetreuer(any(String.class), any(String.class), any(String.class)))
                .thenReturn(new Betreuer(id, "Anna"));

        mvc.perform(post("/betreuer")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", "Anna")
                        .param("email", "anna@uni.de")
                        .param("matrikelnummer", "M-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/betreuer/" + id));
    }

    @Test
    @DisplayName("POST /betreuer returns form errors for blank name")
    void create_betreuer_returns_errors_on_blank_name() throws Exception {
        mvc.perform(post("/betreuer")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/form"))
                .andExpect(model().attributeHasFieldErrors("betreuerForm", "name"));
    }

    @Test
    @DisplayName("/betreuer/{id}/bearbeiten returns form view")
    void edit_form_returns_view() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getById(id)).thenReturn(new Betreuer(id, "Anna"));

        mvc.perform(get("/betreuer/{id}/bearbeiten", id)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/form"))
                .andExpect(model().attributeExists("betreuerForm"));
    }

    @Test
    @DisplayName("POST /betreuer/{id}/bearbeiten updates and redirects")
    void update_betreuer_redirects() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.updateBetreuer(any(UUID.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(new Betreuer(id, "Anna"));

        mvc.perform(post("/betreuer/{id}/bearbeiten", id)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", "Anna")
                        .param("email", "")
                        .param("matrikelnummer", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/betreuer/" + id));
    }

    @Test
    @DisplayName("POST /betreuer/{id}/loeschen deletes and redirects")
    void delete_betreuer_redirects() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(post("/betreuer/{id}/loeschen", id)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/betreuer"));

        verify(service).deleteBetreuer(id);
    }

    @Test
    @DisplayName("POST /betreuer is forbidden for students")
    void create_betreuer_forbidden_for_students() throws Exception {
        mvc.perform(post("/betreuer")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", "Anna"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /betreuer/{id}/loeschen is forbidden for students")
    void delete_betreuer_forbidden_for_students() throws Exception {
        mvc.perform(post("/betreuer/{id}/loeschen", UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());
    }
}
