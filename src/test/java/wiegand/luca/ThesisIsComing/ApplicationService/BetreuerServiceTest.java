package wiegand.luca.ThesisIsComing.ApplicationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;
import wiegand.luca.ThesisIsComing.domainModel.exception.InvalidLinkException;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BetreuerServiceTest {

    @Mock
    private BetreuerRepository repo;

    private BetreuerService service;

    @BeforeEach
    void setup() {
        service = new BetreuerService(repo);
    }

    @Test
    void listAll_returns_all_betreuer() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Betreuer b1 = new Betreuer(id1, "Anna");
        Betreuer b2 = new Betreuer(id2, "Max");
        when(repo.findAll()).thenReturn(List.of(b1, b2));

        List<Betreuer> result = service.listAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(b1, b2);
        verify(repo).findAll();
    }

    @Test
    void getById_throws_when_missing() {
        when(repo.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(UUID.randomUUID()))
                .isInstanceOf(BetreuerNotFoundException.class);
        verify(repo).findById(any(UUID.class));
    }

    @Test
    void addLink_saves_updated_betreuer() {
        UUID id = UUID.randomUUID();
        Betreuer betreuer = new Betreuer(id, "Anna");
        when(repo.findById(id)).thenReturn(Optional.of(betreuer));
        when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addLink(id, "https://example.com", "Beispiel");

        ArgumentCaptor<Betreuer> captor = ArgumentCaptor.forClass(Betreuer.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getLinks())
                .containsExactly(new Link("https://example.com", "Beispiel"));
    }

    @Test
    void updateLink_replaces_existing_link() {
        UUID id = UUID.randomUUID();
        Link oldLink = new Link("https://old", "Alt");
        Betreuer betreuer = new Betreuer(id, "Anna", null, Set.of(), Set.of(), Set.of(oldLink));
        when(repo.findById(id)).thenReturn(Optional.of(betreuer));
        when(repo.save(any(Betreuer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateLink(id, "https://old", "Alt", "https://neu", "Neu");

        ArgumentCaptor<Betreuer> captor = ArgumentCaptor.forClass(Betreuer.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getLinks())
                .containsExactly(new Link("https://neu", "Neu"));
    }

    @Test
    void addLink_throws_when_invalid() {
        UUID id = UUID.randomUUID();
        Betreuer betreuer = new Betreuer(id, "Anna");
        when(repo.findById(id)).thenReturn(Optional.of(betreuer));

        assertThatThrownBy(() -> service.addLink(id, " ", ""))
                .isInstanceOf(InvalidLinkException.class);
    }
}
