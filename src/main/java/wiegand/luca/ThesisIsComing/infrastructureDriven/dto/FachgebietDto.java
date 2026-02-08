package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("betreuer_fachgebiet")
public record FachgebietDto(@Id Long id, String bezeichnung) {
}
