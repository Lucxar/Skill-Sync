package wiegand.luca.ThesisIsComing.infrastructureDriven.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_role_assignment")
public record UserRoleAssignmentDto(@Id Long id, String githubLogin, String role) {}
