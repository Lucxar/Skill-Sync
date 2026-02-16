package wiegand.luca.ThesisIsComing.presentationDriving;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import wiegand.luca.ThesisIsComing.ApplicationService.DateiSpeicher;
import wiegand.luca.ThesisIsComing.ApplicationService.MatchingService;
import wiegand.luca.ThesisIsComing.ApplicationService.RoleAssignmentService;
import wiegand.luca.ThesisIsComing.ApplicationService.StudentService;
import wiegand.luca.ThesisIsComing.ApplicationService.SucheForm;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.presentationDriving.security.SecurityConfiguration;

@WebMvcTest(
    controllers = {
      BetreuerController.class,
      ThemaController.class,
      StudentController.class,
      MatchingController.class,
      AdminRollenController.class,
      DateiController.class,
      MeinProfilController.class
    })
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, TestOAuth2ClientConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.admin-logins=admin")
class SecurityRoleMatrixTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private BetreuerService betreuerService;

  @MockitoBean private BetreuerThemenBeziehungService beziehungService;

  @MockitoBean private ThemaService themaService;

  @MockitoBean private StudentService studentService;

  @MockitoBean private MatchingService matchingService;

  @MockitoBean private DateiSpeicher dateiSpeicher;

  @MockitoBean private RoleAssignmentService roleAssignmentService;

  @MockitoBean private CurrentUser currentUser;

  @BeforeEach
  void setupDefaults() {
    when(betreuerService.searchBetreuer(any(SucheForm.class)))
        .thenReturn(List.of(new Betreuer(UUID.randomUUID(), "Anna")));
    when(betreuerService.listAll()).thenReturn(List.of(new Betreuer(UUID.randomUUID(), "Anna")));
    when(themaService.searchThemen(any(SucheForm.class)))
        .thenReturn(List.of(new Thema(UUID.randomUUID(), "Thema A")));
    when(studentService.listAll()).thenReturn(List.of(new Student(UUID.randomUUID(), "Tom")));
    when(roleAssignmentService.listAll()).thenReturn(List.of());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"/betreuer", "/themen", "/studierende", "/matching", "/admin/rollen", "/mein-profil"})
  @DisplayName("Unauthenticated users are redirected to GitHub login")
  void unauthenticated_users_are_redirected(String path) throws Exception {
    mvc.perform(get(path))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/oauth2/authorization/github"));
  }

  @Test
  @DisplayName("Studierende can access read pages but not admin")
  void studierende_can_access_read_pages_but_not_admin() throws Exception {
    mvc.perform(
            get("/betreuer")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/themen")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/studierende")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/matching")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/admin/rollen")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Betreuer can access betreuer/thema forms but not student or admin")
  void betreuer_can_access_betreuer_and_thema_forms_but_not_student_or_admin() throws Exception {
    mvc.perform(
            get("/betreuer/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("betreuer").roles("BETREUER")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/themen/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("betreuer").roles("BETREUER")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/studierende/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("betreuer").roles("BETREUER")))
        .andExpect(status().isForbidden());

    mvc.perform(
            get("/admin/rollen")
                .with(SecurityMockMvcRequestPostProcessors.user("betreuer").roles("BETREUER")))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Admin can access admin and all create forms")
  void admin_can_access_admin_and_all_create_forms() throws Exception {
    mvc.perform(
            get("/admin/rollen")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/betreuer/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/themen/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk());

    mvc.perform(
            get("/studierende/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk());
  }
}
