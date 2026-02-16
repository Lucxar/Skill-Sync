package wiegand.luca.ThesisIsComing.infrastructureDriven;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;
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
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ThemaRepositoryImplementation.class)
@TestPropertySource(properties = "spring.flyway.enabled=true")
@ActiveProfiles("test")
public class ThemaRepositoryImplementationTest extends TestPostgresContainer {

  @Autowired private ThemaRepository themaRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("save inserts a new Thema")
  void save_inserts_new_Thema() {
    UUID id = UUID.randomUUID();
    Thema thema = new Thema(id, "Titel 1");

    Thema savedThema = themaRepository.save(thema);
    Integer count =
        jdbcTemplate.queryForObject("select count(*) from thema where id = ?", Integer.class, id);

    assertThat(savedThema.getId()).isEqualTo(id);
    assertThat(count).isEqualTo(1);
  }

  @Test
  @DisplayName("save updates an existing Thema")
  void save_updates_existing_thema() {
    UUID id = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)", id, "Alt", null);

    Thema thema = new Thema(id, "Neu");

    Thema saved = themaRepository.save(thema);
    Optional<Thema> loaded = themaRepository.findById(id);

    assertThat(saved.getId()).isEqualTo(id);
    assertThat(loaded).isPresent();
    assertThat(loaded.get().getTitel()).isEqualTo("Neu");
  }

  @Test
  @DisplayName("save persists links for Thema")
  void save_persists_links() {
    UUID id = UUID.randomUUID();
    Thema thema =
        new Thema(
            id,
            "Titel 1",
            null,
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(new Link("https://example.com", "Beispiel")),
            Set.of());

    themaRepository.save(thema);

    Integer count =
        jdbcTemplate.queryForObject(
            "select count(*) from thema_link where thema_id = ?", Integer.class, id);
    assertThat(count).isEqualTo(1);

    var row =
        jdbcTemplate.queryForMap("select url, anzeigetext from thema_link where thema_id = ?", id);
    assertThat(row.get("url")).isEqualTo("https://example.com");
    assertThat(row.get("anzeigetext")).isEqualTo("Beispiel");
  }

  @Test
  @DisplayName("save persists voraussetzungen, fachgebiete, and dateien for Thema")
  void save_persists_voraussetzungen_fachgebiete_and_dateien() {
    UUID id = UUID.randomUUID();
    Informationsdatei datei =
        new Informationsdatei(
            "Info",
            "Beschreibung",
            "anna",
            LocalDateTime.now(),
            "stored.pdf",
            Dateityp.PDF,
            "info.pdf",
            5L);
    Thema thema =
        new Thema(
            id,
            "Titel 1",
            null,
            Set.of(new Kurs("Algo", new Fachgebiet("AI"))),
            Set.of(new Fachgebiet("AI")),
            Set.of(datei),
            Set.of(),
            Set.of());

    themaRepository.save(thema);

    Integer kursCount =
        jdbcTemplate.queryForObject(
            "select count(*) from thema_voraussetzung where thema_id = ?", Integer.class, id);
    Integer fachgebietCount =
        jdbcTemplate.queryForObject(
            "select count(*) from thema_fachgebiet where thema_id = ?", Integer.class, id);
    Integer dateiCount =
        jdbcTemplate.queryForObject(
            "select count(*) from thema_informationsdatei where thema_id = ?", Integer.class, id);
    assertThat(kursCount).isEqualTo(1);
    assertThat(fachgebietCount).isEqualTo(1);
    assertThat(dateiCount).isEqualTo(1);

    var kursRow =
        jdbcTemplate.queryForMap(
            "select kursbezeichnung, fachgebiet_bezeichnung from thema_voraussetzung where thema_id = ?",
            id);
    assertThat(kursRow.get("kursbezeichnung")).isEqualTo("Algo");
    assertThat(kursRow.get("fachgebiet_bezeichnung")).isEqualTo("AI");

    var dateiRow =
        jdbcTemplate.queryForMap(
            "select titel, dateisystem_pfad from thema_informationsdatei where thema_id = ?", id);
    assertThat(dateiRow.get("titel")).isEqualTo("Info");
    assertThat(dateiRow.get("dateisystem_pfad")).isEqualTo("stored.pdf");
  }

  @Test
  @DisplayName("save updates links for Thema")
  void save_updates_links_for_thema() {
    UUID id = UUID.randomUUID();
    Thema thema =
        new Thema(
            id,
            "Titel 1",
            null,
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(new Link("https://alt", "Alt")),
            Set.of());
    themaRepository.save(thema);

    Thema updated =
        new Thema(
            id,
            "Titel 1",
            null,
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(new Link("https://neu", "Neu")),
            Set.of());
    themaRepository.save(updated);

    Integer count =
        jdbcTemplate.queryForObject(
            "select count(*) from thema_link where thema_id = ?", Integer.class, id);
    assertThat(count).isEqualTo(1);

    var row =
        jdbcTemplate.queryForMap("select url, anzeigetext from thema_link where thema_id = ?", id);
    assertThat(row.get("url")).isEqualTo("https://neu");
    assertThat(row.get("anzeigetext")).isEqualTo("Neu");
  }

  @Test
  @DisplayName("findByTitel returns the correct Thema")
  void findByTitel_returns_correct_thema() {
    UUID id = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
        id,
        "Titel X",
        null);

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
        id1,
        "Titel 1",
        null);
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
        id2,
        "Titel 2",
        null);

    List<Thema> result =
        StreamSupport.stream(themaRepository.findAll().spliterator(), false).toList();

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
        themaId,
        "Thema X",
        null);
    jdbcTemplate.update(
        "insert into betreuer (id, name, email, matrikelnummer) values (?, ?, ?, ?)",
        betreuerId1,
        "Anna",
        null,
        null);
    jdbcTemplate.update(
        "insert into betreuer (id, name, email, matrikelnummer) values (?, ?, ?, ?)",
        betreuerId2,
        "Bernd",
        null,
        null);
    jdbcTemplate.update(
        "insert into betreuer_thema (betreuer_id, thema_id) values (?, ?)", betreuerId1, themaId);
    jdbcTemplate.update(
        "insert into betreuer_thema (betreuer_id, thema_id) values (?, ?)", betreuerId2, themaId);

    assertThat(themaRepository.findBetreuerIdsByThemaId(themaId))
        .containsExactlyInAnyOrder(betreuerId1, betreuerId2);

    assertThat(themaRepository.findBetreuerByThemaIdOrderByName(themaId))
        .extracting(Betreuer::getName)
        .containsExactly("Anna", "Bernd");
  }

  @Test
  @DisplayName("findByTitelContainsIgnoreCase returns matching Themen")
  void findByTitelContainsIgnoreCase_returns_matching_themen() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
        id1,
        "Machine Learning",
        null);
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
        id2,
        "Software Engineering",
        null);

    var result = themaRepository.findByTitelContainsIgnoreCase("learning");

    assertThat(result).extracting(Thema::getTitel).containsExactly("Machine Learning");
  }

  @Test
  @DisplayName("findByFachgebiet returns matching Themen")
  void findByFachgebiet_returns_matching_themen() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
        id1,
        "Thema AI",
        null);
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
        id2,
        "Thema Data",
        null);
    jdbcTemplate.update(
        "insert into thema_fachgebiet (bezeichnung, thema_id) values (?, ?)", "AI", id1);
    jdbcTemplate.update(
        "insert into thema_fachgebiet (bezeichnung, thema_id) values (?, ?)", "Data", id2);

    var result = themaRepository.findByFachgebiet("ai");

    assertThat(result).extracting(Thema::getTitel).containsExactly("Thema AI");
  }

  @Test
  @DisplayName("findByTitelContainsIgnoreCaseAndFachgebiet returns matching Themen")
  void findByTitelContainsIgnoreCaseAndFachgebiet_returns_matching_themen() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
        id1,
        "AI Projekt",
        null);
    jdbcTemplate.update(
        "insert into thema (id, titel, beschreibung_markdown) values (?, ?, ?)",
        id2,
        "AI und Data",
        null);
    jdbcTemplate.update(
        "insert into thema_fachgebiet (bezeichnung, thema_id) values (?, ?)", "AI", id1);
    jdbcTemplate.update(
        "insert into thema_fachgebiet (bezeichnung, thema_id) values (?, ?)", "Data", id2);

    var result = themaRepository.findByTitelContainsIgnoreCaseAndFachgebiet("ai", "ai");

    assertThat(result).extracting(Thema::getId).containsExactly(id1);
  }

  @Test
  @DisplayName("deleteById removes Thema and linked data")
  void deleteById_removes_thema_and_links() {
    UUID id = UUID.randomUUID();
    Thema thema =
        new Thema(
            id,
            "Titel 1",
            null,
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(new Link("https://example.com", "Beispiel")),
            Set.of());
    themaRepository.save(thema);

    themaRepository.deleteById(id);

    Integer themaCount =
        jdbcTemplate.queryForObject("select count(*) from thema where id = ?", Integer.class, id);
    Integer linkCount =
        jdbcTemplate.queryForObject(
            "select count(*) from thema_link where thema_id = ?", Integer.class, id);
    assertThat(themaCount).isEqualTo(0);
    assertThat(linkCount).isEqualTo(0);
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
