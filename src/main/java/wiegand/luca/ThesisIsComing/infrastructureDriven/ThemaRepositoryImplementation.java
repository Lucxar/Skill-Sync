package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaRepository;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.ThemaDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ThemaRepositoryImplementation implements ThemaRepository {

    private final ThemaDataRepository repository;

    public ThemaRepositoryImplementation(ThemaDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Thema> findByTitel(String titel) {
        return repository.findByTitel(titel).map(Mapper::toThema);
    }

    @Override
    public Optional<Thema> findById(UUID id) {
        return repository.findById(id).map(Mapper::toThema);
    }

    @Override
    public Iterable<Thema> findAll() {
        List<Thema> result = new ArrayList<>();
        for (ThemaDto dto : repository.findAll()) {
            result.add(Mapper.toThema(dto));
        }
        return result;
    }

    @Override
    public void save(Thema thema) {
        repository.save(Mapper.toThemaDto(thema));
    }

    @Override
    public void update(Thema thema) {
        repository.save(Mapper.toThemaDto(thema));
    }
}
