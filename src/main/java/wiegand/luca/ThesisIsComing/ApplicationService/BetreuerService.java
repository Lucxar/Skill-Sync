package wiegand.luca.ThesisIsComing.ApplicationService;

import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class BetreuerService {

    private static final long MAX_UPLOAD_BYTES = 10 * 1024 * 1024;

    private final BetreuerRepository betreuerRepository;
    private final DateiSpeicher dateiSpeicher;
    private final CurrentUser currentUser;

    public BetreuerService(BetreuerRepository betreuerRepository,
                           DateiSpeicher dateiSpeicher,
                           CurrentUser currentUser) {
        this.betreuerRepository = betreuerRepository;
        this.dateiSpeicher = dateiSpeicher;
        this.currentUser = currentUser;
    }


    public List<Betreuer> listAll() {
        return StreamSupport.stream(betreuerRepository.findAll().spliterator(), false)
                .toList();
    }

    public List<Betreuer> searchBetreuer(SucheForm form) {
        SucheForm criteria = form == null ? new SucheForm(null, null) : form;
        String suchbegriff = criteria.normalizedSuchbegriff();
        Fachgebiet fachgebiet = criteria.toFachgebiet();
        boolean hasSuchbegriff = suchbegriff != null;
        boolean hasFachgebiet = fachgebiet != null;
        if (!hasSuchbegriff && !hasFachgebiet) {
            return listAll();
        }
        if (hasSuchbegriff && hasFachgebiet) {
            return betreuerRepository.findByNameContainsIgnoreCaseAndFachgebiet(
                    suchbegriff,
                    fachgebiet.getBezeichnung()
            );
        }
        if (hasSuchbegriff) {
            return betreuerRepository.findByNameContainsIgnoreCase(suchbegriff);
        }
        return betreuerRepository.findByFachgebiet(fachgebiet.getBezeichnung());
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

    public Informationsdatei uploadDatei(UUID betreuerId, DateiUploadData data) {
        Betreuer betreuer = getById(betreuerId);
        DateiUploadData validated = validateUploadData(data);
        Dateityp dateityp = resolveDateityp(validated.originalDateiname());
        String gespeicherterName = dateiSpeicher.speichere(validated.originalDateiname(), validated.inhalt());
        String uploaderName = resolveUploaderName();
        Informationsdatei datei;
        try {
            datei = new Informationsdatei(
                    validated.titel(),
                    validated.beschreibung(),
                    uploaderName,
                    LocalDateTime.now(),
                    gespeicherterName,
                    dateityp,
                    validated.originalDateiname(),
                    validated.groesseInBytes()
            );
        } catch (IllegalArgumentException ex) {
            throw new DateiValidationException(ex.getMessage());
        }
        betreuer.addDatei(datei);
        betreuerRepository.save(betreuer);
        return datei;
    }

    public Informationsdatei findDatei(UUID betreuerId, String gespeicherterName) {
        if (gespeicherterName == null || gespeicherterName.isBlank()) {
            throw new DateiNotFoundException("Datei wurde nicht gefunden");
        }
        Betreuer betreuer = getById(betreuerId);
        return betreuer.getDateien().stream()
                .filter(datei -> gespeicherterName.equals(datei.getDateisystemPfad()))
                .findFirst()
                .orElseThrow(() -> new DateiNotFoundException("Datei wurde nicht gefunden"));
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

    private DateiUploadData validateUploadData(DateiUploadData data) {
        if (data == null) {
            throw new DateiValidationException("Datei-Daten fehlen");
        }
        if (data.inhalt() == null || data.inhalt().length == 0) {
            throw new DateiValidationException("Datei ist leer");
        }
        if (data.groesseInBytes() > MAX_UPLOAD_BYTES) {
            throw new DateiValidationException("Datei ist groesser als 10 MB");
        }
        if (data.originalDateiname() == null || data.originalDateiname().trim().isEmpty()) {
            throw new DateiValidationException("Dateiname fehlt");
        }
        return data;
    }

    private Dateityp resolveDateityp(String dateiname) {
        try {
            return Dateityp.fromDateiname(dateiname);
        } catch (IllegalArgumentException ex) {
            throw new DateiValidationException(ex.getMessage());
        }
    }

    private String resolveUploaderName() {
        String login = currentUser.login();
        return (login == null || login.isBlank()) ? "unbekannt" : login;
    }


}
