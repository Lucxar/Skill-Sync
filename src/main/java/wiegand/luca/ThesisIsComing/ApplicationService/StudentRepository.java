package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.Optional;
import java.util.UUID;
import wiegand.luca.ThesisIsComing.domainModel.Student;

public interface StudentRepository {
  Optional<Student> findById(UUID id);

  Optional<Student> findByGithubLogin(String githubLogin);

  Iterable<Student> findAll();

  Student save(Student student);

  void deleteById(UUID id);
}
