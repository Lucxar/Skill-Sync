package wiegand.luca.ThesisIsComing.ApplicationService;

import wiegand.luca.ThesisIsComing.domainModel.BetreuerMatch;
import wiegand.luca.ThesisIsComing.domainModel.ThemaMatch;

import java.util.List;

public record MatchingResult(List<ThemaMatch> themen,
                             List<BetreuerMatch> betreuer) {
}
