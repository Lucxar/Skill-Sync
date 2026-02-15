package wiegand.luca.ThesisIsComing.domainModel;

import wiegand.luca.ThesisIsComing.domainModel.valueObjects.StudierendenVoraussetzungen;

import java.util.ArrayList;
import java.util.List;

public class MatchingService {

    public List<ThemaMatch> matchThemen(StudierendenVoraussetzungen voraussetzungen,
                                       Iterable<Thema> themen) {
        List<ThemaMatch> result = new ArrayList<>();
        if (themen == null) {
            return result;
        }
        for (Thema thema : themen) {
            MatchScore score = MatchingRegeln.bewerteThema(thema, voraussetzungen);
            if (score.kompatibel()) {
                result.add(new ThemaMatch(thema, score.score(), score.passendeFachgebiete(), score.passendeKurse()));
            }
        }
        return MatchingSortierer.sortiereThemen(result);
    }

    public List<BetreuerMatch> matchBetreuer(StudierendenVoraussetzungen voraussetzungen,
                                            Iterable<Betreuer> betreuer) {
        List<BetreuerMatch> result = new ArrayList<>();
        if (betreuer == null) {
            return result;
        }
        for (Betreuer einBetreuer : betreuer) {
            MatchScore score = MatchingRegeln.bewerteBetreuer(einBetreuer, voraussetzungen);
            if (score.kompatibel()) {
                result.add(new BetreuerMatch(einBetreuer, score.score(), score.passendeFachgebiete()));
            }
        }
        return MatchingSortierer.sortiereBetreuer(result);
    }
}
