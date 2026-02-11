package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;

import java.util.List;
import java.util.UUID;

@Controller
public class BetreuerController {

    private final View view;
    BetreuerService service;

    public BetreuerController(BetreuerService service, View view) {
        this.service = service;
        this.view = view;
    }


    @PostMapping("/upload")
    public String handleFileUpload() {

        return "not implemented yet";
    }

    @GetMapping("/betreuer")
    public void getAllBetreuer(Model model) {
        List<Betreuer> list = service.listAll();
        model.addAttribute("betreuerListe", list);
        return "/betreuer/list";
    }


    @GetMapping("/betreuer/{id}")
    public  void getBetreuerById(@PathVariable UUID id, Model model) {
        Betreuer betreuer = service.getById(id);
        model.addAttribute("betreuer", betreuer);
        return "/betreuer/detail";
    }





}
