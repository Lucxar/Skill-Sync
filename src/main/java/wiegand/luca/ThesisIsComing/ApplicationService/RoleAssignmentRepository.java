package wiegand.luca.ThesisIsComing.ApplicationService;

import java.util.List;

public interface RoleAssignmentRepository {
  List<RoleAssignment> findAll();

  List<RoleAssignment> findByGithubLogin(String githubLogin);

  boolean exists(String githubLogin, String role);

  void save(RoleAssignment assignment);

  void delete(String githubLogin, String role);
}
