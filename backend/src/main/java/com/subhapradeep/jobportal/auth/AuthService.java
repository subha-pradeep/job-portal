package com.subhapradeep.jobportal.auth;

import com.subhapradeep.jobportal.common.ApiException;
import com.subhapradeep.jobportal.model.User;
import com.subhapradeep.jobportal.repository.UserRepository;
import com.subhapradeep.jobportal.security.JwtService;
import com.subhapradeep.jobportal.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = User.builder()
                .fullName(request.fullName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))   // store the hash only
                .role(request.role())
                .build();
        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user), UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // Same message for "no such user" and "wrong password", so attackers
        // can't find out which emails are registered
        User user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        return new AuthResponse(jwtService.generateToken(user), UserResponse.from(user));
    }
}
