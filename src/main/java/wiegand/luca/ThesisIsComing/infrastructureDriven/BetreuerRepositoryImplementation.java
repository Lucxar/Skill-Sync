package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerRepository;

import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.BetreuerDto;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BetreuerRepositoryImplementation implements BetreuerRepository {

    private final BetreuerDataRepository dataRepository;

    public BetreuerRepositoryImplementation(BetreuerDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public Optional<Betreuer> findById(UUID id) {
        return dataRepository.findById(id).map(Mapper::toBetreuer);
    }

    @Override
    public Iterable<Betreuer> findAll() {
        List<Betreuer> result = new ArrayList<>();
        for (BetreuerDto dto : dataRepository.findAll()) {
            result.add(Mapper.toBetreuer(dto));
        }
        return result;
    }

    @Override
    public Betreuer save(Betreuer betreuer) {
        BetreuerDto saved = dataRepository.save(Mapper.toBetreuerDto(betreuer));
        return Mapper.toBetreuer(saved);
    }


}
