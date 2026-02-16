package wiegand.luca.ThesisIsComing.infrastructureDriven;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.UserRoleAssignmentDto;

@Repository
public interface UserRoleAssignmentDataRepository
    extends CrudRepository<UserRoleAssignmentDto, Long> {
  List<UserRoleAssignmentDto> findByGithubLogin(String githubLogin);

  boolean existsByGithubLoginAndRole(String githubLogin, String role);

  void deleteByGithubLoginAndRole(String githubLogin, String role);
}
