package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("thema_informationsdatei")
public record ThemaInformationsdateiDto(
        @Id Long id,
        String titel,
        String beschreibung,
        String uploaderName,
        LocalDateTime uploadDatum,
        String dateisystemPfad) {
}
