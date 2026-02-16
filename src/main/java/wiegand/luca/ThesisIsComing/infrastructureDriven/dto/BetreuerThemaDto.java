package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import java.util.UUID;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("betreuer_thema")
public record BetreuerThemaDto(@Column("thema_id") UUID themaId) {}
