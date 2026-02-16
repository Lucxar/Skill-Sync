package wiegand.luca.ThesisIsComing.presentationDriving.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.RoleAssignmentService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, OAuth2UserService<OAuth2UserRequest, OAuth2User> userService)
      throws Exception {
    http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        "/error",
                        "/error/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/oauth2/**",
                        "/login/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(oauth -> oauth.userInfoEndpoint(userInfo -> userInfo.userService(userService)))
        .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

    return http.build();
  }

  @Bean
  public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(
      BetreuerService betreuerService,
      RoleAssignmentService roleAssignmentService,
      SecurityProperties securityProperties) {
    return new GithubOAuth2UserLoader(betreuerService, roleAssignmentService, securityProperties);
  }
}
