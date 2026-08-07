package com.skylink.crewscheduler.dto;

import com.skylink.crewscheduler.model.AssignmentRole;
import com.skylink.crewscheduler.model.AssignmentStatus;
import com.skylink.crewscheduler.model.Flight;
import com.skylink.crewscheduler.model.TripAssignment;

import java.time.LocalDate;
import java.util.List;

/**
 * Flattened, calendar-widget-friendly shape for a single trip assignment.
 * Field names deliberately mirror what a FullCalendar.js "event object"
 * expects (id/title/start/end/color) so the static frontend can bind to it
 * directly without a mapping layer.
 */
public record CalendarEventDto(
        Long id,
        String title,
        LocalDate start,
        LocalDate end,
        String color,
        Long tripId,
        String tripNumber,
        AssignmentRole assignmentRole,
        AssignmentStatus status,
        List<String> flightNumbers
) {

    public static CalendarEventDto from(TripAssignment assignment) {
        var trip = assignment.getTrip();
        List<String> flightNumbers = trip.getFlights().stream()
                .map(Flight::getFlightNumber)
                .toList();

        String title = trip.getTripNumber() + " \u00B7 " + assignment.getAssignmentRole();

        return new CalendarEventDto(
                assignment.getId(),
                title,
                trip.getStartDate(),
                // FullCalendar end dates are exclusive, so add one day to make a
                // trip that ends on the same day it starts still render as a block.
                trip.getEndDate().plusDays(1),
                colorFor(assignment.getStatus()),
                trip.getId(),
                trip.getTripNumber(),
                assignment.getAssignmentRole(),
                assignment.getStatus(),
                flightNumbers
        );
    }

    private static String colorFor(AssignmentStatus status) {
        return switch (status) {
            case ASSIGNED -> "#F2A93B";
            case CONFIRMED -> "#2BB3A3";
            case COMPLETED -> "#7C8AA5";
            case CANCELLED -> "#D9534F";
        };
    }
}
