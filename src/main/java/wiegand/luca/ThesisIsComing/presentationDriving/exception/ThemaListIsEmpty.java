package wiegand.luca.ThesisIsComing.presentationDriving.exception;

public class ThemaListIsEmpty extends RuntimeException {
    public ThemaListIsEmpty(String message) {
        super(message == null || message.isBlank()
                ? "Thema Liste kann nicht angezeigt werden"
                : message);
    }
}
