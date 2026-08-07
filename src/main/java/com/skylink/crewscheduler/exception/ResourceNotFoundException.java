package com.skylink.crewscheduler.exception;

/**
 * Thrown when a requested entity does not exist. Translated to a 404 by
 * {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
