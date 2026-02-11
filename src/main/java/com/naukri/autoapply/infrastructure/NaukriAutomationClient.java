package com.naukri.autoapply.infrastructure;

import com.naukri.autoapply.domain.JobSearchCriteria;
import com.naukri.autoapply.domain.NaukriCredentials;

public interface NaukriAutomationClient {

    AutomationRunResult applyToMatchingJobs(NaukriCredentials credentials, JobSearchCriteria criteria);
}
