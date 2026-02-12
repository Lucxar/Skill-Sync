package wiegand.luca.ThesisIsComing.presentationDriving;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BetreuerController.class)
class BetreuerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BetreuerService service;

    @Test
    @DisplayName("'/betreuer' renders a list of Betreuer into HTML '/betreuer/list'.")
    void list_view_renders_betreuer_model() throws Exception {
        Betreuer betreuer = new Betreuer(UUID.randomUUID(), "Anna");
        when(service.listAll()).thenReturn(List.of(betreuer));

        mvc.perform(get("/betreuer"))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/list"))
                .andExpect(model().attributeExists("betreuer"));
    }

    @Test
    @DisplayName("'/betreuer/{id}' renders a Betreuer into HTML '/betreuer/detail'")
    void find_by_ID_returns_correct_betreuer_model() throws Exception {

        UUID id = UUID.randomUUID();
        Betreuer betreuer = new Betreuer(id, "Tom");
        when(service.getById(id)).thenReturn(betreuer);

        mvc.perform(get("/betreuer/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("betreuer/detail"))
                .andExpect(model().attributeExists("betreuer"));

    }
}
