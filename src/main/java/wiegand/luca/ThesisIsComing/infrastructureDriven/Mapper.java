package wiegand.luca.ThesisIsComing.infrastructureDriven;

import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.*;

import java.util.HashSet;
import java.util.Set;

public class Mapper {



    public static Betreuer toBetreuer(BetreuerDto dto) {
        Kontaktinformationen kontaktinformationen = toKontaktinformationen(
                new KontaktinformationenDto(dto.email(), dto.matrikelnummer())
        );
        Betreuer betreuer = new Betreuer(
                dto.id(),
                dto.name(),
                kontaktinformationen,
                toFachgebiete(dto.fachgebiete()),
                toInformationsdateien(dto.dateien()),
                toLinks(dto.links())
        );
        betreuer.getThemen().addAll(toThemen(dto.themen()));
        return betreuer;
    }

    public static BetreuerDto toBetreuerDto(Betreuer betreuer) {
        KontaktinformationenDto kontaktinformationen = toKontaktinformationenDto(betreuer.getKontaktinformationen());
        String email = kontaktinformationen != null ? kontaktinformationen.email() : null;
        String matrikelnummer = kontaktinformationen != null ? kontaktinformationen.matrikelnummer() : null;
        return new BetreuerDto(
                betreuer.getId(),
                betreuer.getName(),
                email,
                matrikelnummer,
                toFachgebietDtos(betreuer.getFachgebiete()),
                toInformationsdateiDtos(betreuer.getDateien()),
                toLinkDtos(betreuer.getLinks()),
                toBetreuerThemaDtos(betreuer.getThemen())
        );
    }

    public static Set<Thema> toThemen(Set<BetreuerThemaDto> dtos) {
        Set<Thema> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (BetreuerThemaDto dto : dtos) {
            result.add(new Thema(dto.themaId(), ""));
        }
        return result;
    }

    public static Set<BetreuerThemaDto> toBetreuerThemaDtos(Set<Thema> themen) {
        Set<BetreuerThemaDto> result = new HashSet<>();
        if (themen == null) {
            return result;
        }
        for (Thema thema : themen) {
            result.add(new BetreuerThemaDto(thema.getId()));
        }
        return result;
    }

    public static Set<Fachgebiet> toFachgebiete(Set<FachgebietDto> dtos) {
        Set<Fachgebiet> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (FachgebietDto dto : dtos) {
            result.add(new Fachgebiet(dto.bezeichnung()));
        }
        return result;
    }

    public static Set<FachgebietDto> toFachgebietDtos(Set<Fachgebiet> fachgebiete) {
        Set<FachgebietDto> result = new HashSet<>();
        if (fachgebiete == null) {
            return result;
        }
        for (Fachgebiet fachgebiet : fachgebiete) {
            result.add(new FachgebietDto(null, fachgebiet.getBezeichnung()));
        }
        return result;
    }

    public static Set<Informationsdatei> toInformationsdateien(Set<InformationsdateiDto> dtos) {
        Set<Informationsdatei> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (InformationsdateiDto dto : dtos) {
            result.add(toInformationsdatei(dto));
        }
        return result;
    }

    public static Set<InformationsdateiDto> toInformationsdateiDtos(Set<Informationsdatei> dateien) {
        Set<InformationsdateiDto> result = new HashSet<>();
        if (dateien == null) {
            return result;
        }
        for (Informationsdatei datei : dateien) {
            result.add(toInformationsdateiDto(datei));
        }
        return result;
    }

    public static Set<Link> toLinks(Set<LinkDto> dtos) {
        Set<Link> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (LinkDto linkDto : dtos) {
            result.add(toLink(linkDto));
        }
        return result;
    }

    public static Set<LinkDto> toLinkDtos(Set<Link> links) {
        Set<LinkDto> result = new HashSet<>();
        if (links == null) {
            return result;
        }
        for (Link link : links) {
            result.add(toLinkDto(link));
        }
        return result;
    }

    public static Informationsdatei toInformationsdatei(InformationsdateiDto dto) {
        if (dto == null) {
            return null;
        }
        return new Informationsdatei(
                dto.titel(),
                dto.beschreibung(),
                dto.uploaderName(),
                dto.uploadDatum(),
                dto.dateisystemPfad()
        );
    }

    public static InformationsdateiDto toInformationsdateiDto(Informationsdatei datei) {
        if (datei == null) {
            return null;
        }
        return new InformationsdateiDto(
                null,
                datei.getTitel(),
                datei.getBeschreibung(),
                datei.getUploaderName(),
                datei.getUploadDatum(),
                datei.getDateisystemPfad()
        );
    }

    public static Link toLink(LinkDto dto) {
        if (dto == null) {
            return null;
        }
        return new Link(dto.url(), dto.anzeigetext());
    }

    public static LinkDto toLinkDto(Link link) {
        if (link == null) {
            return null;
        }
        return new LinkDto(null, link.getUrl(), link.getAnzeigetext());
    }


    public static Thema toThema(ThemaDto dto) {
        return new Thema(
                dto.id(),
                dto.titel(),
                dto.beschreibungMarkdown(),
                toThemaKurse(dto.voraussetzungen()),
                toThemaFachgebiete(dto.fachgebiete()),
                toThemaInformationsdateien(dto.dateien()),
                toThemaLinks(dto.links()),
                null
        );
    }

    public static ThemaDto toThemaDto(Thema thema) {
        return new ThemaDto(
                thema.getId(),
                thema.getTitel(),
                thema.getBeschreibungMarkdown(),
                toThemaKursDtos(thema.getVoraussetzungen()),
                toThemaFachgebietDtos(thema.getFachgebiete()),
                toThemaInformationsdateiDtos(thema.getDateien()),
                toThemaLinkDtos(thema.getLinks())
        );
    }

