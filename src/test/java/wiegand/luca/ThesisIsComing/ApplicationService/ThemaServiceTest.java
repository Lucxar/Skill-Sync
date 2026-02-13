package wiegand.luca.ThesisIsComing.ApplicationService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

}
