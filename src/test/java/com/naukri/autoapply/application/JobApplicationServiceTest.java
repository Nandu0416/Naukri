package com.naukri.autoapply.application;

import com.naukri.autoapply.api.StartAutoApplyRequest;
import com.naukri.autoapply.domain.ApplicationRunStatus;
import com.naukri.autoapply.infrastructure.ApplicationRunRepository;
import com.naukri.autoapply.infrastructure.NaukriAutomationClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

class JobApplicationServiceTest {

    private JobApplicationService service;

    @BeforeEach
    void setUp() {
        ApplicationRunRepository repository = new ApplicationRunRepository();
        NaukriAutomationClient client = (credentials, criteria) -> criteria.applicationsToSubmit();
        Executor sameThreadExecutor = Runnable::run;
        service = new JobApplicationService(repository, client, sameThreadExecutor);
    }

    @Test
    void shouldQueueAndProcessRun() {
        StartAutoApplyRequest request = new StartAutoApplyRequest(
                "user@example.com",
                "password",
                "Java Developer",
                List.of("Java", "Spring Boot"),
                "Immediate",
                3,
                "12 LPA",
                "Bangalore",
                7
        );

        var startResponse = service.startAutoApply(request);
        var runResponse = service.getRun(startResponse.runId());

        assertThat(runResponse.status()).isEqualTo(ApplicationRunStatus.COMPLETED);
        assertThat(runResponse.requestedApplications()).isEqualTo(3);
        assertThat(runResponse.submittedApplications()).isEqualTo(3);
    }
}
