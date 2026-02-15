package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerThemenBeziehungService;
import wiegand.luca.ThesisIsComing.ApplicationService.SucheForm;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaDeleteNotAllowedException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaValidationException;
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
    public String getAllThemen(@ModelAttribute("sucheForm") SucheForm sucheForm, Model model) {
        SucheForm criteria = sucheForm == null ? new SucheForm(null, null) : sucheForm;
        var themen = service.searchThemen(criteria);
        if (themen.isEmpty() && criteria.isEmpty()) {
            throw new wiegand.luca.ThesisIsComing.presentationDriving.exception.ThemaListIsEmpty(
                    "Keine Themen gefunden"
            );
        }
        model.addAttribute("themen", themen);
        model.addAttribute("sucheForm", criteria);
        return "thema/list";
    }

    @GetMapping("/themen/{id}")
    public String getThemaById(@PathVariable UUID id, Model model) {
        populateDetailModel(id, model);
        return "thema/detail";
    }

    @GetMapping("/themen/neu")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String showCreateForm(Model model) {
        model.addAttribute("themaForm", new ThemaForm("", ""));
        populateFormModel(model, null, "Thema anlegen", "Anlegen");
        return "thema/form";
    }

    @PostMapping("/themen")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String createThema(@Valid ThemaForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, null, "Thema anlegen", "Anlegen");
            return "thema/form";
        }
        try {
            var created = service.createThema(form.titel(), form.beschreibungMarkdown());
            return "redirect:/themen/" + created.getId();
        } catch (ThemaValidationException ex) {
            bindingResult.rejectValue("titel", "thema.titel.invalid", ex.getMessage());
            populateFormModel(model, null, "Thema anlegen", "Anlegen");
            return "thema/form";
        }
    }

    @GetMapping("/themen/{id}/bearbeiten")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String showEditForm(@PathVariable UUID id, Model model) {
        var thema = service.getById(id);
        model.addAttribute("themaForm", new ThemaForm(thema.getTitel(), thema.getBeschreibungMarkdown()));
        populateFormModel(model, id, "Thema bearbeiten", "Speichern");
        return "thema/form";
    }

    @PostMapping("/themen/{id}/bearbeiten")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String updateThema(@PathVariable UUID id,
                              @Valid ThemaForm form,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, id, "Thema bearbeiten", "Speichern");
            return "thema/form";
        }
        try {
            service.updateThema(id, form.titel(), form.beschreibungMarkdown());
            return "redirect:/themen/" + id;
        } catch (ThemaValidationException ex) {
            bindingResult.rejectValue("titel", "thema.titel.invalid", ex.getMessage());
            populateFormModel(model, id, "Thema bearbeiten", "Speichern");
            return "thema/form";
        }
    }

    @PostMapping("/themen/{id}/loeschen")
    @PreAuthorize("hasAnyRole('ADMIN','BETREUER')")
    public String deleteThema(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteThema(id);
            return "redirect:/themen";
        } catch (ThemaDeleteNotAllowedException ex) {
            redirectAttributes.addFlashAttribute("themaError", ex.getMessage());
            return "redirect:/themen/" + id;
        }
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

    private void populateFormModel(Model model, UUID id, String pageTitle, String submitLabel) {
        String formAction = id == null ? "/themen" : "/themen/" + id + "/bearbeiten";
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("submitLabel", submitLabel);
    }
}
