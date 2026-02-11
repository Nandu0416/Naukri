package com.naukri.autoapply.domain;

import java.util.List;

public record JobSearchCriteria(
        String role,
        List<String> skills,
        String noticePeriod,
        String expectedPackage,
        String location,
        Integer freshnessInDays,
        int applicationsToSubmit
) {
}
