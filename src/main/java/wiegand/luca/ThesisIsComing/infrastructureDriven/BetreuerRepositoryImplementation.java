package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
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
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    public BetreuerRepositoryImplementation(BetreuerDataRepository dataRepository,
                                            JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.dataRepository = dataRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
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
        BetreuerDto dto = Mapper.toBetreuerDto(betreuer);
        BetreuerDto saved = (dto.id() != null && dataRepository.existsById(dto.id()))
                ? jdbcAggregateTemplate.update(dto)
                : jdbcAggregateTemplate.insert(dto);
        return Mapper.toBetreuer(saved);
    }


}
