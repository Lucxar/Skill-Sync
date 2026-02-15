package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.constraints.NotBlank;

public record StudentForm(
        @NotBlank(message = "Name darf nicht leer sein") String name,
        String email,
        String matrikelnummer,
        String bestandeneKurse,
        String interessensgebiete
) {
}
