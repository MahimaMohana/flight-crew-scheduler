package com.skylink.crewscheduler.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record TripCreateRequest(
        @NotBlank String tripNumber,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotEmpty(message = "A trip needs at least one flight leg") @Valid List<FlightRequest> flights
) {
}
