package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.BetreuerListIsEmpty;

import java.util.UUID;

@Controller
public class BetreuerController {

    private final BetreuerService service;

    public BetreuerController(BetreuerService service) {
        this.service = service;
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
        Betreuer betreuer = service.getById(id);
        model.addAttribute("betreuer", betreuer);
        return "betreuer/detail";
    }





}
