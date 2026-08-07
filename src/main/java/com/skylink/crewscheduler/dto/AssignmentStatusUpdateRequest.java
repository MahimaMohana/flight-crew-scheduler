package com.skylink.crewscheduler.dto;

import com.skylink.crewscheduler.model.AssignmentStatus;
import jakarta.validation.constraints.NotNull;

public record AssignmentStatusUpdateRequest(
        @NotNull AssignmentStatus status
) {
}
