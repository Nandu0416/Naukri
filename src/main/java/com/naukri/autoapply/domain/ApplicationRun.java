package com.naukri.autoapply.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApplicationRun {
    private final UUID id;
    private final int requestedApplications;
    private final Instant startedAt;
    private final List<String> notes;

    private ApplicationRunStatus status;
    private int submittedApplications;
    private Instant completedAt;

    public ApplicationRun(UUID id, int requestedApplications) {
        this.id = id;
        this.requestedApplications = requestedApplications;
        this.startedAt = Instant.now();
        this.status = ApplicationRunStatus.QUEUED;
        this.notes = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public int getRequestedApplications() {
        return requestedApplications;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public ApplicationRunStatus getStatus() {
        return status;
    }

    public int getSubmittedApplications() {
        return submittedApplications;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public List<String> getNotes() {
        return List.copyOf(notes);
    }

    public void markRunning() {
        this.status = ApplicationRunStatus.RUNNING;
    }

    public void markCompleted(int submittedApplications) {
        this.submittedApplications = submittedApplications;
        this.status = ApplicationRunStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = ApplicationRunStatus.FAILED;
        this.completedAt = Instant.now();
        this.notes.add(reason);
    }

    public void addNote(String note) {
        this.notes.add(note);
    }
}
