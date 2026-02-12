package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerRepository;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(BetreuerRepositoryImplementation.class)
@TestPropertySource(properties = "spring.flyway.enabled=true")
@ActiveProfiles("test")
class BetreuerRepositoryImplementationTest extends wiegand.luca.ThesisIsComing.TestPostgresContainer {

    @Autowired
    private BetreuerRepository betreuerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("save inserts a new Betreuer")
    void save_inserts_new_betreuer() {
        UUID id = UUID.randomUUID();
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Betreuer betreuer = new Betreuer(id, "Anna", kontakt, Set.of(), Set.of(), Set.of());

        Betreuer saved = betreuerRepository.save(betreuer);
        Integer count = jdbcTemplate.queryForObject("select count(*) from betreuer where id = ?", Integer.class, id);

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("save updates an existing Betreuer")
    void save_updates_existing_betreuer() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into betreuer (id, name, email, matrikelnummer) values (?, ?, ?, ?)",
                id, "Old", "old@b.de", "M-0"
        );
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Betreuer betreuer = new Betreuer(id, "Anna", kontakt, Set.of(), Set.of(), Set.of());

        Betreuer saved = betreuerRepository.save(betreuer);
        Optional<Betreuer> loaded = betreuerRepository.findById(id);

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("Anna");
    }
}
