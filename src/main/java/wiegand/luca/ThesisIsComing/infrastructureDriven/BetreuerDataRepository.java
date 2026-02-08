package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.BetreuerDto;

import java.util.UUID;

@Repository
public interface BetreuerDataRepository extends CrudRepository<BetreuerDto, UUID> {
}
