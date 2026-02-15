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
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.exception.LinkNotFoundException;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.BetreuerListIsEmpty;

import java.util.UUID;

@Controller
public class BetreuerController {

    private final BetreuerService service;
    private final BetreuerThemenBeziehungService beziehungService;
    private final ThemaService themaService;

    public BetreuerController(BetreuerService service,
                              BetreuerThemenBeziehungService beziehungService,
                              ThemaService themaService) {
        this.service = service;
        this.beziehungService = beziehungService;
        this.themaService = themaService;
    }


    @PostMapping("/upload")
    public String handleFileUpload() {
        return "not implemented yet";
    }

    @GetMapping("/betreuer")
    public String getAllBetreuer(Model model) {
        var betreuerListe = service.listAll();
        if (betreuerListe.isEmpty()) {
            throw new BetreuerListIsEmpty(
                    "Keine Betreuer gefunden"
            );
        }
        model.addAttribute("betreuer", betreuerListe);
        return "betreuer/list";
    }


    @GetMapping("/betreuer/{id}")
    public String getBetreuerById(@PathVariable UUID id, Model model) {
        populateDetailModel(id, model);
        return "betreuer/detail";
    }

    @PostMapping("/betreuer/{id}/themen")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public RedirectView assignThema(@PathVariable UUID id, @RequestParam UUID themaId) {
        beziehungService.assignThemaToBetreuer(id, themaId);
        return new RedirectView("/betreuer/" + id);
    }

    @PostMapping("/betreuer/{id}/links")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String addLink(@PathVariable UUID id, LinkForm form, Model model) {
        try {
            service.addLink(id, form.url(), form.anzeigetext());
            return "redirect:/betreuer/" + id;
        } catch (InvalidLinkException ex) {
            populateDetailModel(id, model);
            model.addAttribute("linkError", ex.getMessage());
            return "betreuer/detail";
        }
    }

    @PostMapping("/betreuer/{id}/links/update")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String updateLink(@PathVariable UUID id, LinkUpdateForm form, Model model) {
        try {
            service.updateLink(id, form.oldUrl(), form.oldAnzeigetext(), form.url(), form.anzeigetext());
            return "redirect:/betreuer/" + id;
        } catch (InvalidLinkException | LinkNotFoundException ex) {
            populateDetailModel(id, model);
            model.addAttribute("linkError", ex.getMessage());
            return "betreuer/detail";
        }
    }

    @PostMapping("/betreuer/{id}/links/delete")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String deleteLink(@PathVariable UUID id, LinkForm form, Model model) {
        try {
            service.removeLink(id, form.url(), form.anzeigetext());
            return "redirect:/betreuer/" + id;
        } catch (InvalidLinkException | LinkNotFoundException ex) {
            populateDetailModel(id, model);
            model.addAttribute("linkError", ex.getMessage());
            return "betreuer/detail";
        }
    }

    private void populateDetailModel(UUID id, Model model) {
        Betreuer betreuer = service.getById(id);
        model.addAttribute("betreuer", betreuer);
        model.addAttribute("themen", service.findThemenByBetreuerIdOrderByTitel(id));
        model.addAttribute("allThemen", themaService.listAll());
    }





}
