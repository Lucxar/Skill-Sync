package wiegand.luca.ThesisIsComing.ApplicationService.exception;

public class OptimisticLockingException extends RuntimeException {

  public OptimisticLockingException(String message) {
    super(message);
  }
}
