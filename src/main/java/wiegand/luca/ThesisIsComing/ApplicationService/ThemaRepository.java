package wiegand.luca.ThesisIsComing.ApplicationService;

import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThemaRepository {

    Optional<Thema> findByTitel(String titel);
    Optional<Thema> findById(UUID id);
    Iterable<Thema> findAll();
    Thema save(Thema thema);
    void deleteById(UUID id);

    List<UUID> findBetreuerIdsByThemaId(UUID themaId);
    List<Betreuer> findBetreuerByThemaId(UUID themaId);
    List<Betreuer> findBetreuerByThemaIdOrderByName(UUID themaId);

}
