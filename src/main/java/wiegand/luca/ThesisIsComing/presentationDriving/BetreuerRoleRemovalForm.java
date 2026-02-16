package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BetreuerRoleRemovalForm(@NotNull(message = "Betreuer-ID fehlt") UUID betreuerId) {}
