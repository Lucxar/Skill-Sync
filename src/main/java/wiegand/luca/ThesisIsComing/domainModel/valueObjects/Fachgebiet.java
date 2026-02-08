package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

public class Fachgebiet {

    private String bezeichnung;

    public Fachgebiet(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    // Getter und Setter
    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

}
