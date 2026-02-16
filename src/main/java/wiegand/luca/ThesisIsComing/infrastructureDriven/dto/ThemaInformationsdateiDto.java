package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("thema_informationsdatei")
public record ThemaInformationsdateiDto(
    @Id Long id,
    String titel,
    String beschreibung,
    String uploaderName,
    LocalDateTime uploadDatum,
    String dateisystemPfad) {}
