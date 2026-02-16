package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("betreuer")
public record BetreuerDto(
    @Id UUID id,
    String name,
    String email,
    String matrikelnummer,
    String githubLogin,
    @Version Long version,
    @MappedCollection(idColumn = "betreuer_id") Set<FachgebietDto> fachgebiete,
    @MappedCollection(idColumn = "betreuer_id") Set<InformationsdateiDto> dateien,
    @MappedCollection(idColumn = "betreuer_id") Set<LinkDto> links,
    @MappedCollection(idColumn = "betreuer_id") Set<BetreuerThemaDto> themen) {}
