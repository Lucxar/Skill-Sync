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
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.exception.LinkNotFoundException;

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
        populateDetailModel(id, model);
        return "thema/detail";
    }

    @PostMapping("/themen/{id}/betreuer")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public RedirectView assignBetreuer(@PathVariable UUID id, @RequestParam UUID betreuerId) {
        beziehungService.assignBetreuerToThema(id, betreuerId);
        return new RedirectView("/themen/" + id);
    }

    @PostMapping("/themen/{id}/links")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String addLink(@PathVariable UUID id, LinkForm form, Model model) {
        try {
            service.addLink(id, form.url(), form.anzeigetext());
            return "redirect:/themen/" + id;
        } catch (InvalidLinkException ex) {
            populateDetailModel(id, model);
            model.addAttribute("linkError", ex.getMessage());
            return "thema/detail";
        }
    }

    @PostMapping("/themen/{id}/links/update")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String updateLink(@PathVariable UUID id, LinkUpdateForm form, Model model) {
        try {
            service.updateLink(id, form.oldUrl(), form.oldAnzeigetext(), form.url(), form.anzeigetext());
            return "redirect:/themen/" + id;
        } catch (InvalidLinkException | LinkNotFoundException ex) {
            populateDetailModel(id, model);
            model.addAttribute("linkError", ex.getMessage());
            return "thema/detail";
        }
    }

    @PostMapping("/themen/{id}/links/delete")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String deleteLink(@PathVariable UUID id, LinkForm form, Model model) {
        try {
            service.removeLink(id, form.url(), form.anzeigetext());
            return "redirect:/themen/" + id;
        } catch (InvalidLinkException | LinkNotFoundException ex) {
            populateDetailModel(id, model);
            model.addAttribute("linkError", ex.getMessage());
            return "thema/detail";
        }
    }

    private void populateDetailModel(UUID id, Model model) {
        model.addAttribute("thema", service.getById(id));
        model.addAttribute("betreuer", service.findBetreuerByThemaIdOrderByName(id));
        model.addAttribute("allBetreuer", betreuerService.listAll());
    }
}
