package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerRoleAssignmentException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;

import java.util.Comparator;
import java.util.List;

@Controller
public class AdminRollenController {

    private final BetreuerService betreuerService;

    public AdminRollenController(BetreuerService betreuerService) {
        this.betreuerService = betreuerService;
    }

    @GetMapping("/admin/rollen")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRoleManagement(Model model) {
        populateModel(model);
        return "admin/roles";
    }

    @PostMapping("/admin/rollen/zuweisen")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignRole(@Valid @ModelAttribute("assignForm") BetreuerRoleAssignmentForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            return "admin/roles";
        }
        try {
            betreuerService.assignBetreuerRole(form.betreuerId(), form.githubLogin());
            return "redirect:/admin/rollen";
        } catch (BetreuerRoleAssignmentException ex) {
            bindingResult.rejectValue("githubLogin", "betreuer.githubLogin.invalid", ex.getMessage());
            populateModel(model);
            return "admin/roles";
        }
    }

    @PostMapping("/admin/rollen/entziehen")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeRole(@Valid @ModelAttribute("removeForm") BetreuerRoleRemovalForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            return "admin/roles";
        }
        try {
            betreuerService.removeBetreuerRole(form.betreuerId());
            return "redirect:/admin/rollen";
        } catch (BetreuerRoleAssignmentException ex) {
            bindingResult.rejectValue("betreuerId", "betreuer.role.invalid", ex.getMessage());
            populateModel(model);
            return "admin/roles";
        }
    }

    private void populateModel(Model model) {
        List<Betreuer> betreuer = betreuerService.listAll();
        betreuer.sort(Comparator.comparing(Betreuer::getName, String.CASE_INSENSITIVE_ORDER));
        model.addAttribute("betreuer", betreuer);
        if (!model.containsAttribute("assignForm")) {
            model.addAttribute("assignForm", new BetreuerRoleAssignmentForm(null, ""));
        }
        if (!model.containsAttribute("removeForm")) {
            model.addAttribute("removeForm", new BetreuerRoleRemovalForm(null));
        }
    }
}
