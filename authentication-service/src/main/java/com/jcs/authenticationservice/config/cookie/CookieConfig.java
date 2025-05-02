package com.jcs.authenticationservice.config.cookie;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cookies")
@Getter
@Setter
public class CookieConfig {
    private boolean enabled;
    private Defaults defaults;

    @Getter @Setter
    public static class Defaults {
        private boolean secure;
        private boolean httpOnly;
        private String path;
        private SameSite sameSite;
        private String domain;
    }
    public enum SameSite {
        STRICT, LAX, NONE;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}