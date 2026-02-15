package wiegand.luca.ThesisIsComing.ApplicationService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaDeleteNotAllowedException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaValidationException;
import wiegand.luca.ThesisIsComing.domainModel.Thema;
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThemaServiceTest {

    @Mock
    private ThemaRepository repo;

    private ThemaService service;

    @BeforeEach
    void setup() {
        service = new ThemaService(repo);
    }


    @Test
    @DisplayName("listAll returns all Themen")
    void listAll_returns_all_themen() {

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Thema t1 = new Thema(id1, "Thema 1");
        Thema t2 = new Thema(id2, "Thema 2");

        when(repo.findAll()).thenReturn(List.of(t1, t2));

        List<Thema> result = service.listAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(t1, t2);
        verify(repo).findAll();
    }

    @Test
    @DisplayName("searchThemen returns all when no filters")
    void searchThemen_returns_all_when_no_filters() {
        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(repo.findAll()).thenReturn(List.of(t1));

        List<Thema> result = service.searchThemen(new SucheForm(null, null));

        assertThat(result).containsExactly(t1);
        verify(repo).findAll();
    }

    @Test
    @DisplayName("searchThemen filters by titel")
    void searchThemen_filters_by_titel() {
        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(repo.findByTitelContainsIgnoreCase("Thema")).thenReturn(List.of(t1));

        List<Thema> result = service.searchThemen(new SucheForm("Thema", null));

        assertThat(result).containsExactly(t1);
        verify(repo).findByTitelContainsIgnoreCase("Thema");
    }

    @Test
    @DisplayName("searchThemen filters by fachgebiet")
    void searchThemen_filters_by_fachgebiet() {
        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(repo.findByFachgebiet("AI")).thenReturn(List.of(t1));

        List<Thema> result = service.searchThemen(new SucheForm(null, "AI"));

        assertThat(result).containsExactly(t1);
        verify(repo).findByFachgebiet("AI");
    }

    @Test
    @DisplayName("searchThemen filters by titel and fachgebiet")
    void searchThemen_filters_by_titel_and_fachgebiet() {
        Thema t1 = new Thema(UUID.randomUUID(), "Thema 1");
        when(repo.findByTitelContainsIgnoreCaseAndFachgebiet("Thema", "AI")).thenReturn(List.of(t1));

        List<Thema> result = service.searchThemen(new SucheForm("Thema", "AI"));

        assertThat(result).containsExactly(t1);
        verify(repo).findByTitelContainsIgnoreCaseAndFachgebiet("Thema", "AI");
    }

    @Test
    @DisplayName("getById returns the correct Thema")
    void getById_returns_correct_Thema() {

        UUID id1 = UUID.randomUUID();
        Thema t1 = new Thema(id1, "Thema 1");

        when(repo.findById(any(UUID.class))).thenReturn(Optional.of(t1));

        Thema result = service.getById(id1);

        assertThat(result.getTitel()).isEqualTo("Thema 1");
        assertThat(result.getId()).isEqualTo(id1);
        verify(repo).findById(id1);
    }

    @Test
    @DisplayName("getById throws when Thema is missing")
    void getById_throws_when_missing() {
        when(repo.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(UUID.randomUUID()))
                .isInstanceOf(ThemaNotFoundException.class);
        verify(repo).findById(any(UUID.class));
    }

    @Test
    @DisplayName("addLink saves updated Thema")
    void addLink_saves_updated_thema() {
        UUID id = UUID.randomUUID();
        Thema thema = new Thema(id, "Thema 1");
        when(repo.findById(id)).thenReturn(Optional.of(thema));
        when(repo.save(any(Thema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addLink(id, "https://example.com", "Beispiel");

        ArgumentCaptor<Thema> captor = ArgumentCaptor.forClass(Thema.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getLinks())
                .containsExactly(new Link("https://example.com", "Beispiel"));
    }

    @Test
    @DisplayName("updateLink replaces existing Link")
    void updateLink_replaces_existing_link() {
        UUID id = UUID.randomUUID();
        Link oldLink = new Link("https://old", "Alt");
        Thema thema = new Thema(id, "Thema 1", null, Set.of(), Set.of(), Set.of(), Set.of(oldLink), Set.of());
        when(repo.findById(id)).thenReturn(Optional.of(thema));
        when(repo.save(any(Thema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateLink(id, "https://old", "Alt", "https://neu", "Neu");

        ArgumentCaptor<Thema> captor = ArgumentCaptor.forClass(Thema.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getLinks())
                .containsExactly(new Link("https://neu", "Neu"));
    }

    @Test
    @DisplayName("addLink throws when invalid")
    void addLink_throws_when_invalid() {
        UUID id = UUID.randomUUID();
        Thema thema = new Thema(id, "Thema 1");
        when(repo.findById(id)).thenReturn(Optional.of(thema));

        assertThatThrownBy(() -> service.addLink(id, " ", ""))
                .isInstanceOf(InvalidLinkException.class);
    }

    @Test
    @DisplayName("createThema saves and returns created Thema")
    void createThema_saves_and_returns_created_thema() {
        when(repo.save(any(Thema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Thema created = service.createThema("Neues Thema", "Beschreibung");

        ArgumentCaptor<Thema> captor = ArgumentCaptor.forClass(Thema.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getTitel()).isEqualTo("Neues Thema");
        assertThat(created.getTitel()).isEqualTo("Neues Thema");
    }

    @Test
    @DisplayName("createThema throws when title is blank")
    void createThema_throws_when_title_blank() {
        assertThatThrownBy(() -> service.createThema(" ", null))
                .isInstanceOf(ThemaValidationException.class);
        verify(repo, never()).save(any(Thema.class));
    }

    @Test
    @DisplayName("updateThema updates titel and description")
    void updateThema_updates_details() {
        UUID id = UUID.randomUUID();
        Thema thema = new Thema(id, "Alt", "Alt", Set.of(), Set.of(), Set.of(), Set.of(), Set.of());
        when(repo.findById(id)).thenReturn(Optional.of(thema));
        when(repo.save(any(Thema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Thema updated = service.updateThema(id, "Neu", "Neu Beschreibung");

        assertThat(updated.getTitel()).isEqualTo("Neu");
        assertThat(updated.getBeschreibungMarkdown()).isEqualTo("Neu Beschreibung");
    }

    @Test
    @DisplayName("updateThema throws when title is blank")
    void updateThema_throws_when_title_blank() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(new Thema(id, "Alt")));

        assertThatThrownBy(() -> service.updateThema(id, " ", null))
                .isInstanceOf(ThemaValidationException.class);
        verify(repo, never()).save(any(Thema.class));
    }

    @Test
    @DisplayName("deleteThema deletes when no Betreuer assigned")
    void deleteThema_deletes_when_allowed() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(new Thema(id, "Thema")));
        when(repo.findBetreuerIdsByThemaId(id)).thenReturn(List.of());

        service.deleteThema(id);

        verify(repo).deleteById(id);
    }

    @Test
    @DisplayName("deleteThema throws when Betreuer assigned")
    void deleteThema_throws_when_betreuer_assigned() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(new Thema(id, "Thema")));
        when(repo.findBetreuerIdsByThemaId(id)).thenReturn(List.of(UUID.randomUUID()));

        assertThatThrownBy(() -> service.deleteThema(id))
                .isInstanceOf(ThemaDeleteNotAllowedException.class);
        verify(repo, never()).deleteById(id);
    }

}
