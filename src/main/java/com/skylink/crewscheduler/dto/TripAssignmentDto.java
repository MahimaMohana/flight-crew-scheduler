package com.skylink.crewscheduler.dto;

import com.skylink.crewscheduler.model.AssignmentRole;
import com.skylink.crewscheduler.model.AssignmentStatus;
import com.skylink.crewscheduler.model.TripAssignment;

import java.time.LocalDate;

public record TripAssignmentDto(
        Long id,
        CrewMemberDto crewMember,
        TripDto trip,
        AssignmentRole assignmentRole,
        AssignmentStatus status,
        LocalDate assignedDate
) {

    public static TripAssignmentDto from(TripAssignment assignment) {
        return new TripAssignmentDto(
                assignment.getId(),
                CrewMemberDto.from(assignment.getCrewMember()),
                TripDto.from(assignment.getTrip()),
                assignment.getAssignmentRole(),
                assignment.getStatus(),
                assignment.getAssignedDate()
        );
    }
}
