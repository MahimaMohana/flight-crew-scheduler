package com.skylink.crewscheduler.exception;

/**
 * Thrown when an operation would violate a uniqueness rule (e.g. a duplicate
 * username or double-booking a crew member on the same trip). Translated to
 * a 409 by {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
