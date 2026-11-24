package com.subhapradeep.jobportal.application;

import com.subhapradeep.jobportal.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // ---------- Candidates ----------

    /** Multipart upload: form field "resume" holds the PDF. */
    @PostMapping(value = "/api/jobs/{jobId}/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ApplicationResponse apply(@PathVariable Long jobId,
                                     @RequestParam("resume") MultipartFile resume,
                                     @AuthenticationPrincipal User candidate) {
        return applicationService.apply(jobId, resume, candidate);
    }

    @GetMapping("/api/candidate/applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    public List<ApplicationResponse> myApplications(@AuthenticationPrincipal User candidate) {
        return applicationService.myApplications(candidate);
    }

    // ---------- Recruiters ----------

    @GetMapping("/api/recruiter/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<ApplicationResponse> applicants(@PathVariable Long jobId,
                                                @AuthenticationPrincipal User recruiter) {
        return applicationService.applicantsForJob(jobId, recruiter);
    }

    @PatchMapping("/api/recruiter/applications/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ApplicationResponse updateStatus(@PathVariable Long id,
                                            @Valid @RequestBody StatusUpdateRequest request,
                                            @AuthenticationPrincipal User recruiter) {
        return applicationService.updateStatus(id, request.status(), recruiter);
    }

    /** Opens the candidate's resume PDF in the browser. */
    @GetMapping("/api/recruiter/applications/{id}/resume")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Resource> resume(@PathVariable Long id,
                                           @AuthenticationPrincipal User recruiter) {
        Resource file = applicationService.resumeFile(id, recruiter);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"resume-" + id + ".pdf\"")
                .body(file);
    }
}
