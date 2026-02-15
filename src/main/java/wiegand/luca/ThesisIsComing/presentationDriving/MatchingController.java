package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wiegand.luca.ThesisIsComing.ApplicationService.MatchingForm;
import wiegand.luca.ThesisIsComing.ApplicationService.MatchingResult;
import wiegand.luca.ThesisIsComing.ApplicationService.MatchingService;

@Controller
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/matching")
    public String showForm(Model model) {
        if (!model.containsAttribute("matchingForm")) {
            model.addAttribute("matchingForm", new MatchingForm("", ""));
        }
        return "matching/form";
    }

    @PostMapping("/matching")
    public String submitForm(@ModelAttribute("matchingForm") MatchingForm form,
                             RedirectAttributes redirectAttributes) {
        MatchingForm criteria = form == null ? new MatchingForm("", "") : form;
        if (criteria.isEmpty()) {
            redirectAttributes.addFlashAttribute("matchingError", "Bitte mindestens ein Feld ausfuellen.");
            return "redirect:/matching";
        }
        redirectAttributes.addAttribute("bestandeneKurse", criteria.bestandeneKurse());
        redirectAttributes.addAttribute("interessensgebiete", criteria.interessensgebiete());
        return "redirect:/matching/ergebnisse";
    }

    @GetMapping("/matching/ergebnisse")
    public String showResults(@ModelAttribute("matchingForm") MatchingForm form, Model model) {
        MatchingForm criteria = form == null ? new MatchingForm("", "") : form;
        MatchingResult result = matchingService.match(criteria.toVoraussetzungen());
        model.addAttribute("matchingForm", criteria);
        model.addAttribute("themenMatches", result.themen());
        model.addAttribute("betreuerMatches", result.betreuer());
        return "matching/results";
    }
}
