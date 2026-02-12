package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;

import java.util.UUID;

@Controller
public class ThemaController {

    private final ThemaService service;

    public ThemaController(ThemaService service) {
        this.service = service;
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
        return "thema/detail";
    }
}
