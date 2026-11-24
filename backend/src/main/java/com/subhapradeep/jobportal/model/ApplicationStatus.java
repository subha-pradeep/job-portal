package com.subhapradeep.jobportal.model;

public enum ApplicationStatus {
    APPLIED,       // just submitted
    SCREENED,      // AI has scored the resume
    SHORTLISTED,   // recruiter moved it forward
    REJECTED
}
