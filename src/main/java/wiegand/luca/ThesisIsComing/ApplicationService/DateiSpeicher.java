package wiegand.luca.ThesisIsComing.ApplicationService;

public interface DateiSpeicher {
  String speichere(String originalDateiname, byte[] inhalt);

  byte[] lade(String gespeicherterDateiname);

  void loesche(String gespeicherterDateiname);
}
