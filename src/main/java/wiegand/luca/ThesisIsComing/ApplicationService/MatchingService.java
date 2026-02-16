package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.domainModel.BetreuerMatch;
import wiegand.luca.ThesisIsComing.domainModel.ThemaMatch;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.StudierendenVoraussetzungen;

@Service
public class MatchingService {

  private final BetreuerRepository betreuerRepository;
  private final ThemaRepository themaRepository;
  private final wiegand.luca.ThesisIsComing.domainModel.MatchingService matchingService;

  public MatchingService(BetreuerRepository betreuerRepository, ThemaRepository themaRepository) {
    this.betreuerRepository = betreuerRepository;
    this.themaRepository = themaRepository;
    this.matchingService = new wiegand.luca.ThesisIsComing.domainModel.MatchingService();
  }

  public MatchingResult match(StudierendenVoraussetzungen voraussetzungen) {
    StudierendenVoraussetzungen criteria =
        voraussetzungen == null
            ? new StudierendenVoraussetzungen(Set.of(), Set.of())
            : voraussetzungen;
    List<ThemaMatch> themen =
        matchingService.matchThemen(criteria, themaRepository.findAllForMatching());
    List<BetreuerMatch> betreuer =
        matchingService.matchBetreuer(criteria, betreuerRepository.findAllForMatching());
    return new MatchingResult(themen, betreuer);
  }
}
