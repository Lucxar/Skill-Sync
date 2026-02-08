package wiegand.luca.ThesisIsComing.domainModel.valueObjects;

public class Kontaktinformationen {

    private String email;
    private String Matrikelnummer;

    public Kontaktinformationen(String email, String matrikelnummer) {
        this.email = email;
        this.Matrikelnummer = matrikelnummer;
    }


    // --- Getter-Methoden ---

    public String getMatrikelnummer() {
        return Matrikelnummer;
    }

    public String getEmail() {
        return email;
    }


    // --- Setter-Methoden ---

    public void setMatrikelnummer(String matrikelnummer) {
        Matrikelnummer = matrikelnummer;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
