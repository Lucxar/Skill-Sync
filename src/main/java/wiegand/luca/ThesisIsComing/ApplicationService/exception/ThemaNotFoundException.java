package wiegand.luca.ThesisIsComing.ApplicationService.exception;

import java.util.UUID;

public class ThemaNotFoundException extends RuntimeException {
    public ThemaNotFoundException(UUID id) {
        super("Thema could not be found" + id);
    }
}
