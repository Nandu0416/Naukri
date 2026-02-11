package com.naukri.autoapply.infrastructure;

import com.naukri.autoapply.domain.JobSearchCriteria;
import com.naukri.autoapply.domain.NaukriCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name = "automation.mode", havingValue = "simulated")
public class SimulatedNaukriAutomationClient implements NaukriAutomationClient {
    private static final Logger log = LoggerFactory.getLogger(SimulatedNaukriAutomationClient.class);

    @Override
    public AutomationRunResult applyToMatchingJobs(NaukriCredentials credentials, JobSearchCriteria criteria) {
        log.info("Preparing simulated run for user={} role={} skills={}",
                credentials.username(), criteria.role(), criteria.skills());

        int score = criteria.skills().stream()
                .mapToInt(skill -> skill.toLowerCase(Locale.ROOT).contains("java") ? 2 : 1)
                .sum();

        int freshnessBoost = criteria.freshnessInDays() == null ? 1 : Math.max(1, 30 - criteria.freshnessInDays()) / 10;
        int maxLikelyApplications = Math.max(1, score * freshnessBoost + ThreadLocalRandom.current().nextInt(1, 5));
        int submitted = Math.min(criteria.applicationsToSubmit(), maxLikelyApplications);

        return AutomationRunResult.of(submitted, List.of("Simulated mode executed."));
    }
}
