package com.subhapradeep.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

// We authenticate with our own JWT filter, so turn off Spring's default in-memory user
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableAsync   // lets AI screening run in the background (@Async)
public class JobPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobPortalApplication.class, args);
    }
}
