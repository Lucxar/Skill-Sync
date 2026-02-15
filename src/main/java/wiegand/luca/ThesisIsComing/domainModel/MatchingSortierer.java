package wiegand.luca.ThesisIsComing.domainModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class MatchingSortierer {

    private MatchingSortierer() {
    }

    public static List<ThemaMatch> sortiereThemen(List<ThemaMatch> matches) {
        List<ThemaMatch> result = matches == null ? new ArrayList<>() : new ArrayList<>(matches);
        result.sort(Comparator
                .comparingInt(ThemaMatch::score)
                .reversed()
                .thenComparing(match -> normalize(match.thema() != null ? match.thema().getTitel() : null))
        );
        return result;
    }

    public static List<BetreuerMatch> sortiereBetreuer(List<BetreuerMatch> matches) {
        List<BetreuerMatch> result = matches == null ? new ArrayList<>() : new ArrayList<>(matches);
        result.sort(Comparator
                .comparingInt(BetreuerMatch::score)
                .reversed()
                .thenComparing(match -> normalize(match.betreuer() != null ? match.betreuer().getName() : null))
        );
        return result;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
