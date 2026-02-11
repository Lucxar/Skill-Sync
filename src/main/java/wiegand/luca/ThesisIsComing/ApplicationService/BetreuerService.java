package wiegand.luca.ThesisIsComing.ApplicationService;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.domainModel.Betreuer;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class BetreuerService {

    private BetreuerRepository betreuerRepository;

    public BetreuerService(BetreuerRepository betreuerRepository) {
        this.betreuerRepository = betreuerRepository;
    }



    public List<Betreuer> listAll() {
        return StreamSupport.stream(betreuerRepository.findAll().spliterator(), false)
                .toList();
    }


    public Betreuer getById(UUID id) {
        return betreuerRepository.findById(id)
                .orElseThrow(() -> new BetreuerNotFoundException(id));
    }

}
