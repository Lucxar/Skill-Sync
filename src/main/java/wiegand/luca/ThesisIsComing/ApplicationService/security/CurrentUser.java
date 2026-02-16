package wiegand.luca.ThesisIsComing.ApplicationService.security;

public interface CurrentUser {
  String login();

  boolean hasRole(String role);
}
