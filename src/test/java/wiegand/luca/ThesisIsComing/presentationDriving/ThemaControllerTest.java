package wiegand.luca.ThesisIsComing.presentationDriving;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wiegand.luca.ThesisIsComing.ApplicationService.ThemaService;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.ThemaListIsEmpty;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ThemaController.class)
public class ThemaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    ThemaService service;


    @Test
    @DisplayName("/themen returns 'thema/list' view")
    void themen_returns_correct_view() throws Exception {

        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(service.listAll()).thenReturn(List.of(t1));

        mvc.perform(get("/themen"))
                .andExpect(status().isOk())
                .andExpect(view().name("thema/list"))
                .andExpect(model().attributeExists("themen"));
    }

    @Test
    @DisplayName("/themen/{id} returns correct Thema")
    void themen_id_returns_correct_thema() throws Exception {

        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(service.getById(any(UUID.class))).thenReturn(t1);

        mvc.perform(get("/themen/{id}", t1.getId())).andExpect(status().isOk())
                .andExpect(view().name("thema/detail"))
                .andExpect(model().attributeExists("thema"));
    }


    @Test
    @DisplayName("/themen returns 404 when list is empty")
    void themen_returns_404_when_list_empty() throws Exception {
        when(service.listAll()).thenReturn(List.of());

        mvc.perform(get("/themen"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    @DisplayName("/themen/{id} returns 404 when Thema not found")
    void themen_id_returns_404_when_not_found() throws Exception {
        when(service.getById(any(UUID.class))).thenThrow(new ThemaNotFoundException(UUID.randomUUID()));

        mvc.perform(get("/themen/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attributeExists("message"));
    }




}
