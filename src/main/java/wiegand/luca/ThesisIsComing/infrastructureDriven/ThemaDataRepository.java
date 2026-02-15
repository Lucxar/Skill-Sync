package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.BetreuerDto;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.ThemaDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThemaDataRepository extends CrudRepository<ThemaDto, UUID> {
    Optional<ThemaDto> findByTitel(String titel);

    @Query("SELECT bt.betreuer_id FROM betreuer_thema bt WHERE bt.thema_id = :themaId")
    List<UUID> findBetreuerIdsByThemaId(@Param("themaId") UUID themaId);

    @Query("SELECT b.* FROM betreuer b JOIN betreuer_thema bt ON bt.betreuer_id = b.id WHERE bt.thema_id = :themaId")
    List<BetreuerDto> findBetreuerByThemaId(@Param("themaId") UUID themaId);

    @Query("SELECT b.* FROM betreuer b JOIN betreuer_thema bt ON bt.betreuer_id = b.id WHERE bt.thema_id = :themaId ORDER BY b.name")
    List<BetreuerDto> findBetreuerByThemaIdOrderByName(@Param("themaId") UUID themaId);

    @Query("SELECT DISTINCT t.* FROM thema t WHERE LOWER(t.titel) LIKE LOWER(CONCAT('%', :titel, '%'))")
    List<ThemaDto> findByTitelContainsIgnoreCase(@Param("titel") String titel);

    @Query("SELECT DISTINCT t.* FROM thema t JOIN thema_fachgebiet tf ON tf.thema_id = t.id "
            + "WHERE LOWER(tf.bezeichnung) = LOWER(:fachgebiet)")
    List<ThemaDto> findByFachgebiet(@Param("fachgebiet") String fachgebiet);

    @Query("SELECT DISTINCT t.* FROM thema t JOIN thema_fachgebiet tf ON tf.thema_id = t.id "
            + "WHERE LOWER(t.titel) LIKE LOWER(CONCAT('%', :titel, '%')) "
            + "AND LOWER(tf.bezeichnung) = LOWER(:fachgebiet)")
    List<ThemaDto> findByTitelContainsIgnoreCaseAndFachgebiet(@Param("titel") String titel,
                                                              @Param("fachgebiet") String fachgebiet);
}
