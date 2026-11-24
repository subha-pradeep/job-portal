package com.subhapradeep.jobportal.repository;

import com.subhapradeep.jobportal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByOpenTrueOrderByCreatedAtDesc();

    List<Job> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);

    List<Job> findByOpenTrueAndTitleContainingIgnoreCase(String keyword);
}
