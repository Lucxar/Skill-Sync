package wiegand.luca.ThesisIsComing.presentationDriving.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private final List<String> adminLogins = new ArrayList<>();

    public List<String> getAdminLogins() {
        return adminLogins;
    }
}
