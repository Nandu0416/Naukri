package com.naukri.autoapply.infrastructure;

import java.util.List;

public record AutomationRunResult(
        int submittedApplications,
        List<String> notes
) {
    public static AutomationRunResult of(int submittedApplications, List<String> notes) {
        return new AutomationRunResult(submittedApplications, notes);
    }
}
