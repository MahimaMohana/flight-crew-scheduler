package com.skylink.crewscheduler.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String path,
        List<String> details
) {

    public static ErrorResponse of(int status, String error, String path, List<String> details) {
        return new ErrorResponse(Instant.now(), status, error, path, details);
    }
}
