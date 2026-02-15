package wiegand.luca.ThesisIsComing.ApplicationService;

import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

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

    public Betreuer createBetreuer(String name, String email, String matrikelnummer) {
        try {
            Kontaktinformationen kontaktinformationen = new Kontaktinformationen(email, matrikelnummer);
            Betreuer betreuer = new Betreuer(name, kontaktinformationen);
            return betreuerRepository.save(betreuer);
        } catch (IllegalArgumentException ex) {
            throw new BetreuerValidationException(ex.getMessage());
        }
    }

    public Betreuer updateBetreuer(UUID id, String name, String email, String matrikelnummer) {
        Betreuer betreuer = getById(id);
        try {
            betreuer.updateDetails(name, email, matrikelnummer);
            return betreuerRepository.save(betreuer);
        } catch (IllegalArgumentException ex) {
            throw new BetreuerValidationException(ex.getMessage());
        }
    }

    public void deleteBetreuer(UUID id) {
        getById(id);
        betreuerRepository.deleteById(id);
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

    public void addLink(UUID betreuerId, String url, String anzeigetext) {
        Betreuer betreuer = getById(betreuerId);
        betreuer.addLink(new Link(url, anzeigetext));
        betreuerRepository.save(betreuer);
    }

    public void updateLink(UUID betreuerId,
                           String oldUrl,
                           String oldAnzeigetext,
                           String newUrl,
                           String newAnzeigetext) {
        Betreuer betreuer = getById(betreuerId);
        betreuer.updateLink(new Link(oldUrl, oldAnzeigetext), new Link(newUrl, newAnzeigetext));
        betreuerRepository.save(betreuer);
    }

    public void removeLink(UUID betreuerId, String url, String anzeigetext) {
        Betreuer betreuer = getById(betreuerId);
        betreuer.removeLink(new Link(url, anzeigetext));
        betreuerRepository.save(betreuer);
    }


}
