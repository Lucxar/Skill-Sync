package wiegand.luca.ThesisIsComing.presentationDriving.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import wiegand.luca.ThesisIsComing.ApplicationService.BetreuerService;
import wiegand.luca.ThesisIsComing.ApplicationService.security.SecurityRoles;

import java.util.HashSet;
import java.util.Set;

public class GithubOAuth2UserLoader implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final BetreuerService betreuerService;
    private final SecurityProperties securityProperties;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    public GithubOAuth2UserLoader(BetreuerService betreuerService, SecurityProperties securityProperties) {
        this.betreuerService = betreuerService;
        this.securityProperties = securityProperties;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauthUser = delegate.loadUser(userRequest);
        String login = (String) oauthUser.getAttributes().get("login");

        Set<GrantedAuthority> authorities = new HashSet<>(oauthUser.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(SecurityRoles.ROLE_STUDIERENDE));

        if (login != null && securityProperties.getAdminLogins().contains(login)) {
            authorities.add(new SimpleGrantedAuthority(SecurityRoles.ROLE_ADMIN));
        }
        if (login != null && betreuerService.findByGithubLogin(login).isPresent()) {
            authorities.add(new SimpleGrantedAuthority(SecurityRoles.ROLE_BETREUER));
        }

        return new DefaultOAuth2User(authorities, oauthUser.getAttributes(), "login");
    }
}
