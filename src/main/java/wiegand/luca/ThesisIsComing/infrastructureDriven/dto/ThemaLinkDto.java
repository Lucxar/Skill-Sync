package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("thema_link")
public record ThemaLinkDto(@Id Long id, String url, String anzeigetext) {}
