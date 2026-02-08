package wiegand.luca.ThesisIsComing.domainModel;

import org.springframework.data.annotation.Id;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.BestandenerKurs;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Fachgebiet;
import wiegand.luca.ThesisIsComing.domainModel.valueObjects.Kontaktinformationen;

import java.util.HashSet;
import java.util.UUID;

public class Student {

    @Id
    private UUID id;

    private String name;
    Kontaktinformationen kontaktinformationen;
    private HashSet<BestandenerKurs> bestandeneKurse;
    private HashSet<Fachgebiet> interessensgebiete;

    public Student (String name, Kontaktinformationen kontaktinformationen) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.kontaktinformationen = kontaktinformationen;
        this.bestandeneKurse = new HashSet<>();
        this.interessensgebiete = new HashSet<>();
    }


}
