package wiegand.luca.ThesisIsComing.infrastructureDriven;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaRepository;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.ThemaDto;

@Repository
public class ThemaRepositoryImplementation implements ThemaRepository {

  private final ThemaDataRepository repository;
  private final JdbcAggregateTemplate jdbcAggregateTemplate;

  public ThemaRepositoryImplementation(
      ThemaDataRepository repository, JdbcAggregateTemplate jdbcAggregateTemplate) {
    this.repository = repository;
    this.jdbcAggregateTemplate = jdbcAggregateTemplate;
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
  public Iterable<Thema> findAllForMatching() {
    return findAll();
  }

  @Override
  public Thema save(Thema thema) {
    ThemaDto dto = Mapper.toThemaDto(thema);
    ThemaDto saved =
        (dto.id() != null && repository.existsById(dto.id()))
            ? jdbcAggregateTemplate.update(dto)
            : jdbcAggregateTemplate.insert(dto);
    return Mapper.toThema(saved);
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }

  @Override
  public List<UUID> findBetreuerIdsByThemaId(UUID themaId) {
    return repository.findBetreuerIdsByThemaId(themaId);
  }

  @Override
  public List<Betreuer> findBetreuerByThemaId(UUID themaId) {
    List<Betreuer> result = new ArrayList<>();
    for (var dto : repository.findBetreuerByThemaId(themaId)) {
      result.add(Mapper.toBetreuer(dto));
    }
    return result;
  }

  @Override
  public List<Betreuer> findBetreuerByThemaIdOrderByName(UUID themaId) {
    List<Betreuer> result = new ArrayList<>();
    for (var dto : repository.findBetreuerByThemaIdOrderByName(themaId)) {
      result.add(Mapper.toBetreuer(dto));
    }
    return result;
  }

  @Override
  public List<Thema> findByTitelContainsIgnoreCase(String titel) {
    List<Thema> result = new ArrayList<>();
    for (ThemaDto dto : repository.findByTitelContainsIgnoreCase(titel)) {
      result.add(Mapper.toThema(dto));
    }
    return result;
  }

  @Override
  public List<Thema> findByFachgebiet(String fachgebiet) {
    List<Thema> result = new ArrayList<>();
    for (ThemaDto dto : repository.findByFachgebiet(fachgebiet)) {
      result.add(Mapper.toThema(dto));
    }
    return result;
  }

  @Override
  public List<Thema> findByTitelContainsIgnoreCaseAndFachgebiet(String titel, String fachgebiet) {
    List<Thema> result = new ArrayList<>();
    for (ThemaDto dto : repository.findByTitelContainsIgnoreCaseAndFachgebiet(titel, fachgebiet)) {
      result.add(Mapper.toThema(dto));
    }
    return result;
  }
}
