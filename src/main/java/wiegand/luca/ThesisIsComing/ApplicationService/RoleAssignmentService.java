package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.RoleAssignmentException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;

@Service
public class RoleAssignmentService {
  private final RoleAssignmentRepository repository;

  public RoleAssignmentService(RoleAssignmentRepository repository) {
    this.repository = repository;
  }

  public List<RoleAssignment> listAll() {
    List<RoleAssignment> result = new ArrayList<>(repository.findAll());
    result.sort(
        Comparator.comparing(RoleAssignment::githubLogin, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(RoleAssignment::role));
    return result;
  }

  public Set<String> rolesForLogin(String githubLogin) {
    String login = normalizeLogin(githubLogin);
    if (login == null) {
      return Set.of();
    }
    Set<String> result = new HashSet<>();
    for (RoleAssignment assignment : repository.findByGithubLogin(login)) {
      result.add(assignment.role());
    }
    return result;
  }

  public void assignRole(String githubLogin, String role) {
    String login = normalizeLogin(githubLogin);
    String normalizedRole = normalizeRole(role);
    if (login == null) {
      throw new RoleAssignmentException("GitHub-Login darf nicht leer sein");
    }
    if (repository.exists(login, normalizedRole)) {
      throw new RoleAssignmentException("Rolle ist bereits zugewiesen");
    }
    repository.save(new RoleAssignment(login, normalizedRole));
  }

  public void removeRole(String githubLogin, String role) {
    String login = normalizeLogin(githubLogin);
    String normalizedRole = normalizeRole(role);
    if (login == null) {
      throw new RoleAssignmentException("GitHub-Login darf nicht leer sein");
    }
    if (!repository.exists(login, normalizedRole)) {
      throw new RoleAssignmentException("Rolle ist nicht zugewiesen");
    }
    repository.delete(login, normalizedRole);
  }

  private static String normalizeLogin(String githubLogin) {
    if (githubLogin == null) {
      return null;
    }
    String trimmed = githubLogin.trim();
    return trimmed.isEmpty() ? null : trimmed.toLowerCase();
  }

  private static String normalizeRole(String role) {
    if (role == null || role.trim().isEmpty()) {
      throw new RoleAssignmentException("Rolle darf nicht leer sein");
    }
    String normalized = role.trim().toUpperCase();
    if ("ORGANISATOR".equals(normalized) || SecurityRoles.ROLE_ORGANISATOR.equals(normalized)) {
      return SecurityRoles.ROLE_ORGANISATOR;
    }
    if ("KORREKTOR".equals(normalized) || SecurityRoles.ROLE_KORREKTOR.equals(normalized)) {
      return SecurityRoles.ROLE_KORREKTOR;
    }
    throw new RoleAssignmentException("Unbekannte Rolle: " + role);
  }
}
