package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("thema_voraussetzung")
public record ThemaKursDto(@Id Long id, String kursbezeichnung, String fachgebietBezeichnung) {}
