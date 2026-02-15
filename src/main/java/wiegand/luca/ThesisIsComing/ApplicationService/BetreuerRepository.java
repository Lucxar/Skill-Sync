package wiegand.luca.ThesisIsComing.ApplicationService;

import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BetreuerRepository {
    Optional<Betreuer> findById(UUID id);
    Iterable<Betreuer> findAll();
    Betreuer save(Betreuer betreuer);
    Optional<Betreuer> findByGithubLogin(String githubLogin);

    List<UUID> findThemaIdsByBetreuerId(UUID betreuerId);
    List<Thema> findThemenByBetreuerId(UUID betreuerId);
    List<Thema> findThemenByBetreuerIdOrderByTitel(UUID betreuerId);
    long countThemenByBetreuerId(UUID betreuerId);
    boolean existsBetreuerThema(UUID betreuerId, UUID themaId);
}
