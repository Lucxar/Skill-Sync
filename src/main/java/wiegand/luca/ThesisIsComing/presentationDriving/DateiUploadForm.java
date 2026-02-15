package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record DateiUploadForm(
        @NotBlank(message = "Titel darf nicht leer sein") String titel,
        String beschreibung,
        MultipartFile datei
) {
}
