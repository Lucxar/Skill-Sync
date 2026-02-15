package wiegand.luca.ThesisIsComing.ApplicationService;

import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.StudentNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.StudentValidationException;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> listAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .toList();
    }

    public Student getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student save(Student student) {
        return repository.save(student);
    }

    public Student createStudent(String name,
                                 String email,
                                 String matrikelnummer,
                                 Set<BestandenerKurs> bestandeneKurse,
                                 Set<Fachgebiet> interessensgebiete) {
        try {
            Kontaktinformationen kontaktinformationen = new Kontaktinformationen(email, matrikelnummer);
            Student student = new Student(UUID.randomUUID(), name, kontaktinformationen, bestandeneKurse, interessensgebiete);
            return repository.save(student);
        } catch (IllegalArgumentException ex) {
            throw new StudentValidationException(ex.getMessage());
        }
    }

    public Student updateStudent(UUID id,
                                 String name,
                                 String email,
                                 String matrikelnummer,
                                 Set<BestandenerKurs> bestandeneKurse,
                                 Set<Fachgebiet> interessensgebiete) {
        Student student = getById(id);
        try {
            student.updateDetails(name, email, matrikelnummer);
            student.replaceVoraussetzungen(bestandeneKurse, interessensgebiete);
            return repository.save(student);
        } catch (IllegalArgumentException ex) {
            throw new StudentValidationException(ex.getMessage());
        }
    }

    public void deleteStudent(UUID id) {
        getById(id);
        repository.deleteById(id);
    }
}
