package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

public enum Dateityp {
    ZIP,
    PDF,
    MARKDOWN;

    public static Dateityp fromDateiname(String dateiname) {
        if (dateiname == null || dateiname.trim().isEmpty()) {
            throw new IllegalArgumentException("Dateiname darf nicht leer sein");
        }
        String lower = dateiname.trim().toLowerCase();
        if (lower.endsWith(".zip")) {
            return ZIP;
        }
        if (lower.endsWith(".pdf")) {
            return PDF;
        }
        if (lower.endsWith(".md") || lower.endsWith(".markdown")) {
            return MARKDOWN;
        }
        throw new IllegalArgumentException("Unbekannter Dateityp: " + dateiname);
    }
}
