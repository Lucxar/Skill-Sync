package wiegand.luca.ThesisIsComing.ApplicationService.exception;

import java.util.UUID;

public class BetreuerNotFoundException extends RuntimeException {

    public BetreuerNotFoundException(UUID id) {
        super("Betreuer not found: " + id);
    }
}
