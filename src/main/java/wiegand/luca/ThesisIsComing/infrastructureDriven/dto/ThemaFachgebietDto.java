package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("thema_fachgebiet")
public record ThemaFachgebietDto(
        @Id Long id,
        String bezeichnung) {
}
