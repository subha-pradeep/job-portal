package com.subhapradeep.jobportal.application;

import com.subhapradeep.jobportal.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

/** Recruiter's decision: SHORTLISTED or REJECTED. */
public record StatusUpdateRequest(@NotNull ApplicationStatus status) {}
