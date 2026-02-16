package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.StudentService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ZuordnungNichtErlaubtException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.CurrentUser;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;

@Controller
public class MeinProfilController {

  private final BetreuerService betreuerService;
  private final StudentService studentService;
  private final CurrentUser currentUser;

  public MeinProfilController(
      BetreuerService betreuerService, StudentService studentService, CurrentUser currentUser) {
    this.betreuerService = betreuerService;
    this.studentService = studentService;
    this.currentUser = currentUser;
  }

  @GetMapping("/mein-profil")
  @PreAuthorize("hasAnyRole('BETREUER','STUDIERENDE')")
  public String showMeinProfil() {
    String login = currentUser.login();
    if (login == null || login.isBlank()) {
      throw new ZuordnungNichtErlaubtException("Kein Login vorhanden.");
    }
    if (currentUser.hasRole(SecurityRoles.ROLE_BETREUER)) {
      return betreuerService
          .findByGithubLogin(login)
          .map(betreuer -> "redirect:/betreuer/" + betreuer.getId())
          .orElseThrow(
              () -> new ZuordnungNichtErlaubtException("Kein Betreuerprofil fuer Login hinterlegt."));
    }
    if (currentUser.hasRole(SecurityRoles.ROLE_STUDIERENDE)) {
      return studentService
          .findByGithubLogin(login)
          .map(student -> "redirect:/studierende/" + student.getId())
          .orElse("redirect:/studierende/neu");
    }
    throw new ZuordnungNichtErlaubtException("Kein Profil fuer Rolle vorhanden.");
  }
}
