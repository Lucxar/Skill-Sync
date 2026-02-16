package wiegand.luca.ThesisIsComing.infrastructureDriven;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.ApplicationService.RoleAssignment;
import wiegand.luca.ThesisIsComing.ApplicationService.RoleAssignmentRepository;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.UserRoleAssignmentDto;

@Repository
public class RoleAssignmentRepositoryImplementation implements RoleAssignmentRepository {
  private final UserRoleAssignmentDataRepository dataRepository;

  public RoleAssignmentRepositoryImplementation(UserRoleAssignmentDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  @Override
  public List<RoleAssignment> findAll() {
    List<RoleAssignment> result = new ArrayList<>();
    for (UserRoleAssignmentDto dto : dataRepository.findAll()) {
      result.add(new RoleAssignment(dto.githubLogin(), dto.role()));
    }
    return result;
  }

  @Override
  public List<RoleAssignment> findByGithubLogin(String githubLogin) {
    List<RoleAssignment> result = new ArrayList<>();
    for (UserRoleAssignmentDto dto : dataRepository.findByGithubLogin(githubLogin)) {
      result.add(new RoleAssignment(dto.githubLogin(), dto.role()));
    }
    return result;
  }

  @Override
  public boolean exists(String githubLogin, String role) {
    return dataRepository.existsByGithubLoginAndRole(githubLogin, role);
  }

  @Override
  public void save(RoleAssignment assignment) {
    dataRepository.save(new UserRoleAssignmentDto(null, assignment.githubLogin(), assignment.role()));
  }

  @Override
  public void delete(String githubLogin, String role) {
    dataRepository.deleteByGithubLoginAndRole(githubLogin, role);
  }
}
