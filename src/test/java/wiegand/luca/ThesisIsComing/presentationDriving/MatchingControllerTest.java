package wiegand.luca.ThesisIsComing.presentationDriving;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import wiegand.luca.ThesisIsComing.ApplicationService.MatchingResult;
import wiegand.luca.ThesisIsComing.ApplicationService.MatchingService;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.BetreuerMatch;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.ThemaMatch;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(MatchingController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration.class)
@ActiveProfiles("test")
class MatchingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private MatchingService matchingService;

    @Test
    @DisplayName("GET /matching renders the matching form")
    void show_form_renders_view() throws Exception {
        mvc.perform(get("/matching")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(view().name("matching/form"))
                .andExpect(model().attributeExists("matchingForm"));
    }

    @Test
    @DisplayName("POST /matching redirects back on empty input")
    void submit_form_redirects_on_empty_input() throws Exception {
        mvc.perform(post("/matching")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("bestandeneKurse", "")
                        .param("interessensgebiete", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/matching"));
    }

    @Test
    @DisplayName("POST /matching redirects to results with params")
    void submit_form_redirects_to_results() throws Exception {
        mvc.perform(post("/matching")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("bestandeneKurse", "Compilerbau")
                        .param("interessensgebiete", "AI"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/matching/ergebnisse?bestandeneKurse=Compilerbau&interessensgebiete=AI"));
    }

    @Test
    @DisplayName("GET /matching/ergebnisse renders matching results")
    void results_view_renders_matches() throws Exception {
        Thema thema = new Thema(UUID.randomUUID(), "Thema A");
        Betreuer betreuer = new Betreuer(UUID.randomUUID(), "Anna");
        MatchingResult result = new MatchingResult(
                List.of(new ThemaMatch(thema, 2, Set.of("ai"), Set.of("compilerbau"))),
                List.of(new BetreuerMatch(betreuer, 1, Set.of("ai")))
        );
        when(matchingService.match(any())).thenReturn(result);

        mvc.perform(get("/matching/ergebnisse")
                        .param("bestandeneKurse", "Compilerbau")
                        .param("interessensgebiete", "AI")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(view().name("matching/results"))
                .andExpect(model().attributeExists("matchingForm"))
                .andExpect(model().attributeExists("themenMatches"))
                .andExpect(model().attributeExists("betreuerMatches"));
    }
}
