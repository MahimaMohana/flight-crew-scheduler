package com.skylink.crewscheduler.dto;

import com.skylink.crewscheduler.model.AssignmentRole;
import jakarta.validation.constraints.NotNull;

public record TripAssignmentCreateRequest(
        @NotNull Long crewMemberId,
        @NotNull Long tripId,
        @NotNull AssignmentRole assignmentRole
) {
}
