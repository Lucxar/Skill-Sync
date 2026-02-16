package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

public interface BetreuerRepository {
  Optional<Betreuer> findById(UUID id);

  Iterable<Betreuer> findAll();

  Iterable<Betreuer> findAllForMatching();

  Betreuer save(Betreuer betreuer);

  Optional<Betreuer> findByGithubLogin(String githubLogin);

  void deleteById(UUID id);

  List<UUID> findThemaIdsByBetreuerId(UUID betreuerId);

  List<Thema> findThemenByBetreuerId(UUID betreuerId);

  List<Thema> findThemenByBetreuerIdOrderByTitel(UUID betreuerId);

  long countThemenByBetreuerId(UUID betreuerId);

  boolean existsBetreuerThema(UUID betreuerId, UUID themaId);

  List<Betreuer> findByNameContainsIgnoreCase(String name);

  List<Betreuer> findByFachgebiet(String fachgebiet);

  List<Betreuer> findByNameContainsIgnoreCaseAndFachgebiet(String name, String fachgebiet);
}
