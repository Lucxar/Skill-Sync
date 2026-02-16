package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.OptimisticLockingException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.StudentNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.StudentValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ZuordnungNichtErlaubtException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;

@Service
public class StudentService {

  private final StudentRepository repository;
  private final CurrentUser currentUser;

  public StudentService(StudentRepository repository, CurrentUser currentUser) {
    this.repository = repository;
    this.currentUser = currentUser;
  }

  public List<Student> listAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
  }

  public Student getById(UUID id) {
    return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
  }

  public java.util.Optional<Student> findByGithubLogin(String githubLogin) {
    String normalized = normalizeGithubLogin(githubLogin);
    if (normalized == null) {
      return java.util.Optional.empty();
    }
    return repository.findByGithubLogin(normalized);
  }

  public Student save(Student student) {
    return repository.save(student);
  }

  public Student createStudent(
      String name,
      String email,
      String matrikelnummer,
      Set<BestandenerKurs> bestandeneKurse,
      Set<Fachgebiet> interessensgebiete) {
    try {
      Kontaktinformationen kontaktinformationen = new Kontaktinformationen(email, matrikelnummer);
      Student student =
          new Student(
              UUID.randomUUID(),
              name,
              currentUser.login(),
              kontaktinformationen,
              bestandeneKurse,
              interessensgebiete);
      return repository.save(student);
    } catch (IllegalArgumentException ex) {
      throw new StudentValidationException(ex.getMessage());
    }
  }

  public Student updateStudent(
      UUID id,
      String name,
      String email,
      String matrikelnummer,
      Set<BestandenerKurs> bestandeneKurse,
      Set<Fachgebiet> interessensgebiete,
      Long expectedVersion) {
    assertStudentOwner(id);
    Student student = getById(id);
    assertVersionMatches(student.getVersion(), expectedVersion);
    try {
      student.updateDetails(name, email, matrikelnummer);
      student.replaceVoraussetzungen(bestandeneKurse, interessensgebiete);
      return saveWithOptimisticHandling(student);
    } catch (IllegalArgumentException ex) {
      throw new StudentValidationException(ex.getMessage());
    }
  }

  public void deleteStudent(UUID id) {
    assertStudentOwner(id);
    getById(id);
    repository.deleteById(id);
  }

  private void assertStudentOwner(UUID studentId) {
    if (currentUser.hasRole(SecurityRoles.ROLE_ADMIN)) {
      return;
    }
    if (!currentUser.hasRole(SecurityRoles.ROLE_STUDIERENDE)) {
      throw new ZuordnungNichtErlaubtException("Keine Berechtigung fuer Studierendenprofil.");
    }
    String login = currentUser.login();
    if (login == null || login.isBlank()) {
      throw new ZuordnungNichtErlaubtException("Kein Studierenden-Login vorhanden.");
    }
    Student student = getById(studentId);
    String studentLogin = student.getGithubLogin();
    if (studentLogin == null || studentLogin.isBlank()) {
      throw new ZuordnungNichtErlaubtException("Studierendenprofil ist keinem Login zugeordnet.");
    }
    if (!studentLogin.equals(login)) {
      throw new ZuordnungNichtErlaubtException(
          "Studierende duerfen nur ihr eigenes Profil bearbeiten.");
    }
  }

  private Student saveWithOptimisticHandling(Student student) {
    try {
      return repository.save(student);
    } catch (OptimisticLockingFailureException ex) {
      throw new OptimisticLockingException(
          "Datensatz wurde zwischenzeitlich geaendert. Bitte Seite neu laden.");
    }
  }

  private static void assertVersionMatches(Long current, Long expected) {
    if (current == null || expected == null || !current.equals(expected)) {
      throw new OptimisticLockingException(
          "Datensatz wurde zwischenzeitlich geaendert. Bitte Seite neu laden.");
    }
  }

  private static String normalizeGithubLogin(String githubLogin) {
    if (githubLogin == null) {
      return null;
    }
    String trimmed = githubLogin.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
