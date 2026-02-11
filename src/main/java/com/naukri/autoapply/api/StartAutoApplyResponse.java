package com.naukri.autoapply.api;

import com.naukri.autoapply.domain.ApplicationRunStatus;

import java.time.Instant;
import java.util.UUID;

public record StartAutoApplyResponse(
        UUID runId,
        ApplicationRunStatus status,
        Instant startedAt
) {
}
