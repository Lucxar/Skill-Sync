package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;

@Table("betreuer_informationsdatei")
public record InformationsdateiDto(
    @Id Long id,
    String titel,
    String beschreibung,
    String uploaderName,
    LocalDateTime uploadDatum,
    String dateisystemPfad,
    Dateityp dateityp,
    String originalDateiname,
    Long groesseInBytes) {}
