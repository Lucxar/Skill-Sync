package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("student_bestandener_kurs")
public record StudentBestandenerKursDto(
        @Id Long id,
        String kursbezeichnung,
        LocalDate bestandenAm,
        String fachgebietBezeichnung
) {
}
