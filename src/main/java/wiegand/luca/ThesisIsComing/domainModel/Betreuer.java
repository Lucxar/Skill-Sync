package wiegand.luca.ThesisIsComing.domainModel;

import org.springframework.data.annotation.Id;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Informationsdatei;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Link;

import java.util.HashSet;
import java.util.UUID;

public class Betreuer {

    @Id
    private UUID id;

    private String name;
    Kontaktinformationen kontaktinformationen;
    private HashSet<Fachgebiet> fachgebiete;
    private HashSet<Informationsdatei> dateien;
    private HashSet<Link> links;


    public Betreuer (String name, Kontaktinformationen kontaktinformationen) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.kontaktinformationen = kontaktinformationen;
        this.fachgebiete = new HashSet<>();
        this.dateien = new HashSet<>();
        this.links = new HashSet<>();
    }



}
