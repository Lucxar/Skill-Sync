package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@TestConfiguration
public class TestOAuth2ClientConfiguration {

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    ClientRegistration registration =
        ClientRegistration.withRegistrationId("github")
            .clientId("test-client")
            .clientSecret("test-secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationUri("https://github.com/login/oauth/authorize")
            .tokenUri("https://github.com/login/oauth/access_token")
            .userInfoUri("https://api.github.com/user")
            .userNameAttributeName("login")
            .scope("read:user")
            .clientName("GitHub")
            .build();
    return new InMemoryClientRegistrationRepository(registration);
  }

  @Bean
  public OAuth2AuthorizedClientService authorizedClientService(
      ClientRegistrationRepository repository) {
    return new InMemoryOAuth2AuthorizedClientService(repository);
  }
}
