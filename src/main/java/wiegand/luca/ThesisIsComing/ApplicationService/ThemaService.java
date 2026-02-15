package wiegand.luca.ThesisIsComing.ApplicationService;

import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class ThemaService {

    private ThemaRepository repository;

    public ThemaService(ThemaRepository repository) {
        this.repository = repository;
    }

    public List<Thema> listAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .toList();
    }

    public Thema getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ThemaNotFoundException(id));
    }

    public Thema save(Thema thema) {
        return repository.save(thema);
    }

    public List<Betreuer> getBetreuerByThemaId(UUID themaId) {
        Thema thema = getById(themaId);
        return thema.getBetreuer().stream().toList();
    }

    public List<UUID> findBetreuerIdsByThemaId(UUID themaId) {
        return repository.findBetreuerIdsByThemaId(themaId);
    }

    public List<Betreuer> findBetreuerByThemaId(UUID themaId) {
        return repository.findBetreuerByThemaId(themaId);
    }

    public List<Betreuer> findBetreuerByThemaIdOrderByName(UUID themaId) {
        return repository.findBetreuerByThemaIdOrderByName(themaId);
    }

    public void addLink(UUID themaId, String url, String anzeigetext) {
        Thema thema = getById(themaId);
        thema.addLink(new Link(url, anzeigetext));
        repository.save(thema);
    }

    public void updateLink(UUID themaId,
                           String oldUrl,
                           String oldAnzeigetext,
                           String newUrl,
                           String newAnzeigetext) {
        Thema thema = getById(themaId);
        thema.updateLink(new Link(oldUrl, oldAnzeigetext), new Link(newUrl, newAnzeigetext));
        repository.save(thema);
    }

    public void removeLink(UUID themaId, String url, String anzeigetext) {
        Thema thema = getById(themaId);
        thema.removeLink(new Link(url, anzeigetext));
        repository.save(thema);
    }

}
