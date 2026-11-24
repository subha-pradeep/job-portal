package com.subhapradeep.jobportal.job;

import com.subhapradeep.jobportal.model.Job;

import java.time.Instant;

/** What the API returns for a job. Keeps the entity (and recruiter's details) out of the JSON. */
public record JobResponse(
        Long id,
        String title,
        String company,
        String location,
        String description,
        String requiredSkills,
        Long recruiterId,
        String recruiterName,
        boolean open,
        Instant createdAt
) {
    public static JobResponse from(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getDescription(),
                job.getRequiredSkills(),
                job.getRecruiter().getId(),
                job.getRecruiter().getFullName(),
                job.isOpen(),
                job.getCreatedAt()
        );
    }
}
