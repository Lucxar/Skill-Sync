package wiegand.luca.ThesisIsComing.infrastructureDriven;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.BetreuerDto;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.ThemaDto;

import java.util.List;
import java.util.UUID;

@Repository
public interface BetreuerDataRepository extends CrudRepository<BetreuerDto, UUID> {

    @Query("SELECT bt.thema_id FROM betreuer_thema bt WHERE bt.betreuer_id = :betreuerId")
    List<UUID> findThemaIdsByBetreuerId(@Param("betreuerId") UUID betreuerId);

    @Query("SELECT * FROM betreuer WHERE github_login = :githubLogin")
    java.util.Optional<BetreuerDto> findByGithubLogin(@Param("githubLogin") String githubLogin);


    @Query("SELECT t.* FROM thema t JOIN betreuer_thema bt ON bt.thema_id = t.id WHERE bt.betreuer_id = :betreuerId")
    List<ThemaDto> findThemenByBetreuerId(@Param("betreuerId") UUID betreuerId);


    @Query("SELECT t.* FROM thema t JOIN betreuer_thema bt ON bt.thema_id = t.id WHERE bt.betreuer_id = :betreuerId ORDER BY t.titel")
    List<ThemaDto> findThemenByBetreuerIdOrderByTitel(@Param("betreuerId") UUID betreuerId);


    @Query("SELECT COUNT(*) FROM betreuer_thema bt WHERE bt.betreuer_id = :betreuerId")
    long countThemenByBetreuerId(@Param("betreuerId") UUID betreuerId);


    @Query("SELECT EXISTS(SELECT 1 FROM betreuer_thema bt WHERE bt.betreuer_id = :betreuerId AND bt.thema_id = :themaId)")
    boolean existsBetreuerThema(@Param("betreuerId") UUID betreuerId, @Param("themaId") UUID themaId);

    @Query("SELECT DISTINCT b.* FROM betreuer b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<BetreuerDto> findByNameContainsIgnoreCase(@Param("name") String name);

    @Query("SELECT DISTINCT b.* FROM betreuer b JOIN betreuer_fachgebiet bf ON bf.betreuer_id = b.id "
            + "WHERE LOWER(bf.bezeichnung) = LOWER(:fachgebiet)")
    List<BetreuerDto> findByFachgebiet(@Param("fachgebiet") String fachgebiet);

    @Query("SELECT DISTINCT b.* FROM betreuer b JOIN betreuer_fachgebiet bf ON bf.betreuer_id = b.id "
            + "WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "AND LOWER(bf.bezeichnung) = LOWER(:fachgebiet)")
    List<BetreuerDto> findByNameContainsIgnoreCaseAndFachgebiet(@Param("name") String name,
                                                                @Param("fachgebiet") String fachgebiet);
}
