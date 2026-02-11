package com.naukri.autoapply.api;

import com.naukri.autoapply.application.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/job-applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PostMapping("/runs")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StartAutoApplyResponse start(@Valid @RequestBody StartAutoApplyRequest request) {
        return service.startAutoApply(request);
    }

    @GetMapping("/runs/{runId}")
    public ApplicationRunResponse getRun(@PathVariable UUID runId) {
        return service.getRun(runId);
    }
}
