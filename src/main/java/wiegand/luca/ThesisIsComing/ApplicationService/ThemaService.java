package wiegand.luca.ThesisIsComing.ApplicationService;

import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Thema;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class ThemaService {

    private ThemaRepository repository;

    public ThemaService(ThemaRepository repository) {
        this.repository = repository;
    }

    public List<Thema> listAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .toList();
    }

    public Thema getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ThemaNotFoundException(id));
    }

}
