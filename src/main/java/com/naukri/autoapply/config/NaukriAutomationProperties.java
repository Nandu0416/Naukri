package com.naukri.autoapply.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "automation.naukri")
public record NaukriAutomationProperties(
        String loginUrl,
        String searchUrl,
        boolean headless,
        int implicitWaitSeconds,
        int pageLoadTimeoutSeconds
) {
}
