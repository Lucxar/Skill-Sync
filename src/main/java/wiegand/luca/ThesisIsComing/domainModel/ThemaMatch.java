package wiegand.luca.ThesisIsComing.domainModel;

import java.util.Set;

public record ThemaMatch(Thema thema,
                         int score,
                         Set<String> passendeFachgebiete,
                         Set<String> passendeKurse) {

    public ThemaMatch {
        passendeFachgebiete = passendeFachgebiete == null ? Set.of() : Set.copyOf(passendeFachgebiete);
        passendeKurse = passendeKurse == null ? Set.of() : Set.copyOf(passendeKurse);
    }
}
