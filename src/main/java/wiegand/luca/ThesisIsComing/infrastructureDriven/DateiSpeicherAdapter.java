package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wiegand.luca.ThesisIsComing.ApplicationService.DateiSpeicher;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class DateiSpeicherAdapter implements DateiSpeicher {

    private final Path root;

    public DateiSpeicherAdapter(@Value("${thesis.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir);
    }

    @Override
    public String speichere(String originalDateiname, byte[] inhalt) {
        String extension = extractExtension(originalDateiname);
        String gespeicherterName = UUID.randomUUID() + extension;
        Path ziel = root.resolve(gespeicherterName).normalize();
        try {
            Files.createDirectories(root);
            Files.write(ziel, inhalt);
            return gespeicherterName;
        } catch (IOException ex) {
            throw new IllegalStateException("Datei konnte nicht gespeichert werden", ex);
        }
    }

    @Override
    public byte[] lade(String gespeicherterDateiname) {
        Path ziel = resolveSafePath(gespeicherterDateiname);
        if (!Files.exists(ziel)) {
            throw new DateiNotFoundException("Datei wurde nicht gefunden");
        }
        try {
            return Files.readAllBytes(ziel);
        } catch (IOException ex) {
            throw new IllegalStateException("Datei konnte nicht geladen werden", ex);
        }
    }

    private Path resolveSafePath(String gespeicherterDateiname) {
        if (gespeicherterDateiname == null || gespeicherterDateiname.isBlank()) {
            throw new DateiNotFoundException("Datei wurde nicht gefunden");
        }
        if (gespeicherterDateiname.contains("/") || gespeicherterDateiname.contains("\\")) {
            throw new DateiNotFoundException("Datei wurde nicht gefunden");
        }
        return root.resolve(gespeicherterDateiname).normalize();
    }

    private static String extractExtension(String originalDateiname) {
        if (originalDateiname == null) {
            return "";
        }
        String trimmed = originalDateiname.trim();
        int dotIndex = trimmed.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == trimmed.length() - 1) {
            return "";
        }
        return trimmed.substring(dotIndex).toLowerCase();
    }
}
