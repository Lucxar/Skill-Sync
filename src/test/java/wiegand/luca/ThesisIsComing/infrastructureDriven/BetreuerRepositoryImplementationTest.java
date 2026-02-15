package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerRepository;
import wiegand.luca.ThesisIsComing.TestPostgresContainer;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(BetreuerRepositoryImplementation.class)
@TestPropertySource(properties = "spring.flyway.enabled=true")
@ActiveProfiles("test")
class BetreuerRepositoryImplementationTest extends TestPostgresContainer {

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

    @Test
    @DisplayName("save persists links for Betreuer")
    void save_persists_links() {
        UUID id = UUID.randomUUID();
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Betreuer betreuer = new Betreuer(
                id,
                "Anna",
                kontakt,
                Set.of(),
                Set.of(),
                Set.of(new Link("https://example.com", "Beispiel"))
        );

        betreuerRepository.save(betreuer);

        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from betreuer_link where betreuer_id = ?",
                Integer.class,
                id
        );
        assertThat(count).isEqualTo(1);

        var row = jdbcTemplate.queryForMap(
                "select url, anzeigetext from betreuer_link where betreuer_id = ?",
                id
        );
        assertThat(row.get("url")).isEqualTo("https://example.com");
        assertThat(row.get("anzeigetext")).isEqualTo("Beispiel");
    }

    @Test
    @DisplayName("save persists informationsdatei metadata for Betreuer")
    void save_persists_informationsdatei_metadata() {
        UUID id = UUID.randomUUID();
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Informationsdatei datei = new Informationsdatei(
                "Info",
                "Beschreibung",
                "anna",
                LocalDateTime.now(),
                "stored.pdf",
                Dateityp.PDF,
                "info.pdf",
                123L
        );
        Betreuer betreuer = new Betreuer(id, "Anna", kontakt, Set.of(), Set.of(datei), Set.of());

        betreuerRepository.save(betreuer);

        var row = jdbcTemplate.queryForMap(
                "select dateityp, original_dateiname, groesse_in_bytes from betreuer_informationsdatei where betreuer_id = ?",
                id
        );
        assertThat(row.get("dateityp").toString()).isEqualTo("PDF");
        assertThat(row.get("original_dateiname")).isEqualTo("info.pdf");
        assertThat(((Number) row.get("groesse_in_bytes")).longValue()).isEqualTo(123L);
    }

    @Test
    @DisplayName("relation queries return Themen for a Betreuer")
    void relation_queries_return_themen_for_betreuer() {
        UUID betreuerId = UUID.randomUUID();
        UUID themaId1 = UUID.randomUUID();
        UUID themaId2 = UUID.randomUUID();

        jdbcTemplate.update(
                "insert into betreuer (id, name, email, matrikelnummer) values (?, ?, ?, ?)",
                betreuerId, "Anna", null, null
        );
        jdbcTemplate.update(
                "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
                themaId1, "A", null
        );
        jdbcTemplate.update(
                "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
                themaId2, "B", null
        );
        jdbcTemplate.update(
                "insert into betreuer_thema (betreuer_id, thema_id) values (?, ?)",
                betreuerId, themaId1
        );
        jdbcTemplate.update(
                "insert into betreuer_thema (betreuer_id, thema_id) values (?, ?)",
                betreuerId, themaId2
        );

        assertThat(betreuerRepository.findThemaIdsByBetreuerId(betreuerId))
                .containsExactlyInAnyOrder(themaId1, themaId2);

        assertThat(betreuerRepository.findThemenByBetreuerIdOrderByTitel(betreuerId))
                .extracting(Thema::getTitel)
                .containsExactly("A", "B");

        assertThat(betreuerRepository.countThemenByBetreuerId(betreuerId)).isEqualTo(2);
        assertThat(betreuerRepository.existsBetreuerThema(betreuerId, themaId1)).isTrue();
        assertThat(betreuerRepository.existsBetreuerThema(betreuerId, UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("deleteById removes Betreuer and linked data")
    void deleteById_removes_betreuer_and_links() {
        UUID id = UUID.randomUUID();
        Kontaktinformationen kontakt = new Kontaktinformationen("a@b.de", "M-1");
        Betreuer betreuer = new Betreuer(
                id,
                "Anna",
                kontakt,
                Set.of(),
                Set.of(),
                Set.of(new Link("https://example.com", "Beispiel"))
        );
        betreuerRepository.save(betreuer);

        betreuerRepository.deleteById(id);

        Integer betreuerCount = jdbcTemplate.queryForObject(
                "select count(*) from betreuer where id = ?",
                Integer.class,
                id
        );
        Integer linkCount = jdbcTemplate.queryForObject(
                "select count(*) from betreuer_link where betreuer_id = ?",
                Integer.class,
                id
        );
        assertThat(betreuerCount).isEqualTo(0);
        assertThat(linkCount).isEqualTo(0);
    }

    @Test
    @DisplayName("findByGithubLogin returns Betreuer when login matches")
    void findByGithubLogin_returns_betreuer() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into betreuer (id, name, email, matrikelnummer, github_login) values (?, ?, ?, ?, ?)",
                id, "Anna", "anna@uni.de", "M-1", "anna-gh"
        );

        Optional<Betreuer> result = betreuerRepository.findByGithubLogin("anna-gh");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("betreuer_thema enforces foreign keys")
    void betreuer_thema_enforces_foreign_keys() {
        assertThatThrownBy(() -> jdbcTemplate.update(
                "insert into betreuer_thema (betreuer_id, thema_id) values (?, ?)",
                UUID.randomUUID(), UUID.randomUUID()
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @BeforeEach
    void clearTables() {
        jdbcTemplate.update("delete from betreuer_link");
        jdbcTemplate.update("delete from betreuer_informationsdatei");
        jdbcTemplate.update("delete from betreuer_fachgebiet");
        jdbcTemplate.update("delete from betreuer_thema");
        jdbcTemplate.update("delete from betreuer");
        jdbcTemplate.update("delete from thema");
    }
}
