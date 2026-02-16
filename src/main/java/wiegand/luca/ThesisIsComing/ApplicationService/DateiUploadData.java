package wiegand.luca.ThesisIsComing.ApplicationService;

public record DateiUploadData(
    String titel,
    String beschreibung,
    String originalDateiname,
    long groesseInBytes,
    byte[] inhalt) {}
