package com.subhapradeep.jobportal.ai;

import com.subhapradeep.jobportal.common.ApiException;
import com.subhapradeep.jobportal.model.ApplicationStatus;
import com.subhapradeep.jobportal.model.Job;
import com.subhapradeep.jobportal.model.JobApplication;
import com.subhapradeep.jobportal.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ResumeScreener screener;
    private final JobApplicationRepository applicationRepository;

    /**
     * Runs automatically after a candidate applies:
     * - AFTER_COMMIT: only once the application is really saved in the database
     * - @Async: on a background thread, so the candidate gets a fast response and
     *   a slow or failing AI call never breaks applying
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onApplicationSubmitted(ApplicationSubmittedEvent event) {
        if (!screener.isEnabled()) {
            log.info("AI screening skipped for application {}: GEMINI_API_KEY is not set", event.applicationId());
            return;
        }
        try {
            applicationRepository.findById(event.applicationId()).ifPresent(this::screenAndSave);
        } catch (Exception e) {
            // Leave it as APPLIED; the recruiter can retry with the "screen" endpoint
            log.warn("AI screening failed for application {}: {}", event.applicationId(), e.getMessage());
        }
    }

    /** Manual (re)screening, e.g. after adding the API key. Runs right away. */
    @Transactional
    public JobApplication screenNow(JobApplication application) {
        if (!screener.isEnabled()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI screening is not configured (GEMINI_API_KEY missing)");
        }
        try {
            screenAndSave(application);
            return application;
        } catch (Exception e) {
            log.warn("AI screening failed for application {}: {}", application.getId(), e.getMessage());
            throw new ApiException(HttpStatus.BAD_GATEWAY, "The AI service failed. Please try again in a minute.");
        }
    }

    private void screenAndSave(JobApplication application) {
        Job job = application.getJob();
        ScreeningResult result = screener.screen(
                job.getTitle(), job.getDescription(), job.getRequiredSkills(), application.getResumeText());

        application.setAiScore(result.score());
        application.setAiFeedback(result.toFeedbackText());
        // Don't undo a decision the recruiter already made
        if (application.getStatus() == ApplicationStatus.APPLIED) {
            application.setStatus(ApplicationStatus.SCREENED);
        }
        applicationRepository.save(application);
        log.info("Application {} scored {}/100", application.getId(), result.score());
    }
}
