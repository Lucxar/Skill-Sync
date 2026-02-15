package wiegand.luca.ThesisIsComing.domainModel;

import java.util.Set;

public record BetreuerMatch(Betreuer betreuer,
                            int score,
                            Set<String> passendeFachgebiete) {

    public BetreuerMatch {
        passendeFachgebiete = passendeFachgebiete == null ? Set.of() : Set.copyOf(passendeFachgebiete);
    }
}
