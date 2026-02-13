package wiegand.luca.ThesisIsComing.ApplicationService;

import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.UUID;


@Service
public class BetreuerThemenBeziehungService {

    private BetreuerService betreuerService;
    private ThemaService themaService;

    public Betreuer assignThemaToBetreuer(UUID betreuerId, UUID themaId) {
        Betreuer betreuer = betreuerService.getById(betreuerId);
        betreuer.assignThema(themaId);
        return betreuerService.save(betreuer);
    }

    public List<Thema> listThemenByBetreuer(UUID betreuerId) {
        Betreuer betreuer = betreuerService.getById(betreuerId);
        return betreuer.getThemen().stream().toList();
    }

    public Thema assignBetreuerToThema(UUID themaId, UUID betreuerId) {
        Thema thema = themaService.getById(themaId);
        thema.assignBetreuer(betreuerId);
        return themaService.save(thema);
    }





}
