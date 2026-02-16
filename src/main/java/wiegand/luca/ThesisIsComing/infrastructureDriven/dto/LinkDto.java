package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("betreuer_link")
public record LinkDto(@Id Long id, String url, String anzeigetext) {}
