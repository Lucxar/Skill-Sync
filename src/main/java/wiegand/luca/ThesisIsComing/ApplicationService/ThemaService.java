package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.OptimisticLockingException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaDeleteNotAllowedException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaValidationException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

@Service
public class ThemaService {

  private ThemaRepository repository;

  public ThemaService(ThemaRepository repository) {
    this.repository = repository;
  }

  public List<Thema> listAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
  }

  public List<Thema> searchThemen(SucheForm form) {
    SucheForm criteria = form == null ? new SucheForm(null, null) : form;
    String suchbegriff = criteria.normalizedSuchbegriff();
    Fachgebiet fachgebiet = criteria.toFachgebiet();
    boolean hasSuchbegriff = suchbegriff != null;
    boolean hasFachgebiet = fachgebiet != null;
    if (!hasSuchbegriff && !hasFachgebiet) {
      return listAll();
    }
    if (hasSuchbegriff && hasFachgebiet) {
      return repository.findByTitelContainsIgnoreCaseAndFachgebiet(
          suchbegriff, fachgebiet.getBezeichnung());
    }
    if (hasSuchbegriff) {
      return repository.findByTitelContainsIgnoreCase(suchbegriff);
    }
    return repository.findByFachgebiet(fachgebiet.getBezeichnung());
  }

  public Thema getById(UUID id) {
    return repository.findById(id).orElseThrow(() -> new ThemaNotFoundException(id));
  }

  public Thema save(Thema thema) {
    return repository.save(thema);
  }

  public Thema createThema(String titel, String beschreibungMarkdown, Set<Fachgebiet> fachgebiete) {
    try {
      Thema thema = new Thema(UUID.randomUUID(), titel);
      thema.setBeschreibungMarkdown(beschreibungMarkdown);
      thema.replaceFachgebiete(fachgebiete);
      return repository.save(thema);
    } catch (IllegalArgumentException ex) {
      throw new ThemaValidationException(ex.getMessage());
    }
  }

  public Thema updateThema(
      UUID id,
      String titel,
      String beschreibungMarkdown,
      Set<Fachgebiet> fachgebiete,
      Long expectedVersion) {
    Thema thema = getById(id);
    assertVersionMatches(thema.getVersion(), expectedVersion);
    try {
      thema.updateDetails(titel, beschreibungMarkdown);
      thema.replaceFachgebiete(fachgebiete);
      return saveWithOptimisticHandling(thema);
    } catch (IllegalArgumentException ex) {
      throw new ThemaValidationException(ex.getMessage());
    }
  }

  public void deleteThema(UUID id) {
    getById(id);
    if (!repository.findBetreuerIdsByThemaId(id).isEmpty()) {
      throw new ThemaDeleteNotAllowedException(
          "Thema hat zugeordnete Betreuer und kann nicht geloescht werden");
    }
    repository.deleteById(id);
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
    saveWithOptimisticHandling(thema);
  }

  public void updateLink(
      UUID themaId, String oldUrl, String oldAnzeigetext, String newUrl, String newAnzeigetext) {
    Thema thema = getById(themaId);
    thema.updateLink(new Link(oldUrl, oldAnzeigetext), new Link(newUrl, newAnzeigetext));
    saveWithOptimisticHandling(thema);
  }

  public void removeLink(UUID themaId, String url, String anzeigetext) {
    Thema thema = getById(themaId);
    thema.removeLink(new Link(url, anzeigetext));
    saveWithOptimisticHandling(thema);
  }

  private Thema saveWithOptimisticHandling(Thema thema) {
    try {
      return repository.save(thema);
    } catch (OptimisticLockingFailureException ex) {
      throw new OptimisticLockingException(
          "Datensatz wurde zwischenzeitlich geaendert. Bitte Seite neu laden.");
    }
  }

  private static void assertVersionMatches(Long current, Long expected) {
    if (current == null || expected == null || !current.equals(expected)) {
      throw new OptimisticLockingException(
          "Datensatz wurde zwischenzeitlich geaendert. Bitte Seite neu laden.");
    }
  }
}
