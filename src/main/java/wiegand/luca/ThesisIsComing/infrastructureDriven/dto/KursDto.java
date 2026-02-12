package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;

public record KursDto(
        @Id Long id,
        String Kursbezeichnung,
        FachgebietDto fachgebietDesKurses) {

}
