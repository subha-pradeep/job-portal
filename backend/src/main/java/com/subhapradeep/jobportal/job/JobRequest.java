package com.subhapradeep.jobportal.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body for creating or editing a job. */
public record JobRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 150) String company,
        @Size(max = 150) String location,
        @NotBlank @Size(min = 30, message = "should describe the role in at least 30 characters") String description,
        @Size(max = 500) String requiredSkills   // e.g. "Java, Spring Boot, Docker"
) {}
