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
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaRepository;
import wiegand.luca.ThesisIsComing.TestPostgresContainer;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ThemaRepositoryImplementation.class)
@TestPropertySource(properties = "spring.flyway.enabled=true")
@ActiveProfiles("test")
public class ThemaRepositoryImplementationTest extends TestPostgresContainer {

    @Autowired
    private ThemaRepository themaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;




    @Test
    @DisplayName("save inserts a new Thema")
    void save_inserts_new_Thema() {
        UUID id = UUID.randomUUID();
        Thema thema = new Thema(id, "Titel 1");

        Thema savedThema = themaRepository.save(thema);
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from thema where id = ?",
                Integer.class,
                id
        );

        assertThat(savedThema.getId()).isEqualTo(id);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("save updates an existing Thema")
    void save_updates_existing_thema() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
                id, "Alt", null
        );

        Thema thema = new Thema(id, "Neu");

        Thema saved = themaRepository.save(thema);
        Optional<Thema> loaded = themaRepository.findById(id);

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getTitel()).isEqualTo("Neu");
    }

    @Test
    @DisplayName("findByTitel returns the correct Thema")
    void findByTitel_returns_correct_thema() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
                id, "Titel X", null
        );

        Optional<Thema> loaded = themaRepository.findByTitel("Titel X");

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("findAll returns all Themen")
    void findAll_returns_all_themen() {

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
                id1, "Titel 1", null
        );
        jdbcTemplate.update(
                "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
                id2, "Titel 2", null
        );

        List<Thema> result = StreamSupport.stream(themaRepository.findAll().spliterator(), false)
                .toList();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("relation queries return Betreuer for a Thema")
    void relation_queries_return_betreuer_for_thema() {
        UUID themaId = UUID.randomUUID();
        UUID betreuerId1 = UUID.randomUUID();
        UUID betreuerId2 = UUID.randomUUID();

        jdbcTemplate.update(
                "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
                themaId, "Thema X", null
        );
        jdbcTemplate.update(
                "insert into betreuer (id, name, email, matrikelnummer) values (?, ?, ?, ?)",
                betreuerId1, "Anna", null, null
        );
        jdbcTemplate.update(
                "insert into betreuer (id, name, email, matrikelnummer) values (?, ?, ?, ?)",
                betreuerId2, "Bernd", null, null
        );
        jdbcTemplate.update(
                "insert into betreuer_thema (betreuer_id, thema_id) values (?, ?)",
                betreuerId1, themaId
        );
        jdbcTemplate.update(
                "insert into betreuer_thema (betreuer_id, thema_id) values (?, ?)",
                betreuerId2, themaId
        );

        assertThat(themaRepository.findBetreuerIdsByThemaId(themaId))
                .containsExactlyInAnyOrder(betreuerId1, betreuerId2);

        assertThat(themaRepository.findBetreuerByThemaIdOrderByName(themaId))
                .extracting(Betreuer::getName)
                .containsExactly("Anna", "Bernd");
    }



    @BeforeEach
    public void clearThemaTables() {
        jdbcTemplate.update("delete from betreuer_thema");
        jdbcTemplate.update("delete from betreuer");
        jdbcTemplate.update("delete from thema_link");
        jdbcTemplate.update("delete from thema_informationsdatei");
        jdbcTemplate.update("delete from thema_fachgebiet");
        jdbcTemplate.update("delete from thema_voraussetzung");
        jdbcTemplate.update("delete from thema");
    }

}
