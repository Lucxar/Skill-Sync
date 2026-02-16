package wiegand.luca.ThesisIsComing.presentationDriving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static wiegand.luca.ThesisIsComing.testdata.FormTestData.studentForm;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wiegand.luca.ThesisIsComing.ApplicationService.StudentService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.StudentNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Student;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration.class)
@ActiveProfiles("test")
class StudentControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private StudentService service;

  @Test
  @DisplayName("'/studierende' renders list view")
  void list_view_renders_students() throws Exception {
    Student student = new Student(UUID.randomUUID(), "Anna");
    when(service.listAll()).thenReturn(List.of(student));

    mvc.perform(
            get("/studierende")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isOk())
        .andExpect(view().name("student/list"))
        .andExpect(model().attributeExists("students"));
  }

  @Test
  @DisplayName("'/studierende/{id}' renders detail view")
  void detail_view_renders_student() throws Exception {
    UUID id = UUID.randomUUID();
    Student student = new Student(id, "Tom");
    when(service.getById(id)).thenReturn(student);

    var result =
        mvc.perform(
                get("/studierende/{id}", id)
                    .with(
                        SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
            .andExpect(status().isOk())
            .andExpect(view().name("student/detail"))
            .andExpect(model().attributeExists("student"))
            .andReturn();

    var doc = org.jsoup.Jsoup.parse(result.getResponse().getContentAsString());
    assertThat(doc.text()).contains("Tom");
  }

  @Test
  @DisplayName("'/studierende/neu' returns form view")
  void create_form_returns_view() throws Exception {
    mvc.perform(
            get("/studierende/neu")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(view().name("student/form"))
        .andExpect(model().attributeExists("studentForm"));
  }

  @Test
  @DisplayName("POST /studierende creates student and redirects")
  void create_student_redirects() throws Exception {
    UUID id = UUID.randomUUID();
    when(service.createStudent(
            any(String.class), any(String.class), any(String.class), any(), any()))
        .thenReturn(new Student(id, "Anna"));

    mvc.perform(
            post("/studierende")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("studentForm", studentForm()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/studierende/" + id));
  }

  @Test
  @DisplayName("POST /studierende returns form errors for blank name")
  void create_student_returns_errors_on_blank_name() throws Exception {
    mvc.perform(
            post("/studierende")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("studentForm", studentForm(" ", null, null, "", "")))
        .andExpect(status().isOk())
        .andExpect(view().name("student/form"))
        .andExpect(model().attributeHasFieldErrors("studentForm", "name"));
  }

  @Test
  @DisplayName("/studierende/{id}/bearbeiten returns form view")
  void edit_form_returns_view() throws Exception {
    UUID id = UUID.randomUUID();
    when(service.getById(id)).thenReturn(new Student(id, "Anna"));

    mvc.perform(
            get("/studierende/{id}/bearbeiten", id)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(view().name("student/form"))
        .andExpect(model().attributeExists("studentForm"));
  }

  @Test
  @DisplayName("POST /studierende/{id}/bearbeiten updates and redirects")
  void update_student_redirects() throws Exception {
    UUID id = UUID.randomUUID();
    when(service.updateStudent(
            any(UUID.class),
            any(String.class),
            any(String.class),
            any(String.class),
            any(),
            any(),
            any(Long.class)))
        .thenReturn(new Student(id, "Anna"));

    mvc.perform(
            post("/studierende/{id}/bearbeiten", id)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("studentForm", studentForm("Anna", "", "", "", "")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/studierende/" + id));
  }

  @Test
  @DisplayName("POST /studierende/{id}/loeschen deletes and redirects")
  void delete_student_redirects() throws Exception {
    UUID id = UUID.randomUUID();

    mvc.perform(
            post("/studierende/{id}/loeschen", id)
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/studierende"));

    verify(service).deleteStudent(id);
  }

  @Test
  @DisplayName("POST /studierende is forbidden for betreuer")
  void create_student_forbidden_for_betreuer() throws Exception {
    mvc.perform(
            post("/studierende")
                .with(SecurityMockMvcRequestPostProcessors.user("betreuer").roles("BETREUER"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .flashAttr("studentForm", studentForm("Anna", null, null, "", "")))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("/studierende returns 404 when list is empty")
  void list_returns_404_when_empty() throws Exception {
    when(service.listAll()).thenReturn(List.of());

    mvc.perform(
            get("/studierende")
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isNotFound())
        .andExpect(view().name("errors/404"))
        .andExpect(model().attributeExists("message"));
  }

  @Test
  @DisplayName("/studierende/{id} returns 404 when student not found")
  void detail_returns_404_when_not_found() throws Exception {
    when(service.getById(any(UUID.class)))
        .thenThrow(new StudentNotFoundException(UUID.randomUUID()));

    mvc.perform(
            get("/studierende/{id}", UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
        .andExpect(status().isNotFound())
        .andExpect(view().name("errors/404"))
        .andExpect(model().attributeExists("message"));
  }
}
