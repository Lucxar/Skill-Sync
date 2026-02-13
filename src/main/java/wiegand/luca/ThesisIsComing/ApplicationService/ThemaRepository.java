package wiegand.luca.ThesisIsComing.ApplicationService;

import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.Optional;
import java.util.UUID;

public interface ThemaRepository {

    Optional<Thema> findByTitel(String titel);
    Optional<Thema> findById(UUID id);
    Iterable<Thema> findAll();
    Thema save(Thema thema);

}
