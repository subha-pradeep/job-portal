package com.subhapradeep.jobportal.job;

import com.subhapradeep.jobportal.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // ---------- Public: anyone can browse ----------

    @GetMapping("/api/jobs")
    public List<JobResponse> listJobs(@RequestParam(required = false) String keyword) {
        return jobService.listOpenJobs(keyword);
    }

    @GetMapping("/api/jobs/{id}")
    public JobResponse getJob(@PathVariable Long id) {
        return jobService.getJob(id);
    }

    // ---------- Recruiters only ----------

    @PostMapping("/api/jobs")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('RECRUITER')")
    public JobResponse createJob(@Valid @RequestBody JobRequest request,
                                 @AuthenticationPrincipal User recruiter) {
        return jobService.createJob(request, recruiter);
    }

    @PutMapping("/api/jobs/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public JobResponse updateJob(@PathVariable Long id,
                                 @Valid @RequestBody JobRequest request,
                                 @AuthenticationPrincipal User recruiter) {
        return jobService.updateJob(id, request, recruiter);
    }

    @PatchMapping("/api/jobs/{id}/close")
    @PreAuthorize("hasRole('RECRUITER')")
    public JobResponse closeJob(@PathVariable Long id,
                                @AuthenticationPrincipal User recruiter) {
        return jobService.closeJob(id, recruiter);
    }

    /** Every job this recruiter has posted, open or closed. */
    @GetMapping("/api/recruiter/jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<JobResponse> myJobs(@AuthenticationPrincipal User recruiter) {
        return jobService.listMyJobs(recruiter);
    }
}
