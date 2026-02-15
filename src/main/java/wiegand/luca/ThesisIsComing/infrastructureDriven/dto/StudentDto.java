package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Table("student")
public record StudentDto(
        @Id UUID id,
        String name,
        String email,
        String matrikelnummer,
        @MappedCollection(idColumn = "student_id") Set<StudentBestandenerKursDto> bestandeneKurse,
        @MappedCollection(idColumn = "student_id") Set<StudentFachgebietDto> interessensgebiete
) {
}
