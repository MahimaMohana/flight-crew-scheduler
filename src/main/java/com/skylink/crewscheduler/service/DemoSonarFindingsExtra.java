package com.skylink.crewscheduler.service;

import com.skylink.crewscheduler.model.AssignmentStatus;
import com.skylink.crewscheduler.model.CrewMember;
import com.skylink.crewscheduler.model.Trip;
import com.skylink.crewscheduler.model.TripAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DEMO-ONLY — INTENTIONAL SONARQUBE FINDINGS (EXTRA SET), now fixed.
 * Safe to delete after the demo. Nothing in the application depends on this class.
 */
public class DemoSonarFindingsExtra {

    private DemoSonarFindingsExtra() {
        // utility class — no instantiation
    }

    private static final Logger logger = LoggerFactory.getLogger(DemoSonarFindingsExtra.class);

    public static void logTripStart(Trip trip) {
        logger.info("Trip started: {}", trip.getTripNumber());
    }

    // --- S1172 fixed: removed unused parameter ----------------------------------
    public static String formatCrewId(String id) {
        return "CREW-" + id.toUpperCase();
    }

    // --- S2583 fixed: removed always-true condition and dead else branch ---------
    public static String alwaysTrue(AssignmentStatus status) {
        return status.name();
    }

    // --- S1168 fixed: return Collections.emptyList() instead of null ------------
    public static List<Long> getActiveCrewIds(List<TripAssignment> assignments) {
        if (assignments == null) {
            return Collections.emptyList();
        }
        List<Long> ids = new ArrayList<>();
        for (TripAssignment a : assignments) {
            if (a.getStatus() == AssignmentStatus.CONFIRMED) {
                ids.add(a.getCrewMember().getId());
            }
        }
        return ids;
    }

    // --- S112 fixed: throw specific IllegalArgumentException instead of RuntimeException ---
    public static void validateSeniority(CrewMember crew) {
        if (crew.getSeniorityNumber() == null) {
            throw new IllegalArgumentException("Seniority number is required");
        }
    }

    // --- S1854 fixed: removed dead store; compute result directly ---------------
    public static int computeLayoverHours(Trip trip) {
        int hours = trip.getEndDate().getDayOfYear() - trip.getStartDate().getDayOfYear();
        return hours * 24;
    }

    // --- S2201 fixed: use the return value of trim() ----------------------------
    public static String normalizeAirportCode(String code) {
        return code.trim().toUpperCase();
    }

    // --- S1698 fixed: use equals() instead of == for String comparison ----------
    public static boolean isSameTrip(Trip a, Trip b) {
        return a.getTripNumber().equals(b.getTripNumber());
    }

    // --- S1874 fixed: replaced deprecated java.util.Date with java.time.Instant -
    public static String legacyCrewFormat(CrewMember crew) {
        Instant assignmentInstant = Instant.now();
        return crew.getEmployeeId() + "@" + assignmentInstant;
    }

    // --- S2203 fixed: use collect() instead of forEach(list::add) ---------------
    public static List<Long> collectPilotIds(List<TripAssignment> assignments) {
        return assignments.stream()
                .filter(a -> a.getAssignmentRole() != null)
                .map(a -> a.getCrewMember().getId())
                .collect(Collectors.toList());
    }

    // --- S2221 fixed: catch only NumberFormatException, which is what parseInt throws ---
    public static int parseSeniority(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // --- S2259 fixed: guard against null before calling toUpperCase() ------------
    public static String getCrewBadge(CrewMember crew) {
        if (crew.getCrewRole() == null) {
            return "UNKNOWN";
        }
        return crew.getCrewRole().name().toUpperCase();
    }

    // --- S1066 fixed: merged nested if statements into a single compound condition -
    public static String resolvePriority(CrewMember crew) {
        if (crew.getSeniorityNumber() != null && crew.getSeniorityNumber() < 100) {
            return "HIGH";
        }
        return "NORMAL";
    }
}
