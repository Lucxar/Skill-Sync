package wiegand.luca.ThesisIsComing.presentationDriving;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.presentationDriving.security.SecurityConfiguration;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminRollenController.class)
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, TestOAuth2ClientConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.admin-logins=admin")
class AdminRollenControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BetreuerService betreuerService;

    @Test
    @DisplayName("GET /admin/rollen is accessible for admins")
    void admin_roles_is_accessible_for_admins() throws Exception {
        when(betreuerService.listAll()).thenReturn(List.of(new Betreuer(UUID.randomUUID(), "Anna")));

        mvc.perform(get("/admin/rollen")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/roles"))
                .andExpect(model().attributeExists("betreuer"));
    }

    @Test
    @DisplayName("GET /admin/rollen is forbidden for students")
    void admin_roles_forbidden_for_students() throws Exception {
        mvc.perform(get("/admin/rollen")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /admin/rollen redirects to GitHub login when unauthenticated")
    void admin_roles_redirects_when_unauthenticated() throws Exception {
        mvc.perform(get("/admin/rollen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/github"));
    }

    @Test
    @DisplayName("POST /admin/rollen/zuweisen assigns role and redirects")
    void assign_role_redirects() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(post("/admin/rollen/zuweisen")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("betreuerId", id.toString())
                        .param("githubLogin", "anna-gh"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/rollen"));

        verify(betreuerService).assignBetreuerRole(id, "anna-gh");
    }

    @Test
    @DisplayName("POST /admin/rollen/entziehen removes role and redirects")
    void remove_role_redirects() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(post("/admin/rollen/entziehen")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("betreuerId", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/rollen"));

        verify(betreuerService).removeBetreuerRole(id);
    }
}
