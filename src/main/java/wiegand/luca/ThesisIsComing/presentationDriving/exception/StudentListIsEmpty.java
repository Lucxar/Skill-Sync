package wiegand.luca.ThesisIsComing.presentationDriving.exception;

public class StudentListIsEmpty extends RuntimeException {
    public StudentListIsEmpty(String message) {
        super(message == null || message.isBlank()
                ? "Studierendenliste kann nicht angezeigt werden"
                : message);
    }
}
