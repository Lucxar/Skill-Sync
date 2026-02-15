package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.ApplicationService.StudentRepository;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.StudentDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StudentRepositoryImplementation implements StudentRepository {

    private final StudentDataRepository dataRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    public StudentRepositoryImplementation(StudentDataRepository dataRepository,
                                           JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.dataRepository = dataRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    @Override
    public Optional<Student> findById(UUID id) {
        return dataRepository.findById(id).map(Mapper::toStudent);
    }

    @Override
    public Iterable<Student> findAll() {
        List<Student> result = new ArrayList<>();
        for (StudentDto dto : dataRepository.findAll()) {
            result.add(Mapper.toStudent(dto));
        }
        return result;
    }

    @Override
    public Student save(Student student) {
        StudentDto dto = Mapper.toStudentDto(student);
        StudentDto saved = (dto.id() != null && dataRepository.existsById(dto.id()))
                ? jdbcAggregateTemplate.update(dto)
                : jdbcAggregateTemplate.insert(dto);
        return Mapper.toStudent(saved);
    }

    @Override
    public void deleteById(UUID id) {
        dataRepository.deleteById(id);
    }
}
