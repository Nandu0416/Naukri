package com.naukri.autoapply.application;

import com.naukri.autoapply.api.ApplicationRunResponse;
import com.naukri.autoapply.api.StartAutoApplyRequest;
import com.naukri.autoapply.api.StartAutoApplyResponse;
import com.naukri.autoapply.domain.ApplicationRun;
import com.naukri.autoapply.domain.JobSearchCriteria;
import com.naukri.autoapply.domain.NaukriCredentials;
import com.naukri.autoapply.infrastructure.ApplicationRunRepository;
import com.naukri.autoapply.infrastructure.NaukriAutomationClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.Executor;

@Service
public class JobApplicationService {
    private final ApplicationRunRepository runRepository;
    private final NaukriAutomationClient automationClient;
    private final Executor applicationExecutor;

    public JobApplicationService(
            ApplicationRunRepository runRepository,
            NaukriAutomationClient automationClient,
            @Qualifier("applicationExecutor") Executor applicationExecutor
    ) {
        this.runRepository = runRepository;
        this.automationClient = automationClient;
        this.applicationExecutor = applicationExecutor;
    }

    public StartAutoApplyResponse startAutoApply(StartAutoApplyRequest request) {
        ApplicationRun run = new ApplicationRun(UUID.randomUUID(), request.applicationsToSubmit());
        run.addNote("Accepted request and queued for execution.");
        runRepository.save(run);

        NaukriCredentials credentials = new NaukriCredentials(request.username(), request.password());
        JobSearchCriteria criteria = new JobSearchCriteria(
                request.role(),
                request.skills(),
                request.noticePeriod(),
                request.expectedPackage(),
                request.location(),
                request.freshnessInDays(),
                request.applicationsToSubmit()
        );

        applicationExecutor.execute(() -> executeRun(run.getId(), credentials, criteria));

        return new StartAutoApplyResponse(run.getId(), run.getStatus(), run.getStartedAt());
    }

    void executeRun(UUID runId, NaukriCredentials credentials, JobSearchCriteria criteria) {
        ApplicationRun run = runRepository.findById(runId)
                .orElseThrow(() -> new IllegalStateException("Run not found: " + runId));

        try {
            run.markRunning();
            run.addNote("Searching jobs for role '" + criteria.role() + "'.");

            int submitted = automationClient.applyToMatchingJobs(credentials, criteria);
            run.markCompleted(submitted);
            run.addNote("Run completed successfully.");
        } catch (Exception ex) {
            run.markFailed("Run failed: " + ex.getMessage());
        }
    }

    public ApplicationRunResponse getRun(UUID id) {
        ApplicationRun run = runRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Run not found"));

        return new ApplicationRunResponse(
                run.getId(),
                run.getStatus(),
                run.getStartedAt(),
                run.getCompletedAt(),
                run.getRequestedApplications(),
                run.getSubmittedApplications(),
                run.getNotes()
        );
    }
}
