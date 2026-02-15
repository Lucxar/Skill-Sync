package wiegand.luca.ThesisIsComing.presentationDriving;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.DateiSpeicher;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Dateityp;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
public class DateiController {

    private final BetreuerService betreuerService;
    private final DateiSpeicher dateiSpeicher;

    public DateiController(BetreuerService betreuerService, DateiSpeicher dateiSpeicher) {
        this.betreuerService = betreuerService;
        this.dateiSpeicher = dateiSpeicher;
    }

    @GetMapping("/dateien/{betreuerId}/{dateiname}/download")
    public ResponseEntity<byte[]> downloadDatei(@PathVariable UUID betreuerId,
                                                @PathVariable String dateiname) {
        Informationsdatei datei = betreuerService.findDatei(betreuerId, dateiname);
        byte[] inhalt = dateiSpeicher.lade(dateiname);
        MediaType contentType = resolveContentType(datei.getDateityp());
        String downloadName = datei.getOriginalDateiname() != null
                ? datei.getOriginalDateiname()
                : dateiname;
        ContentDisposition disposition = ContentDisposition.attachment().filename(downloadName).build();
        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(inhalt);
    }

    @GetMapping("/dateien/{betreuerId}/{dateiname}/markdown")
    public String renderMarkdown(@PathVariable UUID betreuerId,
                                 @PathVariable String dateiname,
                                 Model model) {
        Informationsdatei datei = betreuerService.findDatei(betreuerId, dateiname);
        if (datei.getDateityp() != Dateityp.MARKDOWN) {
            throw new DateiNotFoundException("Datei wurde nicht gefunden");
        }
        byte[] inhalt = dateiSpeicher.lade(dateiname);
        String markdown = new String(inhalt, StandardCharsets.UTF_8);
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);
        String safeHtml = Jsoup.parse(html).select("script").remove().body().html();
        model.addAttribute("datei", datei);
        model.addAttribute("contentHtml", safeHtml);
        return "datei/markdown";
    }

    private static MediaType resolveContentType(Dateityp dateityp) {
        if (dateityp == Dateityp.PDF) {
            return MediaType.APPLICATION_PDF;
        }
        if (dateityp == Dateityp.ZIP) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        return MediaType.TEXT_PLAIN;
    }
}
