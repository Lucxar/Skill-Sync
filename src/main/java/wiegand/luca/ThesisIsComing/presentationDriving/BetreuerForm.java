package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.constraints.NotBlank;

public record BetreuerForm(
        @NotBlank(message = "Name darf nicht leer sein") String name,
        String email,
        String matrikelnummer
) {
}
