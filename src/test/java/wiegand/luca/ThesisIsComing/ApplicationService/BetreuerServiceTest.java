package wiegand.luca.ThesisIsComing.ApplicationService;

import org.junit.jupiter.api.Test;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetreuerServiceTest {

    private static class InMemoryBetreuerRepository implements BetreuerRepository {
        private final Map<UUID, Betreuer> store = new HashMap<>();

        @Override
        public Optional<Betreuer> findById(UUID id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Iterable<Betreuer> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public Betreuer save(Betreuer betreuer) {
            store.put(betreuer.getId(), betreuer);
            return betreuer;
        }
    }

    @Test
    void listAll_returns_all_betreuer() {
        InMemoryBetreuerRepository repo = new InMemoryBetreuerRepository();
        BetreuerService service = new BetreuerService(repo);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        repo.save(new Betreuer(id1, "Anna"));
        repo.save(new Betreuer(id2, "Max"));

        List<Betreuer> result = service.listAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void getById_throws_when_missing() {
        BetreuerService service = new BetreuerService(new InMemoryBetreuerRepository());

        assertThatThrownBy(() -> service.getById(UUID.randomUUID()))
                .isInstanceOf(BetreuerNotFoundException.class);
    }
}
