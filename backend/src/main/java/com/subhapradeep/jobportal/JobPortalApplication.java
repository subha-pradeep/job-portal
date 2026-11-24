package com.subhapradeep.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// We authenticate with our own JWT filter, so turn off Spring's default in-memory user
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class JobPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalApplication.class, args);
    }
}
