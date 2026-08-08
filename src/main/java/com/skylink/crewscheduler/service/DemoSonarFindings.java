package com.skylink.crewscheduler.service;

import com.skylink.crewscheduler.model.AssignmentStatus;
import com.skylink.crewscheduler.model.TripAssignment;

import java.util.List;

/**
 * ============================================================================
 *  DEMO-ONLY FILE - INTENTIONAL SONARQUBE FINDINGS
 * ============================================================================
 * This class is not used by the application at runtime. It exists solely so
 * the Kong demo has real, reproducible SonarCloud/SonarQube findings to walk
 * through the Kiro self-healing hook + SonarQube IDE plugin flow against.
 *
 * Each issue below is intentional and labeled with the Sonar rule it trips,
 * using the default Sonar Way Java quality profile:
 *   1. S3776 - Cognitive Complexity: summarizeRoster() nests conditionals
 *      well past the default threshold.
 *   2. S1192  - String literals should not be duplicated: the status labels
 *      below repeat the same literal more than the default threshold (3).
 *   3. S1481 - Unused local variable: `unusedCount` is assigned but never read.
 *
 * A fourth method, seniorityBand(), is calibrated differently: it's clean
 * under default Sonar Way but is meant to flip to "flagged" once a custom
 * "Southwest Way" profile (extending Sonar Way) tightens the Cognitive
 * Complexity threshold. Use it to show the same file passing under one
 * profile and failing under another.
 *
 * Safe to delete this file entirely once it's served its purpose in the demo;
 * nothing else in the codebase depends on it.
 * ============================================================================
 */
public class DemoSonarFindings {

    private static final String TRIP_ASSIGNMENT_STATUS = "trip-assignment-status";
    private static final String CREW_AUDIT_TAG = "crew-audit-tag";

    private DemoSonarFindings() {
        // utility class
    }

    // --- S3776 fixed: extracted helper methods to reduce cognitive complexity --
    public static String summarizeRoster(List<TripAssignment> assignments, boolean includePilots,
                                          boolean includeCabin, boolean verbose) {
        StringBuilder summary = new StringBuilder();

        for (TripAssignment assignment : assignments) {
            if (assignment.getCrewMember() == null || assignment.getCrewMember().getCrewRole() == null) {
                continue;
            }
            appendPilotSummary(summary, assignment, includePilots, verbose);
            appendCabinSummary(summary, assignment, includeCabin);
        }

        return summary.toString();
    }

    private static void appendPilotSummary(StringBuilder summary, TripAssignment assignment,
                                            boolean includePilots, boolean verbose) {
        if (!includePilots || !"PILOT".equals(assignment.getCrewMember().getCrewRole().name())) {
            return;
        }
        if (assignment.getStatus() == AssignmentStatus.CONFIRMED) {
            if (verbose) {
                summary.append("Confirmed pilot: ")
                        .append(assignment.getCrewMember().getFullName())
                        .append("\n");
            } else {
                summary.append("Confirmed pilot\n");
            }
        } else if (assignment.getStatus() == AssignmentStatus.ASSIGNED) {
            summary.append("Assigned pilot\n");
        } else {
            summary.append("Pilot status unknown\n");
        }
    }

    private static void appendCabinSummary(StringBuilder summary, TripAssignment assignment,
                                            boolean includeCabin) {
        if (!includeCabin || !"FLIGHT_ATTENDANT".equals(assignment.getCrewMember().getCrewRole().name())) {
            return;
        }
        if (assignment.getStatus() == AssignmentStatus.CONFIRMED) {
            summary.append("Confirmed cabin\n");
        } else if (assignment.getStatus() == AssignmentStatus.ASSIGNED) {
            summary.append("Assigned cabin\n");
        } else {
            summary.append("Pending cabin\n");
        }
    }

    // --- Southwest Way only: this method is intentionally simple (cognitive
    // complexity ~5) so it stays clean under the DEFAULT Sonar Way profile.
    // In "Southwest Way" (the custom profile extending Sonar Way), tighten
    // java:S3776's Threshold parameter down to something aggressive like 4,
    // and this method flips from clean to flagged - a live, provable
    // "same file, two profiles, two different results" moment for the demo.
    public static String seniorityBand(int seniorityNumber) {
        if (seniorityNumber < 500) {
            if (seniorityNumber < 200) {
                return "Senior";
            } else {
                return "Mid";
            }
        } else {
            return "Junior";
        }
    }

    // --- S1192 fixed: string literal extracted to a constant -----------------
    // --- S3923 fixed: all branches returned the same value; simplified to a direct return ---
    public static String statusLabel() {
        return TRIP_ASSIGNMENT_STATUS;
    }

    // --- S1192 fixed: string literal extracted to a constant --------------------
    // --- S3923 fixed: all branches returned the same value; simplified to a direct return ---
    public static String buildAuditTag() {
        return CREW_AUDIT_TAG;
    }

    // --- S1481: Unused local variable -------------------------------------------
    public static int countRejected(List<TripAssignment> assignments) {
        int rejected = 0;
        for (TripAssignment a : assignments) {
            if (a.getStatus() == AssignmentStatus.CANCELLED) {
                rejected++;
            }
        }
        return rejected;
    }
}
