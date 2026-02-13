package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Table("thema")
public record ThemaDto(
        @Id UUID id,
        String titel,
        String beschreibungMarkdown,
        @MappedCollection(idColumn = "thema_id") Set<ThemaKursDto> voraussetzungen,
        @MappedCollection(idColumn = "thema_id") Set<ThemaFachgebietDto> fachgebiete,
        @MappedCollection(idColumn = "thema_id") Set<ThemaInformationsdateiDto> dateien,
        @MappedCollection(idColumn = "thema_id") Set<ThemaLinkDto> links)
{


}
