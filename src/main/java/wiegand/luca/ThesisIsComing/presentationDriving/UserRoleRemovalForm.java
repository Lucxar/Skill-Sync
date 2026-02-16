package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.constraints.NotBlank;

public record UserRoleRemovalForm(
    @NotBlank(message = "GitHub-Login darf nicht leer sein") String githubLogin,
    @NotBlank(message = "Rolle darf nicht leer sein") String role) {}
