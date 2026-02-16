package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

public interface ThemaRepository {

  Optional<Thema> findByTitel(String titel);

  Optional<Thema> findById(UUID id);

  Iterable<Thema> findAll();

  Iterable<Thema> findAllForMatching();

  Thema save(Thema thema);

  void deleteById(UUID id);

  List<UUID> findBetreuerIdsByThemaId(UUID themaId);

  List<Betreuer> findBetreuerByThemaId(UUID themaId);

  List<Betreuer> findBetreuerByThemaIdOrderByName(UUID themaId);

  List<Thema> findByTitelContainsIgnoreCase(String titel);

  List<Thema> findByFachgebiet(String fachgebiet);

  List<Thema> findByTitelContainsIgnoreCaseAndFachgebiet(String titel, String fachgebiet);
}
