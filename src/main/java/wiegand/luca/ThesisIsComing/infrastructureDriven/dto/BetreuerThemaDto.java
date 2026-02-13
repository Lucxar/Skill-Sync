package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("betreuer_thema")
public record BetreuerThemaDto(
        @Column("thema_id") UUID themaId
) {
}
