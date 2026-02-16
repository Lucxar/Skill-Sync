package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;

public record BestandenerKursDto(
    @Id Long id,
    String Kursbezeichnung,
    LocalDate BestandenAm,
    FachgebietDto fachgebietDesKurses) {}
