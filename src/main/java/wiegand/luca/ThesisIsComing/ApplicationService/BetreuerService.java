package wiegand.luca.ThesisIsComing.ApplicationService;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class BetreuerService {

    private BetreuerRepository betreuerRepository;

    public BetreuerService(BetreuerRepository betreuerRepository) {
        this.betreuerRepository = betreuerRepository;
    }


    public List<Betreuer> listAll() {
        return StreamSupport.stream(betreuerRepository.findAll().spliterator(), false)
                .toList();
    }


    public Betreuer getById(UUID id) {
        return betreuerRepository.findById(id)
                .orElseThrow(() -> new BetreuerNotFoundException(id));
    }

    public Betreuer save(Betreuer betreuer) {
        return betreuerRepository.save(betreuer);
    }

    public java.util.Optional<Betreuer> findByGithubLogin(String githubLogin) {
        return betreuerRepository.findByGithubLogin(githubLogin);
    }

    public List<Thema> listThemenByBetreuer(UUID betreuerId) {
        Betreuer betreuer = getById(betreuerId);
        return betreuer.getThemen().stream().toList();
    }

    public List<UUID> findThemaIdsByBetreuerId(UUID betreuerId) {
        return betreuerRepository.findThemaIdsByBetreuerId(betreuerId);
    }

    public List<Thema> findThemenByBetreuerId(UUID betreuerId) {
        return betreuerRepository.findThemenByBetreuerId(betreuerId);
    }

    public List<Thema> findThemenByBetreuerIdOrderByTitel(UUID betreuerId) {
        return betreuerRepository.findThemenByBetreuerIdOrderByTitel(betreuerId);
    }

    public long countThemenByBetreuerId(UUID betreuerId) {
        return betreuerRepository.countThemenByBetreuerId(betreuerId);
    }

    public boolean existsBetreuerThema(UUID betreuerId, UUID themaId) {
        return betreuerRepository.existsBetreuerThema(betreuerId, themaId);
    }


}
