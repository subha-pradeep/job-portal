package com.subhapradeep.jobportal.ai;

/** Published when a candidate applies. The screening service listens for it. */
public record ApplicationSubmittedEvent(Long applicationId) {}
