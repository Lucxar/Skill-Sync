package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerRepository;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.BetreuerDto;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.FachgebietDto;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.InformationsdateiDto;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.LinkDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class BetreuerRepositoryImplementation implements BetreuerRepository {

    private final BetreuerDataRepository dataRepository;

    public BetreuerRepositoryImplementation(BetreuerDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public Optional<Betreuer> findById(UUID id) {
        return dataRepository.findById(id).map(this::toBetreuer);
    }

    @Override
    public Iterable<Betreuer> findAll() {
        List<Betreuer> result = new ArrayList<>();
        for (BetreuerDto dto : dataRepository.findAll()) {
            result.add(toBetreuer(dto));
        }
        return result;
    }

    @Override
    public Betreuer save(Betreuer betreuer) {
        BetreuerDto saved = dataRepository.save(toBetreuerDto(betreuer));
        return toBetreuer(saved);
    }

    private Betreuer toBetreuer(BetreuerDto dto) {
        Kontaktinformationen kontaktinformationen = null;
        if (dto.email() != null || dto.matrikelnummer() != null) {
            kontaktinformationen = new Kontaktinformationen(dto.email(), dto.matrikelnummer());
        }
        return new Betreuer(
                dto.id(),
                dto.name(),
                kontaktinformationen,
                toFachgebiete(dto.fachgebiete()),
                toInformationsdateien(dto.dateien()),
                toLinks(dto.links())
        );
    }

    private BetreuerDto toBetreuerDto(Betreuer betreuer) {
        Kontaktinformationen kontaktinformationen = betreuer.getKontaktinformationen();
        String email = kontaktinformationen != null ? kontaktinformationen.getEmail() : null;
        String matrikelnummer = kontaktinformationen != null ? kontaktinformationen.getMatrikelnummer() : null;
        return new BetreuerDto(
                betreuer.getId(),
                betreuer.getName(),
                email,
                matrikelnummer,
                toFachgebietDtos(betreuer.getFachgebiete()),
                toInformationsdateiDtos(betreuer.getDateien()),
                toLinkDtos(betreuer.getLinks())
        );
    }

    private Set<Fachgebiet> toFachgebiete(Set<FachgebietDto> dtos) {
        Set<Fachgebiet> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (FachgebietDto dto : dtos) {
            result.add(new Fachgebiet(dto.bezeichnung()));
        }
        return result;
    }

    private Set<FachgebietDto> toFachgebietDtos(Set<Fachgebiet> fachgebiete) {
        Set<FachgebietDto> result = new HashSet<>();
        if (fachgebiete == null) {
            return result;
        }
        for (Fachgebiet fachgebiet : fachgebiete) {
            result.add(new FachgebietDto(null, fachgebiet.getBezeichnung()));
        }
        return result;
    }

    private Set<Informationsdatei> toInformationsdateien(Set<InformationsdateiDto> dtos) {
        Set<Informationsdatei> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (InformationsdateiDto dto : dtos) {
            result.add(new Informationsdatei(
                    dto.titel(),
                    dto.beschreibung(),
                    dto.uploaderName(),
                    dto.uploadDatum(),
                    dto.dateisystemPfad()
            ));
        }
        return result;
    }

    private Set<InformationsdateiDto> toInformationsdateiDtos(Set<Informationsdatei> dateien) {
        Set<InformationsdateiDto> result = new HashSet<>();
        if (dateien == null) {
            return result;
        }
        for (Informationsdatei datei : dateien) {
            result.add(new InformationsdateiDto(
                    null,
                    datei.getTitel(),
                    datei.getBeschreibung(),
                    datei.getUploaderName(),
                    datei.getUploadDatum(),
                    datei.getDateisystemPfad()
            ));
        }
        return result;
    }

    private Set<Link> toLinks(Set<LinkDto> dtos) {
        Set<Link> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (LinkDto linkDto : dtos) {
            result.add(new Link(linkDto.url(), linkDto.anzeigetext()));
        }
        return result;
    }

    private Set<LinkDto> toLinkDtos(Set<Link> links) {
        Set<LinkDto> result = new HashSet<>();
        if (links == null) {
            return result;
        }
        for (Link link : links) {
            result.add(new LinkDto(null, link.getUrl(), link.getAnzeigetext()));
        }
        return result;
    }
}
