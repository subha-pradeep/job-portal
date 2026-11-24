package com.subhapradeep.jobportal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

// Named JobApplication (not Application) to avoid confusion with Spring's own classes
@Entity
@Table(name = "job_applications",
       uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "candidate_id"})) // one application per job
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id")
    private User candidate;

    // Where the uploaded PDF is stored
    private String resumePath;

    // Text pulled out of the PDF, sent to the AI
    @Lob
    @Column(columnDefinition = "TEXT")
    private String resumeText;

    // AI results: 0-100 match score and a short explanation
    private Integer aiScore;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Builder.Default
    private Instant appliedAt = Instant.now();
}
