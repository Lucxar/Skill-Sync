package wiegand.luca.ThesisIsComing.presentationDriving;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.roleAssignmentForm;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.roleRemovalForm;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.userRoleAssignmentForm;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.userRoleRemovalForm;

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
import wiegand.luca.ThesisIsComing.ApplicationService.RoleAssignmentService;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.presentationDriving.security.SecurityConfiguration;

@WebMvcTest(AdminRollenController.class)
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, TestOAuth2ClientConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.admin-logins=admin")
class AdminRollenControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private BetreuerService betreuerService;

  @MockitoBean private RoleAssignmentService roleAssignmentService;

  @Test
  @DisplayName("GET /admin/rollen is accessible for admins")
  void admin_roles_is_accessible_for_admins() throws Exception {
    when(betreuerService.listAll()).thenReturn(List.of(new Betreuer(UUID.randomUUID(), "Anna")));
    when(roleAssignmentService.listAll()).thenReturn(List.of());

    mvc.perform(
            get("/admin/rollen")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(view().name("admin/roles"))
        .andExpect(model().attributeExists("betreuer"));
  }

  @Test
  @DisplayName("GET /admin/rollen is forbidden for students")
  void admin_roles_forbidden_for_students() throws Exception {
    mvc.perform(
            get("/admin/rollen")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("GET /admin/rollen redirects to GitHub login when unauthenticated")
  void admin_roles_redirects_when_unauthenticated() throws Exception {
    mvc.perform(get("/admin/rollen"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/oauth2/authorization/github"));
  }

  @Test
  @DisplayName("POST /admin/rollen/zuweisen assigns role and redirects")
  void assign_role_redirects() throws Exception {
    UUID id = UUID.randomUUID();

    mvc.perform(
            post("/admin/rollen/zuweisen")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("assignForm", roleAssignmentForm(id, "anna-gh")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/rollen"));

    verify(betreuerService).assignBetreuerRole(id, "anna-gh");
  }

  @Test
  @DisplayName("POST /admin/rollen/entziehen removes role and redirects")
  void remove_role_redirects() throws Exception {
    UUID id = UUID.randomUUID();

    mvc.perform(
            post("/admin/rollen/entziehen")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("removeForm", roleRemovalForm(id)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/rollen"));

    verify(betreuerService).removeBetreuerRole(id);
  }

  @Test
  @DisplayName("POST /admin/rollen/zuweisen/benutzerrolle assigns user role and redirects")
  void assign_user_role_redirects() throws Exception {
    mvc.perform(
            post("/admin/rollen/zuweisen/benutzerrolle")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userRoleAssignForm", userRoleAssignmentForm("login", "ORGANISATOR")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/rollen"));

    verify(roleAssignmentService).assignRole("login", "ORGANISATOR");
  }

  @Test
  @DisplayName("POST /admin/rollen/entziehen/benutzerrolle removes user role and redirects")
  void remove_user_role_redirects() throws Exception {
    mvc.perform(
            post("/admin/rollen/entziehen/benutzerrolle")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("userRoleRemoveForm", userRoleRemovalForm("login", "ROLE_KORREKTOR")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/rollen"));

    verify(roleAssignmentService).removeRole("login", "ROLE_KORREKTOR");
  }
}
