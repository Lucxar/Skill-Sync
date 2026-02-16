package wiegand.luca.ThesisIsComing.presentationDriving;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerThemenBeziehungService;
import wiegand.luca.ThesisIsComing.ApplicationService.DateiReplaceData;
import wiegand.luca.ThesisIsComing.ApplicationService.DateiUploadData;
import wiegand.luca.ThesisIsComing.ApplicationService.SucheForm;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiValidationException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.OptimisticLockingException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.exception.LinkNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.BetreuerListIsEmpty;

@Controller
public class BetreuerController {

  private final BetreuerService service;
  private final BetreuerThemenBeziehungService beziehungService;
  private final ThemaService themaService;

  public BetreuerController(
      BetreuerService service,
      BetreuerThemenBeziehungService beziehungService,
      ThemaService themaService) {
    this.service = service;
    this.beziehungService = beziehungService;
    this.themaService = themaService;
  }

  @GetMapping("/betreuer")
  public String getAllBetreuer(@ModelAttribute("sucheForm") SucheForm sucheForm, Model model) {
    SucheForm criteria = sucheForm == null ? new SucheForm(null, null) : sucheForm;
    var betreuerListe = service.searchBetreuer(criteria);
    if (betreuerListe.isEmpty() && criteria.isEmpty()) {
      throw new BetreuerListIsEmpty("Keine Betreuer gefunden");
    }
    model.addAttribute("betreuer", betreuerListe);
    model.addAttribute("sucheForm", criteria);
    return "betreuer/list";
  }

  @GetMapping("/betreuer/{id}")
  public String getBetreuerById(@PathVariable UUID id, Model model) {
    populateDetailModel(id, model);
    return "betreuer/detail";
  }

  @GetMapping("/betreuer/neu")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String showCreateForm(Model model) {
    model.addAttribute("betreuerForm", new BetreuerForm("", null, null, "", null));
    populateFormModel(model, null, "Betreuer anlegen", "Anlegen");
    return "betreuer/form";
  }

