package com.subhapradeep.jobportal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String company;

    private String location;

    // The full job description; the AI compares resumes against this text
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Comma-separated, e.g. "Java,Spring Boot,Docker"
    private String requiredSkills;

    // The recruiter who posted this job
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruiter_id")
    private User recruiter;

    @Builder.Default
    @Column(name = "is_open")   // "open" is a SQL keyword, so use a safer column name
    private boolean open = true;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
