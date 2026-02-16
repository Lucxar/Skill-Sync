package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.List;
import wiegand.luca.ThesisIsComing.domainModel.BetreuerMatch;
import wiegand.luca.ThesisIsComing.domainModel.ThemaMatch;

public record MatchingResult(List<ThemaMatch> themen, List<BetreuerMatch> betreuer) {}
