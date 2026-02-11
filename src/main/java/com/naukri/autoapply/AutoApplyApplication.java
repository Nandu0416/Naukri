package com.naukri.autoapply;

import com.naukri.autoapply.config.NaukriAutomationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(NaukriAutomationProperties.class)
public class AutoApplyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoApplyApplication.class, args);
    }
}
