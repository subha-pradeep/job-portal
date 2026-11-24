package com.subhapradeep.jobportal.user;

import com.subhapradeep.jobportal.model.Role;
import com.subhapradeep.jobportal.model.User;

/** What the API shows about a user. Never includes the password hash. */
public record UserResponse(Long id, String fullName, String email, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
