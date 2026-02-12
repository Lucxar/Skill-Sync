package wiegand.luca.ThesisIsComing.ApplicationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;

import java.util.List;
import java.util.Optional;
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
        when(repo.findAll()).thenReturn(List.of(
                new Betreuer(id1, "Anna"),
                new Betreuer(id2, "Max")
        ));

        List<Betreuer> result = service.listAll();

        assertThat(result).hasSize(2);
        verify(repo).findAll();
    }

    @Test
    void getById_throws_when_missing() {
        when(repo.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(UUID.randomUUID()))
                .isInstanceOf(BetreuerNotFoundException.class);
        verify(repo).findById(any(UUID.class));
    }
}
