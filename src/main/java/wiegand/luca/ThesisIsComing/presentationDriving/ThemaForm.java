package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.constraints.NotBlank;

public record ThemaForm(
        @NotBlank(message = "Titel darf nicht leer sein") String titel,
        String beschreibungMarkdown
) {
}
