package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import wiegand.luca.ThesisIsComing.ApplicationService.StudentRepository;
import wiegand.luca.ThesisIsComing.TestPostgresContainer;
import wiegand.luca.ThesisIsComing.domainModel.Student;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(StudentRepositoryImplementation.class)
@TestPropertySource(properties = "spring.flyway.enabled=true")
@ActiveProfiles("test")
class StudentRepositoryImplementationTest extends TestPostgresContainer {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("save inserts a new Student")
    void save_inserts_new_student() {
        UUID id = UUID.randomUUID();
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Student student = new Student(id, "Anna", kontakt, Set.of(), Set.of());

        Student saved = studentRepository.save(student);
        Integer count = jdbcTemplate.queryForObject("select count(*) from student where id = ?", Integer.class, id);

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("save updates an existing Student")
    void save_updates_existing_student() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into student (id, name, email, matrikelnummer) values (?, ?, ?, ?)",
                id, "Alt", "alt@b.de", "M-0"
        );
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Student student = new Student(id, "Anna", kontakt, Set.of(), Set.of());

        Student saved = studentRepository.save(student);
        Optional<Student> loaded = studentRepository.findById(id);

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("Anna");
    }

    @Test
    @DisplayName("save persists kurse and interessen for Student")
    void save_persists_kurse_and_interessen() {
        UUID id = UUID.randomUUID();
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Student student = new Student(
                id,
                "Anna",
                kontakt,
                Set.of(new BestandenerKurs("Algo", null, null)),
                Set.of(new Fachgebiet("AI"))
        );

        studentRepository.save(student);

        Integer kursCount = jdbcTemplate.queryForObject(
                "select count(*) from student_bestandener_kurs where student_id = ?",
                Integer.class,
                id
        );
        Integer fachgebietCount = jdbcTemplate.queryForObject(
                "select count(*) from student_fachgebiet where student_id = ?",
                Integer.class,
                id
        );

        assertThat(kursCount).isEqualTo(1);
        assertThat(fachgebietCount).isEqualTo(1);

        var kursRow = jdbcTemplate.queryForMap(
                "select kursbezeichnung from student_bestandener_kurs where student_id = ?",
                id
        );
        var fachgebietRow = jdbcTemplate.queryForMap(
                "select bezeichnung from student_fachgebiet where student_id = ?",
                id
        );
        assertThat(kursRow.get("kursbezeichnung")).isEqualTo("Algo");
        assertThat(fachgebietRow.get("bezeichnung")).isEqualTo("AI");
    }

    @Test
    @DisplayName("deleteById removes Student and linked data")
    void deleteById_removes_student_and_links() {
        UUID id = UUID.randomUUID();
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Student student = new Student(
                id,
                "Anna",
                kontakt,
                Set.of(new BestandenerKurs("Algo", null, null)),
                Set.of(new Fachgebiet("AI"))
        );
        studentRepository.save(student);

        studentRepository.deleteById(id);

        Integer studentCount = jdbcTemplate.queryForObject(
                "select count(*) from student where id = ?",
                Integer.class,
                id
        );
        Integer kursCount = jdbcTemplate.queryForObject(
                "select count(*) from student_bestandener_kurs where student_id = ?",
                Integer.class,
                id
        );
        Integer fachgebietCount = jdbcTemplate.queryForObject(
                "select count(*) from student_fachgebiet where student_id = ?",
                Integer.class,
                id
        );
        assertThat(studentCount).isEqualTo(0);
        assertThat(kursCount).isEqualTo(0);
        assertThat(fachgebietCount).isEqualTo(0);
    }

    @BeforeEach
    void clearTables() {
        jdbcTemplate.update("delete from student_bestandener_kurs");
        jdbcTemplate.update("delete from student_fachgebiet");
        jdbcTemplate.update("delete from student");
    }
}
