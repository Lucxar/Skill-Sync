package wiegand.luca.ThesisIsComing.domainService;

import wiegand.luca.ThesisIsComing.domainModel.Betreuer;

import java.util.Optional;
import java.util.UUID;

public interface BetreuerRepository {
    Optional<Betreuer> findById(UUID id);
    Iterable<Betreuer> findAll();
    Betreuer save(Betreuer betreuer);
}
