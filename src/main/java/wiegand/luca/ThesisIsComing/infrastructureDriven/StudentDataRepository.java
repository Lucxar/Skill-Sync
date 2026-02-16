package wiegand.luca.ThesisIsComing.infrastructureDriven;

import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wiegand.luca.ThesisIsComing.infrastructureDriven.dto.StudentDto;

@Repository
public interface StudentDataRepository extends CrudRepository<StudentDto, UUID> {

  @Query("SELECT * FROM student WHERE github_login = :githubLogin")
  java.util.Optional<StudentDto> findByGithubLogin(@Param("githubLogin") String githubLogin);
}
