package wiegand.luca.ThesisIsComing.presentationDriving;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerThemenBeziehungService;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.ThemaListIsEmpty;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.presentationDriving.security.SecurityConfiguration;

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

@WebMvcTest(ThemaController.class)
@Import({SecurityConfiguration.class, TestOAuth2ClientConfiguration.class})
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
    @WithMockUser(roles = "STUDIERENDE")
    void themen_returns_correct_view() throws Exception {

        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(service.listAll()).thenReturn(List.of(t1));

        mvc.perform(get("/themen"))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/list"))
                .andExpect(model().attributeExists("themen"));
    }

    @Test
    @DisplayName("/themen/{id} returns correct Thema")
    @WithMockUser(roles = "STUDIERENDE")
    void themen_id_returns_correct_thema() throws Exception {

        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        Betreuer betreuer = new Betreuer(UUID.randomUUID(), "Anna");
        when(service.getById(any(UUID.class))).thenReturn(t1);
        when(service.findBetreuerByThemaIdOrderByName(t1.getId())).thenReturn(List.of(betreuer));
        when(betreuerService.listAll()).thenReturn(List.of(betreuer));

        mvc.perform(get("/themen/{id}", t1.getId())).andExpect(status().isOk())
                .andExpect(view().name("thema/detail"))
                .andExpect(model().attributeExists("thema"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("allBetreuer"))
                .andExpect(content().string(containsString("Anna")));
    }

    @Test
    @DisplayName("/themen/{id}/betreuer assigns a Betreuer and redirects")
    @WithMockUser(roles = "ADMIN")
    void assign_betreuer_redirects() throws Exception {
        UUID themaId = UUID.randomUUID();
        UUID betreuerId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/betreuer", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("betreuerId", betreuerId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + themaId));

        verify(beziehungService).assignBetreuerToThema(themaId, betreuerId);
    }

    @Test
    @DisplayName("/themen/{id}/betreuer is forbidden for students")
    @WithMockUser(roles = "STUDIERENDE")
    void assign_betreuer_forbidden_for_students() throws Exception {
        UUID themaId = UUID.randomUUID();
        UUID betreuerId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/betreuer", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("betreuerId", betreuerId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/themen/{id}/links adds a link and redirects")
    @WithMockUser(roles = "ADMIN")
    void add_link_redirects() throws Exception {
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/links", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + themaId));

        verify(service).addLink(themaId, "https://example.com", "Beispiel");
    }

    @Test
    @DisplayName("/themen/{id}/links/update updates a link and redirects")
    @WithMockUser(roles = "ADMIN")
    void update_link_redirects() throws Exception {
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/links/update", themaId)
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
    @WithMockUser(roles = "ADMIN")
    void delete_link_redirects() throws Exception {
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/links/delete", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/themen/" + themaId));

        verify(service).removeLink(themaId, "https://example.com", "Beispiel");
    }

    @Test
    @DisplayName("/themen/{id}/links is forbidden for students")
    @WithMockUser(roles = "STUDIERENDE")
    void add_link_forbidden_for_students() throws Exception {
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/themen/{id}/links", themaId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("url", "https://example.com")
                        .param("anzeigetext", "Beispiel"))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("/themen returns 404 when list is empty")
    @WithMockUser(roles = "STUDIERENDE")
    void themen_returns_404_when_list_empty() throws Exception {
        when(service.listAll()).thenReturn(List.of());

        mvc.perform(get("/themen"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    @DisplayName("/themen/{id} returns 404 when Thema not found")
    @WithMockUser(roles = "STUDIERENDE")
    void themen_id_returns_404_when_not_found() throws Exception {
        when(service.getById(any(UUID.class))).thenThrow(new ThemaNotFoundException(UUID.randomUUID()));

        mvc.perform(get("/themen/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attributeExists("message"));
    }




}
