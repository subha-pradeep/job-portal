package com.subhapradeep.jobportal.auth;

import com.subhapradeep.jobportal.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, message = "must be at least 6 characters") String password,
        @NotNull Role role
) {}
