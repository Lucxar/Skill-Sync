package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Table("betreuer")
public record BetreuerDto(
        @Id UUID id,
        String name,
        String email,
        String matrikelnummer,
        @MappedCollection(idColumn = "betreuer_id") Set<FachgebietDto> fachgebiete,
        @MappedCollection(idColumn = "betreuer_id") Set<InformationsdateiDto> dateien,
        @MappedCollection(idColumn = "betreuer_id") Set<LinkDto> links
) {
}
