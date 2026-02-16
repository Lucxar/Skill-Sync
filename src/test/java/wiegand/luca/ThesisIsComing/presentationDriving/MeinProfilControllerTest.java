package wiegand.luca.ThesisIsComing.presentationDriving;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
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
import wiegand.luca.ThesisIsComing.ApplicationService.RoleAssignmentService;
import wiegand.luca.ThesisIsComing.ApplicationService.StudentService;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.presentationDriving.security.SecurityConfiguration;

@WebMvcTest(MeinProfilController.class)
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, TestOAuth2ClientConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.admin-logins=admin")
class MeinProfilControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private BetreuerService betreuerService;

  @MockitoBean private StudentService studentService;

  @MockitoBean private RoleAssignmentService roleAssignmentService;

  @MockitoBean private CurrentUser currentUser;

  @Test
  @DisplayName("/mein-profil redirects Betreuer to their profile")
  void betreuer_redirects_to_profile() throws Exception {
    UUID id = UUID.randomUUID();
    when(currentUser.login()).thenReturn("betreuer");
    when(currentUser.hasRole(SecurityRoles.ROLE_BETREUER)).thenReturn(true);
    when(currentUser.hasRole(SecurityRoles.ROLE_STUDIERENDE)).thenReturn(false);
    when(betreuerService.findByGithubLogin("betreuer"))
        .thenReturn(Optional.of(new Betreuer(id, "Anna")));

    mvc.perform(
            get("/mein-profil")
                .with(SecurityMockMvcRequestPostProcessors.user("betreuer").roles("BETREUER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/betreuer/" + id));
  }

  @Test
  @DisplayName("/mein-profil redirects Studierende to their profile when present")
  void student_redirects_to_profile_when_present() throws Exception {
    UUID id = UUID.randomUUID();
    when(currentUser.login()).thenReturn("student");
    when(currentUser.hasRole(SecurityRoles.ROLE_BETREUER)).thenReturn(false);
    when(currentUser.hasRole(SecurityRoles.ROLE_STUDIERENDE)).thenReturn(true);
    when(studentService.findByGithubLogin("student"))
        .thenReturn(Optional.of(new Student(id, "Tom")));

    mvc.perform(
            get("/mein-profil")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/studierende/" + id));
  }

  @Test
  @DisplayName("/mein-profil redirects Studierende to create form when missing")
  void student_redirects_to_create_when_missing() throws Exception {
    when(currentUser.login()).thenReturn("student");
    when(currentUser.hasRole(SecurityRoles.ROLE_BETREUER)).thenReturn(false);
    when(currentUser.hasRole(SecurityRoles.ROLE_STUDIERENDE)).thenReturn(true);
    when(studentService.findByGithubLogin("student")).thenReturn(Optional.empty());

    mvc.perform(
            get("/mein-profil")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/studierende/neu"));
  }
}
