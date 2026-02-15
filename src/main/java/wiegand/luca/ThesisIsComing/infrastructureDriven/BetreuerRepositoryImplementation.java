package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
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

    @Override
    public Optional<Betreuer> findByGithubLogin(String githubLogin) {
        return dataRepository.findByGithubLogin(githubLogin).map(Mapper::toBetreuer);
    }

    @Override
    public void deleteById(UUID id) {
        dataRepository.deleteById(id);
    }

    @Override
    public List<UUID> findThemaIdsByBetreuerId(UUID betreuerId) {
        return dataRepository.findThemaIdsByBetreuerId(betreuerId);
    }

    @Override
    public List<Thema> findThemenByBetreuerId(UUID betreuerId) {
        List<Thema> result = new ArrayList<>();
        for (var dto : dataRepository.findThemenByBetreuerId(betreuerId)) {
            result.add(Mapper.toThema(dto));
        }
        return result;
    }

    @Override
    public List<Thema> findThemenByBetreuerIdOrderByTitel(UUID betreuerId) {
        List<Thema> result = new ArrayList<>();
        for (var dto : dataRepository.findThemenByBetreuerIdOrderByTitel(betreuerId)) {
            result.add(Mapper.toThema(dto));
        }
        return result;
    }

    @Override
    public long countThemenByBetreuerId(UUID betreuerId) {
        return dataRepository.countThemenByBetreuerId(betreuerId);
    }

    @Override
    public boolean existsBetreuerThema(UUID betreuerId, UUID themaId) {
        return dataRepository.existsBetreuerThema(betreuerId, themaId);
    }

    @Override
    public List<Betreuer> findByNameContainsIgnoreCase(String name) {
        List<Betreuer> result = new ArrayList<>();
        for (BetreuerDto dto : dataRepository.findByNameContainsIgnoreCase(name)) {
            result.add(Mapper.toBetreuer(dto));
        }
        return result;
    }

    @Override
    public List<Betreuer> findByFachgebiet(String fachgebiet) {
        List<Betreuer> result = new ArrayList<>();
        for (BetreuerDto dto : dataRepository.findByFachgebiet(fachgebiet)) {
            result.add(Mapper.toBetreuer(dto));
        }
        return result;
    }

    @Override
    public List<Betreuer> findByNameContainsIgnoreCaseAndFachgebiet(String name, String fachgebiet) {
        List<Betreuer> result = new ArrayList<>();
        for (BetreuerDto dto : dataRepository.findByNameContainsIgnoreCaseAndFachgebiet(name, fachgebiet)) {
            result.add(Mapper.toBetreuer(dto));
        }
        return result;
    }

    


}
