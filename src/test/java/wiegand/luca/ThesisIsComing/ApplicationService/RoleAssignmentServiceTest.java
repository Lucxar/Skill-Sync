package wiegand.luca.ThesisIsComing.ApplicationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.RoleAssignmentException;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleAssignmentServiceTest {

  @Mock private RoleAssignmentRepository repository;

  @Test
  @DisplayName("assignRole normalizes login and role")
  void assignRole_normalizes_login_and_role() {
    RoleAssignmentService service = new RoleAssignmentService(repository);
    when(repository.exists("login", SecurityRoles.ROLE_ORGANISATOR)).thenReturn(false);

    service.assignRole(" Login ", "organisator");

    verify(repository).save(new RoleAssignment("login", SecurityRoles.ROLE_ORGANISATOR));
  }

  @Test
  @DisplayName("assignRole throws when role exists")
  void assignRole_throws_when_role_exists() {
    RoleAssignmentService service = new RoleAssignmentService(repository);
    when(repository.exists("login", SecurityRoles.ROLE_KORREKTOR)).thenReturn(true);

    assertThatThrownBy(() -> service.assignRole("login", "KORREKTOR"))
        .isInstanceOf(RoleAssignmentException.class);
  }

  @Test
  @DisplayName("removeRole throws when missing")
  void removeRole_throws_when_missing() {
    RoleAssignmentService service = new RoleAssignmentService(repository);
    when(repository.exists("login", SecurityRoles.ROLE_KORREKTOR)).thenReturn(false);

    assertThatThrownBy(() -> service.removeRole("login", "ROLE_KORREKTOR"))
        .isInstanceOf(RoleAssignmentException.class);
  }

  @Test
  @DisplayName("rolesForLogin returns assigned roles")
  void rolesForLogin_returns_assigned_roles() {
    RoleAssignmentService service = new RoleAssignmentService(repository);
    when(repository.findByGithubLogin("login"))
        .thenReturn(List.of(new RoleAssignment("login", SecurityRoles.ROLE_ORGANISATOR)));

    Set<String> roles = service.rolesForLogin("login");

    assertThat(roles).containsExactly(SecurityRoles.ROLE_ORGANISATOR);
  }
}
