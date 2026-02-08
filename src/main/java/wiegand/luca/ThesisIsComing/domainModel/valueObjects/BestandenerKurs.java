package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

import java.time.LocalDate;
import java.util.Date;

public class BestandenerKurs {

    private String Kursbezeichnung;
    private LocalDate BestandenAm;
    private Fachgebiet fachgebietDesKurses;

    public BestandenerKurs(String kursbezeichnung, LocalDate bestandenAm, Fachgebiet fachgebietDesKurses) {
        Kursbezeichnung = kursbezeichnung;
        BestandenAm = bestandenAm;
        this.fachgebietDesKurses = fachgebietDesKurses;
    }

    // --- Getter und Setter ---
    public String getKursbezeichnung() {
        return Kursbezeichnung;
    }

    public void setKursbezeichnung(String kursbezeichnung) {
        Kursbezeichnung = kursbezeichnung;
    }

    public LocalDate getBestandenAm() {
        return BestandenAm;
    }

    public void setBestandenAm(LocalDate bestandenAm) {
        BestandenAm = bestandenAm;
    }

    public Fachgebiet getFachgebietDesKurses() {
        return fachgebietDesKurses;
    }

    public void setFachgebietDesKurses(Fachgebiet fachgebietDesKurses) {
        this.fachgebietDesKurses = fachgebietDesKurses;
    }

}
