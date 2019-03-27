package com.cluborg.authservice.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserDoesNotExistException extends ResponseStatusException {

    public UserDoesNotExistException(HttpStatus badRequest, String format) {
        super(badRequest, format);
    }
}
