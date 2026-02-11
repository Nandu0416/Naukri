package com.naukri.autoapply.api;

import com.naukri.autoapply.domain.ApplicationRunStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ApplicationRunResponse(
        UUID runId,
        ApplicationRunStatus status,
        Instant startedAt,
        Instant completedAt,
        int requestedApplications,
        int submittedApplications,
        List<String> notes
) {
}
