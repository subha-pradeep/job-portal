package com.subhapradeep.jobportal.job;

import com.subhapradeep.jobportal.common.ApiException;
import com.subhapradeep.jobportal.model.Job;
import com.subhapradeep.jobportal.model.User;
import com.subhapradeep.jobportal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    // ---------- Public ----------

    /** Open jobs, newest first. Optional keyword searches the title. */
    @Transactional(readOnly = true)
    public List<JobResponse> listOpenJobs(String keyword) {
        List<Job> jobs = (keyword == null || keyword.isBlank())
                ? jobRepository.findByOpenTrueOrderByCreatedAtDesc()
                : jobRepository.findByOpenTrueAndTitleContainingIgnoreCase(keyword.trim());
        return jobs.stream().map(JobResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(Long id) {
        return JobResponse.from(findJob(id));
    }

    // ---------- Recruiter ----------

    @Transactional
    public JobResponse createJob(JobRequest request, User recruiter) {
        Job job = Job.builder()
                .recruiter(recruiter)
                .build();
        applyRequest(job, request);
        return JobResponse.from(jobRepository.save(job));
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request, User recruiter) {
        Job job = findOwnedJob(id, recruiter);
        applyRequest(job, request);
        return JobResponse.from(job);   // saved automatically when the transaction commits
    }

    @Transactional
    public JobResponse closeJob(Long id, User recruiter) {
        Job job = findOwnedJob(id, recruiter);
        job.setOpen(false);
        return JobResponse.from(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> listMyJobs(User recruiter) {
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiter.getId())
                .stream().map(JobResponse::from).toList();
    }

    // ---------- Helpers ----------

    Job findJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Job not found"));
    }

    /** A recruiter may only change jobs they posted. */
    private Job findOwnedJob(Long id, User recruiter) {
        Job job = findJob(id);
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only manage your own jobs");
        }
        return job;
    }

    private void applyRequest(Job job, JobRequest request) {
        job.setTitle(request.title().trim());
        job.setCompany(request.company().trim());
        job.setLocation(request.location() == null ? null : request.location().trim());
        job.setDescription(request.description().trim());
        job.setRequiredSkills(request.requiredSkills() == null ? null : request.requiredSkills().trim());
    }
}
