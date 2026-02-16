package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BetreuerRoleAssignmentForm(
    @NotNull(message = "Betreuer-ID fehlt") UUID betreuerId,
    @NotBlank(message = "GitHub-Login darf nicht leer sein") String githubLogin) {}
