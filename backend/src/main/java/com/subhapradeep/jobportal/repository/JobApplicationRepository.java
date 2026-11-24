package com.subhapradeep.jobportal.repository;

import com.subhapradeep.jobportal.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Recruiter view: applicants for a job, best AI score first
    List<JobApplication> findByJobIdOrderByAiScoreDesc(Long jobId);

    // Candidate view: "my applications"
    List<JobApplication> findByCandidateIdOrderByAppliedAtDesc(Long candidateId);

    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);
}
