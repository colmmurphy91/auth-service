package com.cluborg.authservice.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadCredentialsException extends ResponseStatusException {

    public BadCredentialsException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
