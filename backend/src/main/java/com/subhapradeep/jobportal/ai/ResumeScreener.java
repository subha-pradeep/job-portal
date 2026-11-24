package com.subhapradeep.jobportal.ai;

/**
 * Scores a resume against a job.
 * An interface, so the AI provider can be swapped (Gemini today, OpenAI or a
 * local model later) without changing any other code.
 */
public interface ResumeScreener {

    /** False when the provider isn't configured (e.g. no API key). */
    boolean isEnabled();

    ScreeningResult screen(String jobTitle, String jobDescription, String requiredSkills, String resumeText);
}
