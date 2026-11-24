package com.subhapradeep.jobportal.auth;

import com.subhapradeep.jobportal.user.UserResponse;

/** Returned after signup or login: the token plus who the user is. */
public record AuthResponse(String token, UserResponse user) {}
