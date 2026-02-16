package wiegand.luca.ThesisIsComing.presentationDriving;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerThemenBeziehungService;
import wiegand.luca.ThesisIsComing.ApplicationService.RoleAssignmentService;
import wiegand.luca.ThesisIsComing.ApplicationService.SucheForm;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.presentationDriving.security.SecurityConfiguration;

@WebMvcTest(BetreuerController.class)
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, TestOAuth2ClientConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.admin-logins=admin")
class SecurityAccessTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private BetreuerService betreuerService;

  @MockitoBean private BetreuerThemenBeziehungService beziehungService;

  @MockitoBean private ThemaService themaService;

  @MockitoBean private RoleAssignmentService roleAssignmentService;

  @Test
  @DisplayName("Unauthenticated users are redirected to GitHub login")
  void unauthenticated_users_redirected_to_login() throws Exception {
    mvc.perform(get("/betreuer"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/oauth2/authorization/github"));
  }

  @Test
  @DisplayName("Studierende can access read-only Betreuer list")
  void studierende_can_access_betreuer_list() throws Exception {
    when(betreuerService.searchBetreuer(any(SucheForm.class)))
        .thenReturn(List.of(new Betreuer(UUID.randomUUID(), "Anna")));

    mvc.perform(
            get("/betreuer")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Betreuer can access Betreuer create form")
  void betreuer_can_access_create_form() throws Exception {
    mvc.perform(
            get("/betreuer/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("betreuer").roles("BETREUER")))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Studierende cannot access Betreuer create form")
  void studierende_cannot_access_create_form() throws Exception {
    mvc.perform(
            get("/betreuer/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isForbidden());
  }
}
