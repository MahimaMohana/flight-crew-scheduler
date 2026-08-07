package com.skylink.crewscheduler.dto;

import com.skylink.crewscheduler.model.CrewRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrewMemberCreateRequest(
        @NotBlank String employeeId,
        @NotBlank String username,
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotNull CrewRole crewRole,
        @NotBlank String baseAirport,
        Integer seniorityNumber
) {
}
