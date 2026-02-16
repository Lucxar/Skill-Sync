package wiegand.luca.ThesisIsComing.domainModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wiegand.luca.ThesisIsComing.testdata.DomainTestData.student;
import static wiegand.luca.ThesisIsComing.testdata.DomainTestData.studentWithKontakt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentTest {

  @Test
  @DisplayName("Student trims name and clears empty contact info")
  void trims_name_and_clears_empty_contact_info() {
    Student student = student(java.util.UUID.randomUUID(), "  Tom  ");

    student.updateDetails("  Tom  ", " ", " ");

    assertThat(student.getName()).isEqualTo("Tom");
    assertThat(student.getKontaktinformationen()).isNull();
  }

  @Test
  @DisplayName("Student rejects blank names")
  void rejects_blank_names() {
    assertThatThrownBy(() -> student(java.util.UUID.randomUUID(), " "))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Student keeps non-empty contact info")
  void keeps_non_empty_contact_info() {
    Student student = studentWithKontakt("Anna", " anna@uni.de ", " M-1 ");

    assertThat(student.getKontaktinformationen()).isNotNull();
    assertThat(student.getKontaktinformationen().getEmail()).isEqualTo("anna@uni.de");
    assertThat(student.getKontaktinformationen().getMatrikelnummer()).isEqualTo("M-1");
  }
}
