package com.subhapradeep.jobportal.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Throw this from services to return a clean error with the right HTTP status. */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
