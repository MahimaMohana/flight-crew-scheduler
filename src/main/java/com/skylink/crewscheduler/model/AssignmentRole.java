package com.skylink.crewscheduler.model;

/**
 * The specific duty a crew member holds for a given trip. Distinct from
 * {@link CrewRole}: a pilot is assigned as CAPTAIN or FIRST_OFFICER, a flight
 * attendant is assigned as PURSER or FLIGHT_ATTENDANT.
 */
public enum AssignmentRole {
    CAPTAIN,
    FIRST_OFFICER,
    PURSER,
    FLIGHT_ATTENDANT
}
