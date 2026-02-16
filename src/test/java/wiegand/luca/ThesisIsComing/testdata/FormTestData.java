package wiegand.luca.ThesisIsComing.testdata;

import java.util.UUID;
import org.springframework.mock.web.MockMultipartFile;
import wiegand.luca.ThesisIsComing.presentationDriving.BetreuerForm;
import wiegand.luca.ThesisIsComing.presentationDriving.BetreuerRoleAssignmentForm;
import wiegand.luca.ThesisIsComing.presentationDriving.BetreuerRoleRemovalForm;
import wiegand.luca.ThesisIsComing.presentationDriving.DateiUploadForm;
import wiegand.luca.ThesisIsComing.presentationDriving.LinkForm;
import wiegand.luca.ThesisIsComing.presentationDriving.LinkUpdateForm;
import wiegand.luca.ThesisIsComing.presentationDriving.StudentForm;
import wiegand.luca.ThesisIsComing.presentationDriving.ThemaForm;
import wiegand.luca.ThesisIsComing.presentationDriving.UserRoleAssignmentForm;
import wiegand.luca.ThesisIsComing.presentationDriving.UserRoleRemovalForm;

public final class FormTestData {

  private FormTestData() {}

  public static BetreuerForm betreuerForm() {
    return new BetreuerForm("Anna", "anna@uni.de", "M-1", "AI, Datenbanken", 0L);
  }

  public static BetreuerForm betreuerForm(String name, String email, String matrikelnummer) {
    return new BetreuerForm(name, email, matrikelnummer, "AI", 0L);
  }

  public static BetreuerForm betreuerForm(
      String name, String email, String matrikelnummer, String fachgebiete) {
    return new BetreuerForm(name, email, matrikelnummer, fachgebiete, 0L);
  }

  public static ThemaForm themaForm() {
    return new ThemaForm("Neues Thema", "Beschreibung", "AI", 0L);
  }

  public static ThemaForm themaForm(String titel, String beschreibung) {
    return new ThemaForm(titel, beschreibung, "AI", 0L);
  }

  public static ThemaForm themaForm(String titel, String beschreibung, String fachgebiete) {
    return new ThemaForm(titel, beschreibung, fachgebiete, 0L);
  }

  public static StudentForm studentForm() {
    return new StudentForm("Anna", "anna@uni.de", "M-1", "Algo", "AI", 0L);
  }

  public static StudentForm studentForm(
      String name, String email, String matrikelnummer, String kurse, String interessen) {
    return new StudentForm(name, email, matrikelnummer, kurse, interessen, 0L);
  }

  public static LinkForm linkForm() {
    return new LinkForm("https://example.com", "Beispiel");
  }

  public static LinkForm linkForm(String url, String text) {
    return new LinkForm(url, text);
  }

  public static LinkUpdateForm linkUpdateForm() {
    return new LinkUpdateForm("https://old", "Alt", "https://neu", "Neu");
  }

  public static LinkUpdateForm linkUpdateForm(
      String oldUrl, String oldText, String newUrl, String newText) {
    return new LinkUpdateForm(oldUrl, oldText, newUrl, newText);
  }

  public static DateiUploadForm dateiUploadForm(MockMultipartFile file) {
    return new DateiUploadForm("Dokument", "", file);
  }

  public static BetreuerRoleAssignmentForm roleAssignmentForm(UUID id, String login) {
    return new BetreuerRoleAssignmentForm(id, login);
  }

    public static BetreuerRoleRemovalForm roleRemovalForm(UUID id) {
        return new BetreuerRoleRemovalForm(id);
    }

    public static UserRoleAssignmentForm userRoleAssignmentForm(String login, String role) {
        return new UserRoleAssignmentForm(login, role);
    }

    public static UserRoleRemovalForm userRoleRemovalForm(String login, String role) {
        return new UserRoleRemovalForm(login, role);
    }
}
