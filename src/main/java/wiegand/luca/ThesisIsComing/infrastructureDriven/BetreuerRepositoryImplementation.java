package wiegand.luca.ThesisIsComing.infrastructureDriven;

import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.BetreuerDTO;

public class BetreuerRepositoryImplementation implements BetreuerDataRepository {


    BetreuerDataRepository repository;

    public void saveBetreuer(BetreuerDTO betreuer) {
        repository.save(betreuer);
    }

    public BetreuerDTO findBetreuerById(String id) {
        return repository.findBetreuerById(id);
   }





}
