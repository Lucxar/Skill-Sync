package wiegand.luca.ThesisIsComing.presentationDriving;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.DateiSpeicher;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DateiController.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfiguration.class)
@ActiveProfiles("test")
class DateiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BetreuerService betreuerService;

    @MockitoBean
    private DateiSpeicher dateiSpeicher;

    @Test
    @DisplayName("GET /dateien/{betreuerId}/{dateiname}/download returns attachment")
    void download_datei_returns_attachment() throws Exception {
        UUID betreuerId = UUID.randomUUID();
        Informationsdatei datei = new Informationsdatei(
                "Dokument",
                null,
                "anna",
                LocalDateTime.now(),
                "stored.pdf",
                Dateityp.PDF,
                "info.pdf",
                7L
        );
        when(betreuerService.findDatei(betreuerId, "stored.pdf")).thenReturn(datei);
        when(dateiSpeicher.lade("stored.pdf")).thenReturn("content".getBytes(StandardCharsets.UTF_8));

        mvc.perform(get("/dateien/{betreuerId}/{dateiname}/download", betreuerId, "stored.pdf")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("info.pdf")));
    }

    @Test
    @DisplayName("GET /dateien/{betreuerId}/{dateiname}/markdown renders sanitized HTML")
    void render_markdown_sanitizes_script() throws Exception {
        UUID betreuerId = UUID.randomUUID();
        Informationsdatei datei = new Informationsdatei(
                "Dokument",
                null,
                "anna",
                LocalDateTime.now(),
                "stored.md",
                Dateityp.MARKDOWN,
                "info.md",
                7L
        );
        when(betreuerService.findDatei(betreuerId, "stored.md")).thenReturn(datei);
        when(dateiSpeicher.lade("stored.md")).thenReturn("Hallo<script>alert(1)</script>".getBytes(StandardCharsets.UTF_8));

        mvc.perform(get("/dateien/{betreuerId}/{dateiname}/markdown", betreuerId, "stored.md")
                        .with(SecurityMockMvcRequestPostProcessors.user("student").roles("STUDIERENDE")))
                .andExpect(status().isOk())
                .andExpect(view().name("datei/markdown"))
                .andExpect(content().string(containsString("Hallo")))
                .andExpect(content().string(not(containsString("script"))));
    }
}
