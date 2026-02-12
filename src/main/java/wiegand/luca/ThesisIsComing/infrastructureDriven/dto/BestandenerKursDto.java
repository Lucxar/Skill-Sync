package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;

import java.time.LocalDate;

public record BestandenerKursDto(
        @Id Long id,
        String Kursbezeichnung,
        LocalDate BestandenAm,
        FachgebietDto fachgebietDesKurses) {
}
