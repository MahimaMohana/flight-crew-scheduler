package com.skylink.crewscheduler.model;

/**
 * Operational status of a {@link Trip} (a pairing of one or more flight legs).
 */
public enum TripStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
