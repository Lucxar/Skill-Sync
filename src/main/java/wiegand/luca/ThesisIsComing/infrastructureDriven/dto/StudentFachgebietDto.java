package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("student_fachgebiet")
public record StudentFachgebietDto(@Id Long id, String bezeichnung) {}
