package com.naukri.autoapply.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StartAutoApplyRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String role,
        @NotEmpty List<@NotBlank String> skills,
        @NotBlank String noticePeriod,
        @NotNull @Min(1) @Max(500) Integer applicationsToSubmit,
        String expectedPackage,
        String location,
        @Min(0) @Max(30) Integer freshnessInDays
) {
}
