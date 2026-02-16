package wiegand.luca.ThesisIsComing.ApplicationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.StudentNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.StudentValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.OptimisticLockingException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudentServiceTest {

  @Mock private StudentRepository repo;

  @Mock private CurrentUser currentUser;

  private StudentService service;

  @BeforeEach
  void setup() {
    service = new StudentService(repo, currentUser);
    lenient().when(currentUser.hasRole(SecurityRoles.ROLE_ADMIN)).thenReturn(true);
    lenient().when(currentUser.login()).thenReturn("student");
  }

  @Test
  void listAll_returns_all_students() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Student s1 = new Student(id1, "Anna");
    Student s2 = new Student(id2, "Max");
    when(repo.findAll()).thenReturn(List.of(s1, s2));

    List<Student> result = service.listAll();

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(s1, s2);
    verify(repo).findAll();
  }

  @Test
  void getById_throws_when_missing() {
    when(repo.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getById(UUID.randomUUID()))
        .isInstanceOf(StudentNotFoundException.class);
  }

  @Test
  void findByGithubLogin_returns_student_for_login() {
    Student student = new Student(UUID.randomUUID(), "Anna");
    when(repo.findByGithubLogin("student")).thenReturn(Optional.of(student));

    Optional<Student> result = service.findByGithubLogin(" student ");

    assertThat(result).contains(student);
  }

  @Test
  void findByGithubLogin_returns_empty_when_blank() {
    Optional<Student> result = service.findByGithubLogin("  ");

    assertThat(result).isEmpty();
    verify(repo, never()).findByGithubLogin(any(String.class));
  }

  @Test
  void createStudent_saves_and_returns_created_student() {
    when(repo.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));
    Set<BestandenerKurs> kurse = Set.of(new BestandenerKurs("Algo", null, null));
    Set<Fachgebiet> interessen = Set.of(new Fachgebiet("AI"));

    Student created = service.createStudent("Anna", "anna@uni.de", "M-1", kurse, interessen);

    ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
    verify(repo).save(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("Anna");
    assertThat(created.getName()).isEqualTo("Anna");
  }

  @Test
  void createStudent_throws_when_name_blank() {
    assertThatThrownBy(() -> service.createStudent(" ", null, null, Set.of(), Set.of()))
        .isInstanceOf(StudentValidationException.class);
    verify(repo, never()).save(any(Student.class));
  }

  @Test
  void updateStudent_updates_details_and_voraussetzungen() {
    UUID id = UUID.randomUUID();
    Student student = new Student(id, "Alt");
    when(repo.findById(id)).thenReturn(Optional.of(student));
    when(repo.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Set<BestandenerKurs> kurse = Set.of(new BestandenerKurs("DB", null, null));
    Set<Fachgebiet> interessen = Set.of(new Fachgebiet("Data"));

    Student updated =
        service.updateStudent(
            id, "Neu", "neu@uni.de", "M-2", kurse, interessen, student.getVersion());

    assertThat(updated.getName()).isEqualTo("Neu");
    assertThat(updated.getKontaktinformationen().getEmail()).isEqualTo("neu@uni.de");
    assertThat(updated.getBestandeneKurse())
        .extracting(BestandenerKurs::getKursbezeichnung)
        .containsExactly("DB");
    assertThat(updated.getInteressensgebiete())
        .extracting(Fachgebiet::getBezeichnung)
        .containsExactly("Data");
  }

  @Test
  void updateStudent_throws_when_name_blank() {
    UUID id = UUID.randomUUID();
    Student student = new Student(id, "Alt");
    when(repo.findById(id)).thenReturn(Optional.of(student));

    assertThatThrownBy(
            () -> service.updateStudent(id, " ", null, null, Set.of(), Set.of(), student.getVersion()))
        .isInstanceOf(StudentValidationException.class);
    verify(repo, never()).save(any(Student.class));
  }

  @Test
  void updateStudent_throws_when_version_mismatch() {
    UUID id = UUID.randomUUID();
    Student student = new Student(id, "Alt");
    when(repo.findById(id)).thenReturn(Optional.of(student));

    assertThatThrownBy(
            () -> service.updateStudent(id, "Neu", null, null, Set.of(), Set.of(), 4L))
        .isInstanceOf(OptimisticLockingException.class);
    verify(repo, never()).save(any(Student.class));
  }

  @Test
  void deleteStudent_deletes_when_found() {
    UUID id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.of(new Student(id, "Anna")));

    service.deleteStudent(id);

    verify(repo).deleteById(id);
  }
}