    public static Set<Kurs> toThemaKurse(Set<ThemaKursDto> dtos) {
        Set<Kurs> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (ThemaKursDto dto : dtos) {
            Fachgebiet fachgebiet = dto.fachgebietBezeichnung() != null
                    ? new Fachgebiet(dto.fachgebietBezeichnung())
                    : null;
            result.add(new Kurs(dto.kursbezeichnung(), fachgebiet));
        }
        return result;
    }

    public static Set<ThemaKursDto> toThemaKursDtos(Set<Kurs> kurse) {
        Set<ThemaKursDto> result = new HashSet<>();
        if (kurse == null) {
            return result;
        }
        for (Kurs kurs : kurse) {
            String fachgebietBezeichnung = kurs.getFachgebietDesKurses() != null
                    ? kurs.getFachgebietDesKurses().getBezeichnung()
                    : null;
            result.add(new ThemaKursDto(
                    null,
                    kurs.getKursbezeichnung(),
                    fachgebietBezeichnung
            ));
        }
        return result;
    }

    public static Set<Fachgebiet> toThemaFachgebiete(Set<ThemaFachgebietDto> dtos) {
        Set<Fachgebiet> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (ThemaFachgebietDto dto : dtos) {
            result.add(new Fachgebiet(dto.bezeichnung()));
        }
        return result;
    }

    public static Set<ThemaFachgebietDto> toThemaFachgebietDtos(Set<Fachgebiet> fachgebiete) {
        Set<ThemaFachgebietDto> result = new HashSet<>();
        if (fachgebiete == null) {
            return result;
        }
        for (Fachgebiet fachgebiet : fachgebiete) {
            result.add(new ThemaFachgebietDto(null, fachgebiet.getBezeichnung()));
        }
        return result;
    }

    public static Set<Informationsdatei> toThemaInformationsdateien(Set<ThemaInformationsdateiDto> dtos) {
        Set<Informationsdatei> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (ThemaInformationsdateiDto dto : dtos) {
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

    public static Set<ThemaInformationsdateiDto> toThemaInformationsdateiDtos(Set<Informationsdatei> dateien) {
        Set<ThemaInformationsdateiDto> result = new HashSet<>();
        if (dateien == null) {
            return result;
        }
        for (Informationsdatei datei : dateien) {
            result.add(new ThemaInformationsdateiDto(
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

    public static Set<Link> toThemaLinks(Set<ThemaLinkDto> dtos) {
        Set<Link> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (ThemaLinkDto dto : dtos) {
            result.add(new Link(dto.url(), dto.anzeigetext()));
        }
        return result;
    }

    public static Set<ThemaLinkDto> toThemaLinkDtos(Set<Link> links) {
        Set<ThemaLinkDto> result = new HashSet<>();
        if (links == null) {
            return result;
        }
        for (Link link : links) {
            result.add(new ThemaLinkDto(null, link.getUrl(), link.getAnzeigetext()));
        }
        return result;
    }

    public static Set<Kurs> toKurse(Set<KursDto> dtos) {
        Set<Kurs> result = new HashSet<>();
        if (dtos == null) {
            return result;
        }
        for (KursDto dto : dtos) {
            result.add(toKurs(dto));
        }
        return result;
    }

    public static Set<KursDto> toKursDtos(Set<Kurs> kurse) {
        Set<KursDto> result = new HashSet<>();
        if (kurse == null) {
            return result;
        }
        for (Kurs kurs : kurse) {
            result.add(toKursDto(kurs));
        }
        return result;
    }

    public static Kurs toKurs(KursDto dto) {
        if (dto == null) {
            return null;
        }
        return new Kurs(dto.Kursbezeichnung(), toFachgebiet(dto.fachgebietDesKurses()));
    }

    public static KursDto toKursDto(Kurs kurs) {
        if (kurs == null) {
            return null;
        }
        return new KursDto(null, kurs.getKursbezeichnung(), toFachgebietDto(kurs.getFachgebietDesKurses()));
    }

    public static BestandenerKurs toBestandenerKurs(BestandenerKursDto dto) {
        return new BestandenerKurs(
                dto.Kursbezeichnung(),
                dto.BestandenAm(),
                toFachgebiet(dto.fachgebietDesKurses())
        );
    }

    public static BestandenerKursDto toBestandenerKursDto(BestandenerKurs kurs) {
        return new BestandenerKursDto(
                null,
                kurs.getKursbezeichnung(),
                kurs.getBestandenAm(),
                toFachgebietDto(kurs.getFachgebietDesKurses())
        );
    }

    public static Fachgebiet toFachgebiet(FachgebietDto dto) {
        if (dto == null) {
            return null;
        }
        return new Fachgebiet(dto.bezeichnung());
    }

    public static FachgebietDto toFachgebietDto(Fachgebiet fachgebiet) {
        if (fachgebiet == null) {
            return null;
        }
        return new FachgebietDto(null, fachgebiet.getBezeichnung());
    }

    public static Kontaktinformationen toKontaktinformationen(KontaktinformationenDto dto) {
        if (dto == null) {
            return null;
        }
        if (dto.email() == null && dto.matrikelnummer() == null) {
            return null;
        }
        return new Kontaktinformationen(dto.email(), dto.matrikelnummer());
    }

    public static KontaktinformationenDto toKontaktinformationenDto(Kontaktinformationen kontakt) {
        if (kontakt == null) {
            return null;
        }
        return new KontaktinformationenDto(kontakt.getEmail(), kontakt.getMatrikelnummer());
    }


}
