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
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.presentationDriving.security.SecurityConfiguration;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
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
@Import({SecurityConfiguration.class, TestOAuth2ClientConfiguration.class})
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
    @WithMockUser(roles = "STUDIERENDE")
    void list_view_renders_betreuer_model() throws Exception {
        Betreuer betreuer = new Betreuer(UUID.randomUUID(), "Anna");
        when(service.listAll()).thenReturn(List.of(betreuer));

        mvc.perform(get("/betreuer"))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/list"))
                .andExpect(model().attributeExists("betreuer"));
    }

    @Test
    @DisplayName("'/betreuer/{id}' renders a Betreuer into HTML '/betreuer/detail'")
    @WithMockUser(roles = "STUDIERENDE")
    void find_by_ID_returns_correct_betreuer_model() throws Exception {

        UUID id = UUID.randomUUID();
        Betreuer betreuer = new Betreuer(id, "Tom");
        Thema thema = new Thema(UUID.randomUUID(), "Thema A");
        when(service.getById(id)).thenReturn(betreuer);
        when(service.findThemenByBetreuerIdOrderByTitel(id)).thenReturn(List.of(thema));
        when(themaService.listAll()).thenReturn(List.of(thema));

        mvc.perform(get("/betreuer/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/detail"))
                .andExpect(model().attributeExists("betreuer"))
                .andExpect(model().attributeExists("themen"))
                .andExpect(model().attributeExists("allThemen"))
                .andExpect(content().string(containsString("Thema A")));

    }

    @Test
    @DisplayName("'/betreuer/{id}/themen' assigns a Thema and redirects")
    @WithMockUser(roles = "ADMIN")
    void assign_thema_redirects() throws Exception {
        UUID betreuerId = UUID.randomUUID();
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/betreuer/{id}/themen", betreuerId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("themaId", themaId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/betreuer/" + betreuerId));

        verify(beziehungService).assignThemaToBetreuer(betreuerId, themaId);
    }

    @Test
    @DisplayName("'/betreuer/{id}/themen' is forbidden for students")
    @WithMockUser(roles = "STUDIERENDE")
    void assign_thema_forbidden_for_students() throws Exception {
        UUID betreuerId = UUID.randomUUID();
        UUID themaId = UUID.randomUUID();

        mvc.perform(post("/betreuer/{id}/themen", betreuerId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("themaId", themaId.toString()))
                .andExpect(status().isForbidden());
    }
}
