package wiegand.luca.ThesisIsComing.presentationDriving.exception;

public class BetreuerListIsEmpty extends RuntimeException {
  public BetreuerListIsEmpty(String message) {
    super(
        message == null || message.isBlank()
            ? "Betreuer Liste kann nicht angezeigt werden"
            : message);
  }
}
