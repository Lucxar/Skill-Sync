package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.ThemaDto;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThemaDataRepository extends CrudRepository<ThemaDto, UUID> {
    Optional<ThemaDto> findByTitel(String titel);
}
