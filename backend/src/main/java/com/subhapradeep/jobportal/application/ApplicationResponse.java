package com.subhapradeep.jobportal.application;

import com.subhapradeep.jobportal.model.ApplicationStatus;
import com.subhapradeep.jobportal.model.JobApplication;

import java.time.Instant;

/** An application as the API shows it. Leaves out the full resume text. */
public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        String company,
        Long candidateId,
        String candidateName,
        String candidateEmail,
        Integer aiScore,        // filled in by the AI screening
        String aiFeedback,
        ApplicationStatus status,
        Instant appliedAt
) {
    public static ApplicationResponse from(JobApplication a) {
        return new ApplicationResponse(
                a.getId(),
                a.getJob().getId(),
                a.getJob().getTitle(),
                a.getJob().getCompany(),
                a.getCandidate().getId(),
                a.getCandidate().getFullName(),
                a.getCandidate().getEmail(),
                a.getAiScore(),
                a.getAiFeedback(),
                a.getStatus(),
                a.getAppliedAt()
        );
    }
}
