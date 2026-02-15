package wiegand.luca.ThesisIsComing.domainModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record MatchScore(boolean kompatibel,
                         int score,
                         Set<String> passendeFachgebiete,
                         Set<String> passendeKurse) {

    public static MatchScore notCompatible() {
        return new MatchScore(false, 0, Set.of(), Set.of());
    }

    public static MatchScore compatible(int score,
                                        Set<String> passendeFachgebiete,
                                        Set<String> passendeKurse) {
        Set<String> fachgebiete = toUnmodifiable(passendeFachgebiete);
        Set<String> kurse = toUnmodifiable(passendeKurse);
        return new MatchScore(true, score, fachgebiete, kurse);
    }

    private static Set<String> toUnmodifiable(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }
        return Collections.unmodifiableSet(new HashSet<>(values));
    }
}
