package com.cluborg.authservice.security.exception;

/**
 * @author duc-d
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
