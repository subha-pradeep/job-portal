package com.subhapradeep.jobportal.application;

import com.subhapradeep.jobportal.common.ApiException;
import com.subhapradeep.jobportal.model.ApplicationStatus;
import com.subhapradeep.jobportal.model.Job;
import com.subhapradeep.jobportal.model.JobApplication;
import com.subhapradeep.jobportal.model.User;
import com.subhapradeep.jobportal.repository.JobApplicationRepository;
import com.subhapradeep.jobportal.repository.JobRepository;
import com.subhapradeep.jobportal.storage.ResumeStorageService;
import com.subhapradeep.jobportal.storage.ResumeTextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    // The only decisions a recruiter can make by hand
    private static final Set<ApplicationStatus> RECRUITER_DECISIONS =
            Set.of(ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED);

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ResumeStorageService storageService;
    private final ResumeTextExtractor textExtractor;

    // ---------- Candidate ----------

    @Transactional
    public ApplicationResponse apply(Long jobId, MultipartFile resume, User candidate) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Job not found"));

        if (!job.isOpen()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This job is no longer accepting applications");
        }
        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "You have already applied to this job");
        }

        byte[] content = readBytes(resume);
        String resumeText = textExtractor.extract(content);          // fails early on scanned/broken PDFs
        String storedName = storageService.store(resume, content);   // checks it's a PDF, then saves it

        JobApplication application = JobApplication.builder()
                .job(job)
                .candidate(candidate)
                .resumePath(storedName)
                .resumeText(resumeText)
                .build();
        applicationRepository.save(application);

        // Step 5: the AI will score the resume here

        return ApplicationResponse.from(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> myApplications(User candidate) {
        return applicationRepository.findByCandidateIdOrderByAppliedAtDesc(candidate.getId())
                .stream().map(ApplicationResponse::from).toList();
    }

    // ---------- Recruiter ----------

    /** Applicants for one of my jobs, highest AI score first. */
    @Transactional(readOnly = true)
    public List<ApplicationResponse> applicantsForJob(Long jobId, User recruiter) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Job not found"));
        checkOwner(job, recruiter);

        return applicationRepository.findByJobIdOrderByAiScoreDesc(jobId)
                .stream().map(ApplicationResponse::from).toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, User recruiter) {
        if (!RECRUITER_DECISIONS.contains(status)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Status must be SHORTLISTED or REJECTED");
        }
        JobApplication application = findOwnedApplication(applicationId, recruiter);
        application.setStatus(status);
        return ApplicationResponse.from(application);
    }

    @Transactional(readOnly = true)
    public Resource resumeFile(Long applicationId, User recruiter) {
        JobApplication application = findOwnedApplication(applicationId, recruiter);
        return storageService.load(application.getResumePath());
    }

    // ---------- Helpers ----------

    private JobApplication findOwnedApplication(Long applicationId, User recruiter) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Application not found"));
        checkOwner(application.getJob(), recruiter);
        return application;
    }

    /** Recruiters only see applications for jobs they posted. */
    private void checkOwner(Job job, User recruiter) {
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only view applicants for your own jobs");
        }
    }

    private byte[] readBytes(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please attach your resume as a PDF");
        }
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Could not read the uploaded file");
        }
    }
}