  @PostMapping("/betreuer")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String createBetreuer(@Valid BetreuerForm form, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      populateFormModel(model, null, "Betreuer anlegen", "Anlegen");
      return "betreuer/form";
    }
    try {
      var created =
          service.createBetreuer(
              form.name(), form.email(), form.matrikelnummer(), form.toFachgebiete());
      return "redirect:/betreuer/" + created.getId();
    } catch (BetreuerValidationException ex) {
      bindingResult.rejectValue("name", "betreuer.name.invalid", ex.getMessage());
      populateFormModel(model, null, "Betreuer anlegen", "Anlegen");
      return "betreuer/form";
    }
  }

  @GetMapping("/betreuer/{id}/bearbeiten")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String showEditForm(@PathVariable UUID id, Model model) {
    var betreuer = service.getById(id);
    var kontakt = betreuer.getKontaktinformationen();
    String email = kontakt != null ? kontakt.getEmail() : null;
    String matrikelnummer = kontakt != null ? kontakt.getMatrikelnummer() : null;
    String fachgebiete = formatFachgebiete(betreuer.getFachgebiete());
    model.addAttribute(
        "betreuerForm",
        new BetreuerForm(
            betreuer.getName(), email, matrikelnummer, fachgebiete, betreuer.getVersion()));
    populateFormModel(model, id, "Betreuer bearbeiten", "Speichern");
    return "betreuer/form";
  }

  @PostMapping("/betreuer/{id}/bearbeiten")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String updateBetreuer(
      @PathVariable UUID id, @Valid BetreuerForm form, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      populateFormModel(model, id, "Betreuer bearbeiten", "Speichern");
      return "betreuer/form";
    }
    try {
      service.updateBetreuer(
          id,
          form.name(),
          form.email(),
          form.matrikelnummer(),
          form.toFachgebiete(),
          form.version());
      return "redirect:/betreuer/" + id;
    } catch (BetreuerValidationException ex) {
      bindingResult.rejectValue("name", "betreuer.name.invalid", ex.getMessage());
      populateFormModel(model, id, "Betreuer bearbeiten", "Speichern");
      return "betreuer/form";
    } catch (OptimisticLockingException ex) {
      bindingResult.reject("betreuer.concurrent", ex.getMessage());
      populateFormModel(model, id, "Betreuer bearbeiten", "Speichern");
      return "betreuer/form";
    }
  }

  @PostMapping("/betreuer/{id}/loeschen")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String deleteBetreuer(@PathVariable UUID id) {
    service.deleteBetreuer(id);
    return "redirect:/betreuer";
  }

  @PostMapping("/betreuer/{id}/themen")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public RedirectView assignThema(@PathVariable UUID id, @RequestParam UUID themaId) {
    beziehungService.assignThemaToBetreuer(id, themaId);
    return new RedirectView("/betreuer/" + id);
  }

  @PostMapping("/betreuer/{id}/links")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
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
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
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
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
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

  @PostMapping("/betreuer/{id}/dateien")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String uploadDatei(
      @PathVariable UUID id,
      @Valid DateiUploadForm form,
      BindingResult bindingResult,
      Model model) {
    if (form.datei() == null || form.datei().isEmpty()) {
      bindingResult.rejectValue("datei", "datei.empty", "Datei fehlt");
    }
    if (bindingResult.hasErrors()) {
      populateDetailModel(id, model);
      return "betreuer/detail";
    }
    try {
      DateiUploadData data =
          new DateiUploadData(
              form.titel(),
              form.beschreibung(),
              form.datei().getOriginalFilename(),
              form.datei().getSize(),
              form.datei().getBytes());
      service.uploadDatei(id, data);
      return "redirect:/betreuer/" + id;
    } catch (DateiValidationException ex) {
      populateDetailModel(id, model);
      model.addAttribute("dateiError", ex.getMessage());
      return "betreuer/detail";
    } catch (IOException ex) {
      populateDetailModel(id, model);
      model.addAttribute("dateiError", "Datei konnte nicht gelesen werden");
      return "betreuer/detail";
    }
  }

  @PostMapping("/betreuer/{id}/dateien/{dateiname}/metadaten")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String updateDateiMetadaten(
      @PathVariable UUID id,
      @PathVariable String dateiname,
      @Valid DateiMetadatenForm form,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      populateDetailModel(id, model);
      model.addAttribute("dateiError", bindingResult.getAllErrors().get(0).getDefaultMessage());
      return "betreuer/detail";
    }
    try {
      service.updateDateiMetadaten(id, dateiname, form.titel(), form.beschreibung());
      return "redirect:/betreuer/" + id;
    } catch (DateiValidationException ex) {
      populateDetailModel(id, model);
      model.addAttribute("dateiError", ex.getMessage());
      return "betreuer/detail";
    }
  }

  @PostMapping("/betreuer/{id}/dateien/{dateiname}/ersetzen")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String replaceDatei(
      @PathVariable UUID id,
      @PathVariable String dateiname,
      @Valid DateiReplaceForm form,
      BindingResult bindingResult,
      Model model) {
    if (form.datei() == null || form.datei().isEmpty()) {
      bindingResult.rejectValue("datei", "datei.empty", "Datei fehlt");
    }
    if (bindingResult.hasErrors()) {
      populateDetailModel(id, model);
      model.addAttribute("dateiError", bindingResult.getAllErrors().get(0).getDefaultMessage());
      return "betreuer/detail";
    }
    try {
      DateiReplaceData data =
          new DateiReplaceData(
              form.datei().getOriginalFilename(),
              form.datei().getSize(),
              form.datei().getBytes());
      service.replaceDatei(id, dateiname, data);
      return "redirect:/betreuer/" + id;
    } catch (DateiValidationException ex) {
      populateDetailModel(id, model);
      model.addAttribute("dateiError", ex.getMessage());
      return "betreuer/detail";
    } catch (IOException ex) {
      populateDetailModel(id, model);
      model.addAttribute("dateiError", "Datei konnte nicht gelesen werden");
      return "betreuer/detail";
    }
  }

  @PostMapping("/betreuer/{id}/dateien/{dateiname}/loeschen")
  @PreAuthorize("hasAnyRole('ADMIN','BETREUER','ORGANISATOR')")
  public String deleteDatei(@PathVariable UUID id, @PathVariable String dateiname) {
    service.deleteDatei(id, dateiname);
    return "redirect:/betreuer/" + id;
  }

  private void populateDetailModel(UUID id, Model model) {
    Betreuer betreuer = service.getById(id);
    model.addAttribute("betreuer", betreuer);
    model.addAttribute("themen", service.findThemenByBetreuerIdOrderByTitel(id));
    model.addAttribute("allThemen", themaService.listAll());
    if (!model.containsAttribute("dateiUploadForm")) {
      model.addAttribute("dateiUploadForm", new DateiUploadForm("", null, null));
    }
  }

  private static String formatFachgebiete(Set<Fachgebiet> fachgebiete) {
    if (fachgebiete == null || fachgebiete.isEmpty()) {
      return "";
    }
    return fachgebiete.stream()
        .map(Fachgebiet::getBezeichnung)
        .sorted(String.CASE_INSENSITIVE_ORDER)
        .collect(Collectors.joining("\n"));
  }

  private void populateFormModel(Model model, UUID id, String pageTitle, String submitLabel) {
    String formAction = id == null ? "/betreuer" : "/betreuer/" + id + "/bearbeiten";
    model.addAttribute("formAction", formAction);
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("submitLabel", submitLabel);
  }
}
