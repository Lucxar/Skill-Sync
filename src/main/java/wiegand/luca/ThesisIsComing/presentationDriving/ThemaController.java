package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.access.prepost.PreAuthorize;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerThemenBeziehungService;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;

import java.util.UUID;

@Controller
public class ThemaController {

    private final ThemaService service;
    private final BetreuerService betreuerService;
    private final BetreuerThemenBeziehungService beziehungService;

    public ThemaController(ThemaService service,
                           BetreuerService betreuerService,
                           BetreuerThemenBeziehungService beziehungService) {
        this.service = service;
        this.betreuerService = betreuerService;
        this.beziehungService = beziehungService;
    }

    @GetMapping("/themen")
    public String getAllThemen(Model model) {
        var themen = service.listAll();
        if (themen.isEmpty()) {
            throw new wiegand.luca.ThesisIsComing.presentationDriving.exception.ThemaListIsEmpty(
                    "Keine Themen gefunden"
            );
        }
        model.addAttribute("themen", themen);
        return "thema/list";
    }

    @GetMapping("/themen/{id}")
    public String getThemaById(@PathVariable UUID id, Model model) {
        model.addAttribute("thema", service.getById(id));
        model.addAttribute("betreuer", service.findBetreuerByThemaIdOrderByName(id));
        model.addAttribute("allBetreuer", betreuerService.listAll());
        return "thema/detail";
    }

    @PostMapping("/themen/{id}/betreuer")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public RedirectView assignBetreuer(@PathVariable UUID id, @RequestParam UUID betreuerId) {
        beziehungService.assignBetreuerToThema(id, betreuerId);
        return new RedirectView("/themen/" + id);
    }
}
