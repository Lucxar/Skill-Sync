package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

public class Kurs {

        private String Kursbezeichnung;
        private Fachgebiet fachgebietDesKurses;

        public Kurs(String kursbezeichnung,  Fachgebiet fachgebietDesKurses) {
            Kursbezeichnung = kursbezeichnung;
            this.fachgebietDesKurses = fachgebietDesKurses;
        }

        // --- Getter und Setter ---
        public String getKursbezeichnung() {
            return Kursbezeichnung;
        }

        public void setKursbezeichnung(String kursbezeichnung) {
            Kursbezeichnung = kursbezeichnung;
        }

        public Fachgebiet getFachgebietDesKurses() {
            return fachgebietDesKurses;
        }

        public void setFachgebietDesKurses(Fachgebiet fachgebietDesKurses) {
            this.fachgebietDesKurses = fachgebietDesKurses;
        }


}
