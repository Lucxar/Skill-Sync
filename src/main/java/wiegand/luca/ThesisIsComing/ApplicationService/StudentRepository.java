package wiegand.luca.ThesisIsComing.ApplicationService;

import wiegand.luca.ThesisIsComing.domainModel.Student;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository {
    Optional<Student> findById(UUID id);

    Iterable<Student> findAll();

    Student save(Student student);

    void deleteById(UUID id);
}
