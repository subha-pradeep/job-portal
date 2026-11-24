package com.subhapradeep.jobportal.ai;

import java.util.List;

/** What the AI returns for one resume. */
public record ScreeningResult(
        int score,                  // 0-100: how well the resume fits the job
        String summary,             // 2-3 sentence explanation for the recruiter
        List<String> matchedSkills,
        List<String> missingSkills
) {
    /** Readable text stored in JobApplication.aiFeedback. */
    public String toFeedbackText() {
        return summary
                + "\n\nMatched skills: " + joinOrNone(matchedSkills)
                + "\nMissing skills: " + joinOrNone(missingSkills);
    }

    private static String joinOrNone(List<String> skills) {
        return (skills == null || skills.isEmpty()) ? "none" : String.join(", ", skills);
    }
}
